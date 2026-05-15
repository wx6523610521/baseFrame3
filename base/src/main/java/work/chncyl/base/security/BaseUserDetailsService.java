package work.chncyl.base.security;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import work.chncyl.base.global.tools.RegexUtils;
import work.chncyl.base.security.entity.LoginAuthorityInfo;
import work.chncyl.base.security.entity.LoginMenuButtonInfo;
import work.chncyl.base.security.entity.LoginUserDetail;
import work.chncyl.base.security.mapper.UserDetailsMapper;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 用户验证服务
 */
@Service
public class BaseUserDetailsService implements UserDetailsService {
    private final UserDetailsMapper detailsMapper;

    public BaseUserDetailsService(UserDetailsMapper detailsMapper) {
        this.detailsMapper = detailsMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LoginUserDetail userDetail;
        // 兼容手机号和用户名登录
        if (RegexUtils.isMobileNum(username)) {
            userDetail = this.detailsMapper.getUserDetail(null, username);
        } else {
            userDetail = this.detailsMapper.getUserDetail(username, null);
        }
        if (userDetail == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        // 配置用户的角色/权限
        List<LoginAuthorityInfo> userAuthorityInfo = detailsMapper.getUserAuthorityInfo(userDetail.getUserId());
        Set<SimpleGrantedAuthority> authorities = Set.of();
        if (!userAuthorityInfo.isEmpty()) {
            authorities = new HashSet<>(userAuthorityInfo.size());
            // 处理角色
            for (LoginAuthorityInfo authorityInfo : userAuthorityInfo) {
                if (StringUtils.isNotBlank(authorityInfo.getMark())) {
                    String role = authorityInfo.getMark();
                    if (!role.startsWith("ROLE")) {
                        role = "ROLE_" + role;
                    }
                    authorities.add(new SimpleGrantedAuthority(role));
                }
                //处理细分权限
                for (LoginMenuButtonInfo menu : authorityInfo.getMenus()) {
                    if (StringUtils.isNotBlank(menu.getPermissionName())) {
                        authorities.add(new SimpleGrantedAuthority(menu.getPermissionName()));
                    }
                }
            }
        }
        userDetail.setAuthorities(authorities);
        return userDetail;
    }
}
