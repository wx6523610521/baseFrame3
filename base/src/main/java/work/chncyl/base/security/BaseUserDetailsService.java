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

@Service
public class BaseUserDetailsService implements UserDetailsService {
    private final UserDetailsMapper detailsMapper;

    public BaseUserDetailsService(UserDetailsMapper detailsMapper) {
        this.detailsMapper = detailsMapper;
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LoginUserDetail abpusers;
        if (RegexUtils.isMobileNum(username)) {
            abpusers = this.detailsMapper.getUserDetail(null, username);
        } else {
            abpusers = this.detailsMapper.getUserDetail(username, null);
        }
        if (abpusers == null)
            throw new UsernameNotFoundException("");
        abpusers.setAuthorities(Collections.singleton(new SimpleGrantedAuthority("ROLE_Signed")));
        return abpusers;
    }
}
