package work.chncyl.base.security.filter;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import work.chncyl.base.global.handler.FilterExceptionHandler;
import work.chncyl.base.global.tools.RedisUtils;
import work.chncyl.base.global.tools.SessionUtils;

import java.io.IOException;

public class VerifyCodeFilter extends GenericFilterBean {
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        String defaultFilterProcessUrl = "/api/auth/sign";
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        if ("POST".equalsIgnoreCase(request.getMethod()) && defaultFilterProcessUrl.equals(request.getServletPath())) {
            UserDetails details = SessionUtils.getLoginUser();
            if (details != null) {
                chain.doFilter(req, res);
                return;
            }
            String codeId = request.getParameter("codeId");
            String requestCaptcha = request.getParameter("code");
            if (StringUtils.isBlank(codeId) || StringUtils.isBlank(requestCaptcha))
                FilterExceptionHandler.handler(req, res, new AuthenticationServiceException(""));
            String genCaptcha = RedisUtils.get(codeId);
            if (!requestCaptcha.equalsIgnoreCase(genCaptcha)) {
                FilterExceptionHandler.handler(req, res, new AuthenticationServiceException(""));
                return;
            }
        }
        chain.doFilter(request, response);
    }
}