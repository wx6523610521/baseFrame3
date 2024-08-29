package work.chncyl.base.global.exception;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.chncyl.base.security.annotation.AnonymousAccess;

/**
 * 异常处理controller
 */
@RestController
public class ExceptionHandlerController {
    /**
     * 代理filter抛出异常，保证全局异常处理可以统一拦截处理
     */
    @AnonymousAccess
    @RequestMapping({"/error/filterThrow"})
    @Hidden
    public void rethrow(HttpServletRequest request) throws Exception {
        throw (Exception) request.getAttribute("filter.error");
    }
}