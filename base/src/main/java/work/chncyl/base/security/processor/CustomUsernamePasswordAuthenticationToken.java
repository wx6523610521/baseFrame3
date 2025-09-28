package work.chncyl.base.security.processor;

import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

/**
 * 自定义用户名密码鉴权TOKEN
 */
@Getter
public class CustomUsernamePasswordAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private final String encodeStr;

    public CustomUsernamePasswordAuthenticationToken(Object principal, Object credentials, String encodeStr) {
        super(principal, credentials);
        this.encodeStr = encodeStr;
    }

}