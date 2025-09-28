package work.chncyl.base.global.tools;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSA加解密工具
 */
public class AuthenticateUtil {
    private static final String PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIWGxgtKoHuFN03xnc3CmMZqpYzUyjjuo0ESzC0JOAWfOayMjgFUG3eXG0Y+siobgogbWvafoKRbu8GK7zWs8Qcr7sMmMNBzcPkjj8tth1ISg43kv0pyX6cTqaz1fN5W44zKP3bBELuof8OEPLPOnVBUcLE3nQc5lk3fosV3DiHJAgMBAAECgYAC7YKYKsm8C5670clxAoxRBXOd0+ifOPRRb/UUI7ibvvMHyokulEaJ58FMWAGWFMZW76qd78AY+n3yb3ZxSdp7EpspQBkDOSfP/hNuk5BshWKB/F5tFljqduuGZc2jNPv7d1lbRnZ9Ejy4WvLfGk/lOR94fgHXXkd+BiRgY8QekQJBANEZqDsBlPDeGK2gzSZyg8zz0+DQ+2M6M/JDOjEVJ5WfyITcUzAXreYDOlHwCruBgZ5BMQ5RZm9gEuEBT1zjJzUCQQCjeb2FxVrVlbNorMin0Op0DZykjD7jUjLZEtcBrp2GcR1JVo6qfYBbu1EjeQKmncLFvTKU1oRxVyJMDdv6Xd7FAkBVZ7Ve5HaBKzXJNTSVz5Al1jFkUfdbwBoXiX3rLkjMxEaSfas8qW9N02Ous+yuSWc3zEjNIFJGi2cqn+5aci1BAkEAk1WTZE8csMb9LWgcaHM3+2SROxRTUZmxzkbWlOOFgbfMx15Fso7t8r7+P9Q+eWBSPNlqDw7Pyz7W4GjVS+7yaQJAAM9QNYQEooSGtmw78ca+tYxA3WHod4HAE78Wj1iWogiYqw5+CfOE8uM2DjScX3Lebxeou1KhtytnRW1If/8jUQ==";

    private static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCFhsYLSqB7hTdN8Z3NwpjGaqWM1Mo47qNBEswtCTgFnzmsjI4BVBt3lxtGPrIqG4KIG1r2n6CkW7vBiu81rPEHK+7DJjDQc3D5I4/LbYdSEoON5L9Kcl+nE6ms9XzeVuOMyj92wRC7qH/DhDyzzp1QVHCxN50HOZZN36LFdw4hyQIDAQAB";

    private static final Cipher cipher;

    private static final RSAPublicKey pubKey;

    private static final RSAPrivateKey priKey;

    static {
        byte[] publivDecoded = Base64.decodeBase64(PUBLIC_KEY);
        byte[] privateDecoded = Base64.decodeBase64(PRIVATE_KEY);
        try {
            KeyFactory rsa = KeyFactory.getInstance("RSA");
            pubKey = (RSAPublicKey) rsa.generatePublic(new X509EncodedKeySpec(publivDecoded));
            priKey = (RSAPrivateKey) rsa.generatePrivate(new PKCS8EncodedKeySpec(privateDecoded));
            cipher = Cipher.getInstance("RSA");
        } catch (NoSuchAlgorithmException | java.security.spec.InvalidKeySpecException |
                 javax.crypto.NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String encrypt(String str) {
        try {
            cipher.init(1, pubKey);
            return Base64.encodeBase64String(cipher.doFinal(str.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public static String decrypt(String str) {
        try {
            cipher.init(2, priKey);
            return new String(cipher.doFinal(Base64.decodeBase64(str.getBytes(StandardCharsets.UTF_8))));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

}