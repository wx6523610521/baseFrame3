package work.chncyl.base.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import work.chncyl.base.global.tools.RegexUtils;
import work.chncyl.base.security.entity.LoginUserDetail;
import work.chncyl.base.security.mapper.UserDetailsMapper;

import java.util.Collections;

/**
 * 用户验证服务
 */
@Service
public class BaseUserDetailsService implements UserDetailsService {
    private final UserDetailsMapper detailsMapper;

    public BaseUserDetailsService(UserDetailsMapper detailsMapper) {
        this.detailsMapper = detailsMapper;
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LoginUserDetail userDetail;
        // 兼容手机号和用户名登录
        if (RegexUtils.isMobileNum(username)) {
            userDetail = this.detailsMapper.getUserDetail(null, username);
        } else {
            userDetail = this.detailsMapper.getUserDetail(username, null);
        }
        if (userDetail == null)
            throw new UsernameNotFoundException("");
        // 配置用户的 角色/权限，SpringSecurity将角色和权限统一管理，区别是角色以ROLE_开头，权限则无要求
        userDetail.setAuthorities(Collections.singleton(new SimpleGrantedAuthority("ROLE_Signed")));
        return userDetail;
    }
}
