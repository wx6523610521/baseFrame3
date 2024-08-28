package work.chncyl.base.security;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import work.chncyl.base.global.tools.result.ApiResult;
import work.chncyl.base.security.entity.LoginSuccessVo;
import work.chncyl.base.security.entity.LoginUserDetail;
import work.chncyl.base.security.utils.JwtUtil;

import java.io.PrintWriter;

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
            LoginSuccessVo vo = BeanUtil.copyProperties(userDetail, LoginSuccessVo.class, new String[0]);
            vo.setUserName(userDetail.getUsername());
            vo.setAccessToken(token);
            ApiResult<LoginSuccessVo> ok = ApiResult.OK(vo);
            writer.write(JSON.toJSONString(ok));
            writer.flush();
            writer.close();
        };
    }

    /**
     * 登出处理器
     */
    public static LogoutHandler logoutHandler() {
        return (request, response, authentication) -> {
            JwtUtil.lapsedToken();
        };
    }

    /**
     * 登出成功处理器
     */
    public static LogoutSuccessHandler logoutSuccessHandler() {
        // 登出成功处理器
        return (request, response, authentication) -> {

        };
    }
}
