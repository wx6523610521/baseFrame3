package work.chncyl.base.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import work.chncyl.base.security.processor.CustomUsernamePasswordAuthenticationToken;

import java.io.IOException;
import java.util.Map;

/**
 * 自定义用户名密码鉴权过滤器
 */
public class CustomUsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public CustomUsernamePasswordAuthenticationFilter(String defaultFilterProcessesUrl, AuthenticationManager authenticationManager) {
        super(defaultFilterProcessesUrl, authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String contentType = request.getContentType();
        if (contentType == null || !contentType.contains("application/json")) {
            throw new AuthenticationServiceException("不支持的请求类型");
        }

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> authenticationBean;
        try {
            authenticationBean = (Map<String, String>) mapper.readValue(request.getInputStream(), Map.class);
        } catch (IOException e) {
            throw new AuthenticationServiceException("无法解析登录请求");
        }

        String username = authenticationBean.get("userName");
        String password = authenticationBean.getOrDefault("password", "").trim();
        String encodeStr = authenticationBean.get("encodeStr");
        CustomUsernamePasswordAuthenticationToken authenticationToken =
                new CustomUsernamePasswordAuthenticationToken(username, password, encodeStr);
        setDetails(request, authenticationToken);
        return getAuthenticationManager().authenticate(authenticationToken);
    }

    protected void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }
}
