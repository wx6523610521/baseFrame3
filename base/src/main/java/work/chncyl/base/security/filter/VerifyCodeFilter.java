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
    String defaultFilterProcessUrl = SpringSecurityConfig.loginUrl;

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        if ("POST".equalsIgnoreCase(request.getMethod()) && defaultFilterProcessUrl.equals(request.getServletPath())) {
            UserDetails details = SessionUtils.getLoginUser();
            if (details != null) {
                chain.doFilter(req, res);
                return;
            }
            // 验证码校验
            ObjectMapper mapper = new ObjectMapper();
            ServletInputStream servletInputStream = request.getInputStream();
            Map<String, String> authenticationBean = (Map<String, String>) mapper.readValue(servletInputStream, Map.class);
            String codeId = authenticationBean.get("codeId").trim();
            String requestCaptcha = authenticationBean.get("code").trim();

            if (StringUtils.isBlank(codeId) || StringUtils.isBlank(requestCaptcha)) {
                FilterExceptionHandler.handler(req, res, new AuthenticationServiceException(""));
                return;
            }
            String genCaptcha = RedisUtils.get(codeId);
            if (!requestCaptcha.equalsIgnoreCase(genCaptcha)) {
                FilterExceptionHandler.handler(req, res, new AuthenticationServiceException(""));
                return;
            }
        }
        chain.doFilter(request, response);
    }
}