package work.chncyl.base.security.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecureDigestAlgorithm;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import work.chncyl.base.global.tools.RedisUtils;
import work.chncyl.base.security.entity.LoginUserDetail;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * JWT工具类
 */
@Component
public class JwtUtil {
    /**
     * 加密密钥
     */
    @Value("${security.secret-key-256:http://www.chncyl.work/QkJN59yMC}")
    private String secretKey;
    /**
     * 过期时间（秒）,最少60s
     */
    @Value("${security.expire-seconds:60}")
    public int expireTime;
    /**
     * token是否允许挪用
     */
    @Value("${security.can-redeployed:true}")
    public boolean canRedeployed;

    private static SecretKey KEY;

    private static Integer EXPIRE_TIME;

    private static final SecureDigestAlgorithm<SecretKey, SecretKey> ALGORITHM = Jwts.SIG.HS256;

    private static final String JWT_ISS = "chncyl";

    @PostConstruct
    public void init() {
        if (secretKey.getBytes().length < 32) {
            secretKey = "http://www.chncyl.work/QkJN59yMC";
        }
        KEY = Keys.hmacShaKeyFor(secretKey.getBytes());
        EXPIRE_TIME = (Math.max(expireTime, 60));
    }

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

    /**
     * 使token失效
     */
    public static void lapsedToken() {

    }
}