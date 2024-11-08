package work.chncyl.base.global.tools.requestTool.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import work.chncyl.base.global.tools.requestTool.wrapper.CustomHttpServletRequestWrapper;
import work.chncyl.base.security.config.SpringSecurityConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CustomRequestFilter extends OncePerRequestFilter {

    private static final Boolean allowAll = false;
    private static final List<String> allowPath = new ArrayList<>(1);

    static {
        allowPath.add(SpringSecurityConfig.loginUrl);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (allowAll || allowPath.contains(request.getServletPath())) {
            request = new CustomHttpServletRequestWrapper(request);
        }
        filterChain.doFilter(request, response);
    }
}