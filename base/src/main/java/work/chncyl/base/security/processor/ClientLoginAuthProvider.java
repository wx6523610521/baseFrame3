package work.chncyl.base.security.processor;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import work.chncyl.base.security.entity.LoginUserDetail;
import work.chncyl.base.security.utils.CheckPwdUtils;

import java.util.UUID;

public class ClientLoginAuthProvider extends DaoAuthenticationProvider {
  private final CheckPwdUtils checkPwdUtils;
  
  public ClientLoginAuthProvider(PasswordEncoder passwordEncoder, CheckPwdUtils checkPwdUtils) {
    super(passwordEncoder);
    this.checkPwdUtils = checkPwdUtils;
  }
  
  protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
    if (authentication.getCredentials() == null) {
      this.logger.debug("Failed to authenticate since no credentials provided");
      throw new BadCredentialsException(this.messages
          .getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
    } 
    String presentedPassword = authentication.getCredentials().toString();
    LoginUserDetail ud = (LoginUserDetail)userDetails;
    if (!getPasswordEncoder().matches(presentedPassword, ud.getPassword())) {
      this.logger.debug("Failed to authenticate since password does not match stored value");
      throw new BadCredentialsException(this.messages
          .getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
    } 
    CustomUsernamePasswordAuthenticationToken auth = (CustomUsernamePasswordAuthenticationToken)authentication;
    String pwd = AuthenticateUtil.decrypt(auth.getEncodeStr());
    if (!this.checkPwdUtils.EvalPWD(pwd)) {
      String s = UUID.randomUUID().toString().replaceAll("-", "");
      RedisUtils.set(s, ud.getUserId(), Integer.valueOf(1800));
    } 
  }
}