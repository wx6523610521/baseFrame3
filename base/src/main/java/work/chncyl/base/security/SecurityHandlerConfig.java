package work.chncyl.base.security;

import com.alibaba.fastjson2.JSON;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import work.chncyl.base.global.tools.result.ApiResult;
import work.chncyl.base.security.entity.LoginSuccessVo;
import work.chncyl.base.security.entity.LoginUserDetail;
import work.chncyl.base.security.utils.JwtUtil;

import java.io.PrintWriter;

/**
 * 安全处理器配置类
 */
@Configuration
public class SecurityHandlerConfig {
    /**
     * 登录成功处理器
     */
    public static AuthenticationSuccessHandler loginSuccessHandler() {
        return (request, response, authentication) -> {
            LoginUserDetail userDetail = (LoginUserDetail) authentication.getPrincipal();
            String token = JwtUtil.genToken(userDetail);
            response.setStatus(200);
            response.setContentType("application/json");
            PrintWriter writer = response.getWriter();
            LoginSuccessVo vo = new LoginSuccessVo();
            BeanUtils.copyProperties(userDetail, vo);
            vo.setUserName(userDetail.getUsername());
            vo.setAccessToken(token);
            ApiResult<LoginSuccessVo> ok = ApiResult.OK(vo);
            writer.write(JSON.toJSONString(ok));
            writer.flush();
            writer.close();
        };
    }

    /**
     * 登录失败处理器
     */
    public static AuthenticationFailureHandler loginFailureHandler() {
        return (request, response, exception) -> {
            response.setStatus(500);
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter writer = response.getWriter();
            String message = "登录失败";
            if (exception instanceof BadCredentialsException) {
                message = "用户名或密码错误";
            } else {
                message = exception.getMessage();
            }
            ApiResult<Object> error = ApiResult.error401(message);
            writer.write(JSON.toJSONString(error));
            writer.flush();
            writer.close();
        };
    }

    /**
     * 登出处理器
     */
    public static LogoutHandler logoutHandler() {
        return (request, response, authentication) -> JwtUtil.lapsedToken();
    }

    /**
     * 登出成功处理器
     */
    public static LogoutSuccessHandler logoutSuccessHandler() {
        return (request, response, authentication) -> {
        };
    }
}
