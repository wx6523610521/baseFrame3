package work.chncyl.base.security.interceptor;

import com.alibaba.fastjson2.JSON;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import work.chncyl.base.global.tools.SpringUtils;
import work.chncyl.base.global.tools.result.ApiResult;
import work.chncyl.base.security.annotation.DisabledInterface;
import work.chncyl.base.security.annotation.DisabledInterfaceInTime;

import java.io.PrintWriter;
import java.time.LocalTime;

@Component
public class CustomAccessInterceptor implements HandlerInterceptor {

    @Override
    /**
     * preHandle方法是拦截器的前置处理方法
     * 在请求处理之前进行调用，用于进行预处理逻辑
     * @param request 当前HTTP请求对象
     * @param response 当前HTTP响应对象
     * @param handler 请求处理的方法对象
     * @return 返回true表示继续流程，返回false表示终端流程
     * @throws Exception 可能抛出的异常
     */
    public boolean preHandle(@NotNull HttpServletRequest request
            , @NotNull HttpServletResponse response
            , @NotNull Object handler) throws Exception {
        // 检查处理器是否为HandlerMethod类型，如果不是则直接放行
        if (!(handler instanceof HandlerMethod hm)) {
            return true; // 非控制器请求直接放行
        }

        // 检查方法是否有DisabledInterface注解
        if (hm.hasMethodAnnotation(DisabledInterface.class)) {
            DisabledInterface annotation = hm.getMethodAnnotation(DisabledInterface.class);
            if (annotation != null) {
                String env = annotation.env();
                // 检查环境配置是否不为空
                if (StringUtils.isNotBlank(env)) {
                    // 获取环境配置属性值
                    String property = SpringUtils.getEnvironmentProperty(env);
                    // 如果属性值存在且为true，则返回404错误
                    if (StringUtils.isNotBlank(property)
                            && Boolean.parseBoolean(property)) {
                        writeJsonResponse(response, HttpStatus.NOT_FOUND.value(), "接口已禁用");
                        return false;
                    }
                } else {
                    writeJsonResponse(response, HttpStatus.NOT_FOUND.value(), "接口已禁用");
                    return false;
                }
            }
        }

        // 检查方法是否有DisabledInterfaceInTime注解
        if (hm.hasMethodAnnotation(DisabledInterfaceInTime.class)) {
            DisabledInterfaceInTime annotation = hm.getMethodAnnotation(DisabledInterfaceInTime.class);
            if (annotation != null) {
                String begainTime = annotation.begainTime();
                String endTime = annotation.endTime();
                // 解析开始和结束时间
                boolean inDisabledPeriod = isInDisabledPeriod(begainTime, endTime);

                if (inDisabledPeriod) {
                    writeJsonResponse(response, HttpStatus.NOT_FOUND.value(),
                            String.format("当前时间在禁用时间段内 (%s-%s)", begainTime, endTime)
                    );
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean isInDisabledPeriod(String begainTime, String endTime) {
        LocalTime begin = LocalTime.parse(begainTime);
        LocalTime end = LocalTime.parse(endTime);
        LocalTime now = LocalTime.now();

        // 判断当前时间是否在禁用时间段内
        boolean inDisabledPeriod;
        if (begin.isBefore(end)) {
            // 正常情况：如 09:00-18:00，禁用时间段不跨天
            inDisabledPeriod = !now.isBefore(begin) && !now.isAfter(end);
        } else {
            // 跨天情况：如 21:00-09:00，禁用时间段跨天
            inDisabledPeriod = !now.isBefore(begin) || !now.isAfter(end);
        }
        return inDisabledPeriod;
    }

    /**
     * 写入JSON响应
     */
    private void writeJsonResponse(HttpServletResponse response, int statusCode, String message) throws Exception {
        response.setStatus(statusCode);
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        ApiResult<Object> error = ApiResult.error(statusCode, message);
        writer.write(JSON.toJSONString(error));
        writer.flush();
        writer.close();
    }
}