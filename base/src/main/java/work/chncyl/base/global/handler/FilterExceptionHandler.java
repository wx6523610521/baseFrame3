package work.chncyl.base.global.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import java.io.IOException;

public class FilterExceptionHandler {
    /**
     * filter异常转发至代理controller
     */
    public static void handler(ServletRequest request, ServletResponse response, Exception e) throws ServletException, IOException {
        request.setAttribute("filter.error", e);
        request.getRequestDispatcher("/error/filterThrow").forward(request, response);
    }
}