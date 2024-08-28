package work.chncyl.base.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import work.chncyl.base.security.processor.CustomUsernamePasswordAuthenticationToken;

import java.io.IOException;
import java.util.Map;

public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (request.getContentType().equals("application/json") || request
                .getContentType().equals("application/json")) {
            CustomUsernamePasswordAuthenticationToken authenticationToken;
            ObjectMapper mapper = new ObjectMapper();
            try {
                ServletInputStream servletInputStream = request.getInputStream();
                Map<String, String> authenticationBean = (Map<String, String>) mapper.readValue(servletInputStream, Map.class);
                String username = authenticationBean.get("username");
                String password = authenticationBean.get("password").trim();
                String encodeStr = authenticationBean.get("encodeStr");
                authenticationToken = new CustomUsernamePasswordAuthenticationToken(username, password, encodeStr);
                setDetails(request, authenticationToken);
            } catch (IOException e) {
                e.printStackTrace();
                authenticationToken = new CustomUsernamePasswordAuthenticationToken("", "", "");
            }
            return getAuthenticationManager().authenticate(authenticationToken);
        }
        return super.attemptAuthentication(request, response);
    }
}