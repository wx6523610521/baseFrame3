package work.chncyl.base.security.processor;


import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import work.chncyl.base.global.tools.AuthenticateUtil;
import work.chncyl.base.security.entity.LoginUserDetail;
import work.chncyl.base.security.utils.CheckPwdUtils;

/**
 * 客户端登录处理
 */
public class ClientLoginAuthProvider extends DaoAuthenticationProvider {
    private final CheckPwdUtils checkPwdUtils;

    public ClientLoginAuthProvider(PasswordEncoder passwordEncoder, CheckPwdUtils checkPwdUtils) {
        super();
        setPasswordEncoder(passwordEncoder);
        this.checkPwdUtils = checkPwdUtils;
    }

    /**
     * 额外身份验证
     */
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
        // 获取到自定义封装的token
        CustomUsernamePasswordAuthenticationToken auth = (CustomUsernamePasswordAuthenticationToken) authentication;
        // 获取到登录认证的额外信息
        String str = AuthenticateUtil.decrypt(auth.getEncodeStr());
        System.out.println(str);
        // 密码复杂度校验
        /*if (!this.checkPwdUtils.EvalPWD(str)) {
            String s = UUID.randomUUID().toString().replaceAll("-", "");
            RedisUtils.set(s, ud.getUserId(), Integer.valueOf(1800));
        }*/
    }
}