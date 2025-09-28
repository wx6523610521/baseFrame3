package work.chncyl.base.global.utils;

import work.chncyl.base.global.security.entity.LoginedUserInfo;
import org.apache.shiro.SecurityUtils;

public class SessionUtil {
    public static LoginedUserInfo getSession() {
        return (LoginedUserInfo) SecurityUtils.getSubject().getPrincipal();
    }
}
