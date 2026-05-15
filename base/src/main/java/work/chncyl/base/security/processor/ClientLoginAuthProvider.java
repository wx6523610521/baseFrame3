package work.chncyl.base.security.processor;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import work.chncyl.base.security.entity.LoginUserDetail;
import work.chncyl.base.security.utils.CheckPwdUtils;

/**
 * 客户端登录处理
 */
public class ClientLoginAuthProvider extends DaoAuthenticationProvider {

    private final CheckPwdUtils checkPwdUtils;

    public ClientLoginAuthProvider(PasswordEncoder passwordEncoder,
                                   CheckPwdUtils checkPwdUtils) {
        super(passwordEncoder);
        this.checkPwdUtils = checkPwdUtils;
    }

    /**
     * 额外身份验证
     */
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        if (authentication.getCredentials() == null) {
            this.logger.debug("Failed to authenticate since no credentials provided");
            throw new BadCredentialsException(this.messages
                    .getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }

        String presentedPassword = authentication.getCredentials().toString();
        LoginUserDetail ud = (LoginUserDetail) userDetails;
        if (!getPasswordEncoder().matches(presentedPassword, ud.getPassword())) {
            this.logger.debug("Failed to authenticate since password does not match stored value");
            throw new BadCredentialsException(this.messages
                    .getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }

        // 自定义token包含额外信息时,解析加密传输的密码原文
        if (authentication instanceof CustomUsernamePasswordAuthenticationToken auth) {
            boolean b = checkPwdUtils.safetyEvalPWDWithDecrypt(auth);
            if (!b) {
                throw new BadCredentialsException("密码错误");
            }
        }
    }
}
