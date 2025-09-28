package work.chncyl.base.security.entity;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

@Data
public class LoginUserDetail implements UserDetails {
    public LoginUserDetail(String username, String password, Set<GrantedAuthority> authorities) {
        this(username, password, authorities, true, true);
    }

    public LoginUserDetail(String username, String password, Set<GrantedAuthority> authorities, boolean accountNonLocked, boolean enabled) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.accountNonLocked = accountNonLocked;
        this.enabled = enabled;
    }

    private String userId;

    private String username;

    private String password;

    private Set<GrantedAuthority> authorities;

    private final boolean accountNonLocked;

    private final boolean enabled;

    private String accessToken;

    private String userType;

    private String nickName;

    private String headImage;

    private String organId;

    private String organName;

    private String organPath;

    private String phoneNumber;

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    /**
     * 账户是否未过期，默认永不过期
     * @return
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 账户是凭据是否有效
     * @return
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}