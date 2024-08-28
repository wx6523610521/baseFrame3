package work.chncyl.base.security.processor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class CustomUsernamePasswordAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private final String encodeStr;

    public CustomUsernamePasswordAuthenticationToken(Object principal, Object credentials, String encodeStr) {
        super(principal, credentials);
        this.encodeStr = encodeStr;
    }

    public String getEncodeStr() {
        return this.encodeStr;
    }
}