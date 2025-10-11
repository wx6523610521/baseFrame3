package work.chncyl.base.global.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import work.chncyl.base.global.annotation.DisabledInterface;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

/**
 * 禁用接口过滤器
 * 据DisabledInterface注解配置，在指定环境下访问被注解的接口直接返回404
 */
public class DisabledInterfaceFilter implements Filter {

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    public DisabledInterfaceFilter(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            // 获取当前请求的处理器
            HandlerExecutionChain handlerExecutionChain = requestMappingHandlerMapping.getHandler(httpRequest);
            
            if (handlerExecutionChain != null && handlerExecutionChain.getHandler() instanceof HandlerMethod) {
                HandlerMethod handlerMethod = (HandlerMethod) handlerExecutionChain.getHandler();
                
                // 检查方法上是否有DisabledInterface注解
                DisabledInterface methodAnnotation = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), DisabledInterface.class);
                
                // 查类上是否有DisabledInterface注解
                DisabledInterface classAnnotation = AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), DisabledInterface.class);
                
                // 如果方法或类上有DisabledInterface注解，则检查是否应该禁用
                if (methodAnnotation != null && shouldDisableInterface(methodAnnotation)) {
                    httpResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                
                if (classAnnotation != null && shouldDisableInterface(classAnnotation)) {
                    httpResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
            }
        } catch (Exception e) {
            // 如果出现异常，继续执行后续过滤器
        }
        
        chain.doFilter(request, response);
    }

    /**
     * 判断当前环境是否应该禁用接口
     * @param disabledInterface 注解信息
     * @return 是否应该禁用
     */
    private boolean shouldDisableInterface(DisabledInterface disabledInterface) {
        // 获取适用环境和排除环境
        String[] environments = disabledInterface.environment();
        String[] excludeEnvironments = disabledInterface.excludeEnvironment();
        
        // 如果没有指定环境，默认匹配全部环境
        if (environments.length == 0 || (environments.length == 1 && "".equals(environments[0]))) {
            environments = new String[]{""}; // 匹配所有环境
        }
        
        // 如果没有激活的环境配置，默认为空字符串
        if (activeProfile == null) {
            activeProfile = "";
        }
        
        // 检查是否在排除环境中
        if (Arrays.asList(excludeEnvironments).contains(activeProfile)) {
            return false; // 在排除环境中，不禁用
        }
        
        // 查是否在适用环境中
        if (environments.length == 1 && "".equals(environments[0])) {
            // 空数组或包含空字符串，表示匹配所有环境（除了排除的）
            return true;
        } else {
            // 查当前环境是否在适用环境中
            return Arrays.asList(environments).contains(activeProfile);
        }
    }
}
