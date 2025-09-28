package work.chncyl.base.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
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
        // 登录认证
        if (request.getContentType().equals("application/json")
                || request.getContentType().equals("application/json")) {
            CustomUsernamePasswordAuthenticationToken authenticationToken;
            ObjectMapper mapper = new ObjectMapper();
            try {
                // 从RequestBody中获取登录信息，如使用的form表单提交,则使用request.getParameter获取
                ServletInputStream servletInputStream = request.getInputStream();
                Map<String, String> authenticationBean = (Map<String, String>) mapper.readValue(servletInputStream, Map.class);
                String username = authenticationBean.get("userName");
                String password = authenticationBean.get("password").trim();
                // 登录认证使用的额外信息
                String encodeStr = authenticationBean.get("encodeStr");
                authenticationToken = new CustomUsernamePasswordAuthenticationToken(username, password, encodeStr);
                setDetails(request, authenticationToken);
            } catch (IOException e) {
                e.printStackTrace();
                authenticationToken = new CustomUsernamePasswordAuthenticationToken("", "", "");
            }
            return getAuthenticationManager().authenticate(authenticationToken);
        }

        return null;
    }

    protected void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }
}