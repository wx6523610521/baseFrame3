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

/**
 * 请求封装过滤器，继承自OncePerRequestFilter确保每个请求只过滤一次
 * 实现请求体多次读取
 */
public class CustomRequestFilter extends OncePerRequestFilter {

    // 定义封装所有请求，默认为false
    private static final Boolean allowAll = false;
    // 进行封装的请求路径列表，为安全起见，初始化为仅包含登录URL
    private static final List<String> allowPath = new ArrayList<>(1);

    // 静态代码块，添加登录URL
    static {
        allowPath.add(SpringSecurityConfig.LOGIN_URL);
    }

    /**
     * 过滤器的核心方法，处理每个请求
     * @param request 当前HTTP请求
     * @param response 当前HTTP响应
     * @param filterChain 过滤器链
     * @throws ServletException 可能抛出的Servlet异常
     * @throws IOException 可能抛出的IO异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 如果允许所有请求通过，或者当前请求路径在允许路径列表中，则创建自定义的请求包装器
        if (allowAll || allowPath.contains(request.getServletPath())) {
            request = new CustomHttpServletRequestWrapper(request);
        }
        // 将请求传递给过滤器链中的下一个过滤器
        filterChain.doFilter(request, response);
    }
}