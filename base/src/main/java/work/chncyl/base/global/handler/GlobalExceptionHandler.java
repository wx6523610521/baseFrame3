package work.chncyl.base.global.handler;

import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import work.chncyl.base.global.tools.result.ApiResult;

/**
 * 全局异常处理
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({AuthenticationException.class})
    public ApiResult<Object> authenExceptionHandler(Exception e) {
        System.out.println("" + e);
        return ApiResult.error401(e.getMessage());
    }

    @ExceptionHandler({Exception.class})
    public ApiResult<Object> exceptionHandler(Exception e) {
        System.out.println("" + e);
        return ApiResult.error500(e.getMessage());
    }
}