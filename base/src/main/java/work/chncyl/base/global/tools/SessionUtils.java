package work.chncyl.base.global.tools;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import work.chncyl.base.security.entity.LoginUserDetail;

public class SessionUtils {
    public static LoginUserDetail getLoginUserDetail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUserDetail) {
            return (LoginUserDetail) authentication.getPrincipal();
        }
        return null;
    }
}
