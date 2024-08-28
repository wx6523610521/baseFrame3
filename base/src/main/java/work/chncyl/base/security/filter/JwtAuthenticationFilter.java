package work.chncyl.base.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import work.chncyl.base.global.handler.FilterExceptionHandler;
import work.chncyl.base.security.entity.LoginUserDetail;
import work.chncyl.base.security.utils.JwtUtil;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = this.jwtUtil.getToken(request);
        if (!StringUtils.hasText(jwt)) {
            filterChain.doFilter((ServletRequest) request, (ServletResponse) response);
            return;
        }
        try {
            if (this.jwtUtil.isTokenEffective(jwt).booleanValue()) {
                LoginUserDetail userDetails = this.jwtUtil.getUserDetails(jwt);
                Boolean isTokenValid = Boolean.valueOf(true);
                if (userDetails != null && isTokenValid.booleanValue()) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails((new WebAuthenticationDetailsSource())
                            .buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication((Authentication) authentication);
                }
            }
        } catch (Exception e) {
            FilterExceptionHandler.handler((ServletRequest) request, (ServletResponse) response, e);
            return;
        }
        filterChain.doFilter((ServletRequest) request, (ServletResponse) response);
    }
}