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

    public LoginUserDetail(Long userId, String realName, String username, String password, Boolean enabled, String userType, String nickName, String headImage, String phoneNum) {
        this.userId = userId;
        this.realName = realName;
        this.username = username;
        this.password = password;
        this.enabled = enabled != null && enabled;
        this.userType = userType;
        this.nickName = nickName;
        this.headImage = headImage;
        this.phoneNum = phoneNum;
        this.accountNonLocked = true;
        this.accountNonExpired = true;
        this.credentialsNonExpired = true;
    }

    private Long userId;
    private String realName;
    private String username;
    private String password;
    private Set<? extends GrantedAuthority> authorities;
    private final boolean accountNonLocked;
    private boolean enabled;
    private boolean accountNonExpired = true;
    private boolean credentialsNonExpired = true;
    private String accessToken;
    private String userType;
    private String nickName;
    private String headImage;
    private String organId;
    private String organName;
    private String organPath;
    private String phoneNum;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
