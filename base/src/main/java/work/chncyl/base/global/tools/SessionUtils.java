package work.chncyl.base.global.tools;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;

/**
 * session工具
 */
public class SessionUtils {
    public static UserDetails getSession() {
        if (SecurityContextHolder.getContext() == null ||
                SecurityContextHolder.getContext().getAuthentication() == null)
            return null;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof String) {
            String i = (String) principal;
            if (i.equalsIgnoreCase("anonymousUser"))
                return new User("anonymousUser", null, new ArrayList<>());
        }
        assert principal instanceof UserDetails;
        return (UserDetails) principal;
    }

    public static UserDetails getLoginUser() {
        return getSession();
    }

    public static UserDetails getUser() {
        return getSession();
    }
}