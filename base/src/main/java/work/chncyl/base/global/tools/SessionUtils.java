package work.chncyl.base.global.tools;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import work.chncyl.base.security.entity.LoginUserDetail;

import java.util.ArrayList;

/**
 * session工具
 */
public class SessionUtils {
    public static UserDetails getSession() {
        if (SecurityContextHolder.getContext() == null
                || SecurityContextHolder.getContext().getAuthentication() == null) {
            return null;
        }
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof String s && s.equalsIgnoreCase("anonymousUser")) {
            return new User("anonymousUser", "", new ArrayList<>());
        }
        if (principal instanceof UserDetails ud) {
            return ud;
        }
        return null;
    }

    public static LoginUserDetail getLoginUser() {
        UserDetails session = getSession();
        if (session instanceof LoginUserDetail loginUser) {
            return loginUser;
        }
        return null;
    }
}
