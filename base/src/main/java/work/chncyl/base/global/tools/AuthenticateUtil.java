package work.chncyl.base.global.tools;

import jakarta.annotation.PostConstruct;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSA加解密工具
 */
@Component
public class AuthenticateUtil {

    @Value("${security.rsa-public-key}")
    private String publicKeyBase64;

    @Value("${security.rsa-private-key}")
    private String privateKeyBase64;

    private Cipher cipher;

    private RSAPublicKey pubKey;

    private RSAPrivateKey priKey;

    @PostConstruct
    public void init() throws Exception {
        byte[] pubDecoded = Base64.decodeBase64(publicKeyBase64);
        byte[] priDecoded = Base64.decodeBase64(privateKeyBase64);
        KeyFactory rsa = KeyFactory.getInstance("RSA");
        pubKey = (RSAPublicKey) rsa.generatePublic(new X509EncodedKeySpec(pubDecoded));
        priKey = (RSAPrivateKey) rsa.generatePrivate(new PKCS8EncodedKeySpec(priDecoded));
        cipher = Cipher.getInstance("RSA");
    }

    public String encrypt(String str) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            return Base64.encodeBase64String(cipher.doFinal(str.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException("RSA加密失败", e);
        }
    }

    public String decrypt(String str) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, priKey);
            return new String(cipher.doFinal(Base64.decodeBase64(str.getBytes(StandardCharsets.UTF_8))));
        } catch (Exception e) {
            throw new RuntimeException("RSA解密失败", e);
        }
    }
}