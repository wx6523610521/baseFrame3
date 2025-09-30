package work.chncyl.base.global.aspect;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import work.chncyl.base.global.annotation.CurrentUser;
import work.chncyl.base.global.tools.SessionUtils;
import work.chncyl.base.security.entity.LoginUserDetail;

/**
 * 当前登录用户自动注入实现
 */

public class CurrentUserMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // 支持的参数为JwtClaimDto类或LoginedUserInfo类，且被CurrentUser注解标注
        return parameter.getParameterType().equals(LoginUserDetail.class)
                // 如果不管有没有CurrentUser注解，都进行注入，则不需要下面这行判断
                && parameter.hasParameterAnnotation(CurrentUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUserDetail) {
            LoginUserDetail currentUser = (LoginUserDetail) authentication.getPrincipal();
            if (parameter.getParameterType().equals(LoginUserDetail.class)) {
                return currentUser;
            }
        }
        return null;
    }
}
