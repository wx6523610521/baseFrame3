package work.chncyl.base.security.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecureDigestAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import work.chncyl.base.global.tools.RedisUtils;
import work.chncyl.base.security.entity.LoginUserDetail;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {
    private static final String SECRET_KEY = "94F371BE6C40G8CEC923942668G606740FGG674247BE65308C09E228E68D3EEB";

    public static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    private static final SecureDigestAlgorithm<SecretKey, SecretKey> ALGORITHM = Jwts.SIG.HS256;
    /**
     * 过期时间 （秒）
     */
    public static int EXPIRE_TIME = 12 * 60 * 60;

    private static final String JWT_ISS = "chncyl";

    public static String genToken(LoginUserDetail details) {
        String uuid = UUID.randomUUID().toString();
        Date exprireDate = Date.from(Instant.now().plusSeconds(EXPIRE_TIME));
        String token = Jwts.builder()
                .header()
                .add("typ", "JWT")
                .add("alg", "HS256")
                .and().claim("username", details.getUsername())
                .id(uuid)
                .expiration(exprireDate)
                .issuedAt(new Date())
                .issuer(JWT_ISS)
                .signWith(KEY, ALGORITHM)
                .compact();
        details.setAccessToken(token);
        RedisUtils.set(uuid, details, EXPIRE_TIME);
        return token;
    }

    public static Jws<Claims> parseClaim(String token) {
        return Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token);
    }

    public static JwsHeader parseHeader(String token) {
        return parseClaim(token).getHeader();
    }

    public static Claims parsePayload(String token) {
        return parseClaim(token).getPayload();
    }

    public Boolean isTokenEffective(String token) {
        return extractExpiration(token).after(new Date());
    }

    private Date extractExpiration(String token) {
        return parsePayload(token).getExpiration();
    }

    public LoginUserDetail getUserDetails(String token) {
        return RedisUtils.get(parsePayload(token).getId());
    }

    public String getToken(HttpServletRequest request) {
        String jwt, authHeader = request.getHeader("Authorization");
        if (authHeader == null)
            return null;
        if (authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
        } else {
            jwt = authHeader;
        }
        return jwt;
    }

    public static void lapsedToken() {

    }
}