package work.chncyl.base.global.security.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.shiro.authc.AuthenticationToken;

@AllArgsConstructor
@NoArgsConstructor
public class JwtToken implements AuthenticationToken {

    private String token;

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}