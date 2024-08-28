package work.chncyl.base.global.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.chncyl.base.security.annotation.AnonymousAccess;

@RestController
public class ExceptionHandlerController {
  @AnonymousAccess
  @RequestMapping({"/error/filterThrow"})
  @Hidden
  public void rethrow(HttpServletRequest request) throws Exception {
    throw (Exception)request.getAttribute("filter.error");
  }
}