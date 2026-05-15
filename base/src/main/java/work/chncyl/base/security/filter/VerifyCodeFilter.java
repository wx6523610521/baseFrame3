package work.chncyl.base.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.GenericFilterBean;
import work.chncyl.base.global.handler.FilterExceptionHandler;
import work.chncyl.base.global.tools.RedisUtils;
import work.chncyl.base.global.tools.SessionUtils;
import work.chncyl.base.security.config.SpringSecurityConfig;

import java.io.IOException;
import java.util.Map;

/**
 * 验证码过滤器
 */
public class VerifyCodeFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (!"POST".equalsIgnoreCase(request.getMethod())
                || !SpringSecurityConfig.LOGIN_URL.equals(request.getServletPath())) {
            chain.doFilter(req, res);
            return;
        }

        UserDetails details = SessionUtils.getLoginUser();
        if (details != null) {
            chain.doFilter(req, res);
            return;
        }

        // 验证码校验
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> authenticationBean = (Map<String, String>) mapper.readValue(request.getInputStream(), Map.class);

        String codeId = authenticationBean.get("codeId");
        String requestCaptcha = authenticationBean.get("code");

        if (StringUtils.isBlank(codeId) || StringUtils.isBlank(requestCaptcha)) {
            FilterExceptionHandler.handler(req, res, new AuthenticationServiceException("验证码不能为空"));
            return;
        }

        String genCaptcha = RedisUtils.get(codeId);
        if (genCaptcha == null || !requestCaptcha.equalsIgnoreCase(genCaptcha)) {
            FilterExceptionHandler.handler(req, res, new AuthenticationServiceException("验证码错误"));
            return;
        }

        chain.doFilter(request, response);
    }
}
