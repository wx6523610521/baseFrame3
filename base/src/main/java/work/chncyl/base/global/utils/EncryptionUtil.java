package work.chncyl.base.global.utils;


import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EncryptionUtil {
    private static final String DEFAULT_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA1hC/EIzkqkPl3xHEeGrWKXZ20H8eyqf5Qj6eXD/WP6hO52YoDik/HkI5W/cDnZTTVJOqIWe0ajJQdzHNMz373x8KS3lkY56YRp8ByXy6olhrCcxTGCZWG2WvkxSP+x4kwhwcvjNg4js+o9Eg0FPNj8FGxSazyhY/R3SjgDAHUhiyeL5beHcr4lVDUkA/oFan5Cpg7Pk94YQrC/jEWFFP5e3MI9yK+J59IrG+w3itcR+JvtTfHGuUJsITlI6wv3pdppQZQ2CGB/nquI8LzOwyzvWa8zSLXmHREXFFMzff5A74dGGmIvYOIbRIhbU/T1hVuAw9xini5qRd74ddM6MB7wIDAQAB";
    private static final String DEFAULT_PRIVATE_KEY = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDWEL8QjOSqQ+XfEcR4atYpdnbQfx7Kp/lCPp5cP9Y/qE7nZigOKT8eQjlb9wOdlNNUk6ohZ7RqMlB3Mc0zPfvfHwpLeWRjnphGnwHJfLqiWGsJzFMYJlYbZa+TFI/7HiTCHBy+M2DiOz6j0SDQU82PwUbFJrPKFj9HdKOAMAdSGLJ4vlt4dyviVUNSQD+gVqfkKmDs+T3hhCsL+MRYUU/l7cwj3Ir4nn0isb7DeK1xH4m+1N8ca5QmwhOUjrC/el2mlBlDYIYH+eq4jwvM7DLO9ZrzNIteYdERcUUzN9/kDvh0YaYi9g4htEiFtT9PWFW4DD3GKeLmpF3vh10zowHvAgMBAAECggEBAJJXYncrCswHnOeR2vHDf+NalEFXFjLPAI00B8ji71IXetGgufNsaTUDavke9j7NVmf0hxNNNT6AZRgHWNQWJNFVBBGyzFl6O3nUDEG1XlXMAmjxYUEaPDWAn7X6EbBH3DNDSrcQosQmYl1Vg4xILjh/liUDn1ehO+NarsfcjbSl4y44d7YShsmic6Nc/vhhZnxY7geienFFH1FF1lIjP59bOBgS5OavUlQpqOTmpAEmuXKs59ZlUBoVxTonjhyzakqCMiAHC3cjO26poMXFh2XhpYa9jMhOLFM7DRdkEijZ81IWzqUrnUEq/+q/Tfp90JSVCR+Nwiluwzihe6UFuukCgYEA9ey4bLeiDqEXghgy6Oh5Cwuhl2pQNssh4aUJNGnHKdyZ/XEIoWnwyYJIbK+LiOKu5fx9fli49cTFvnrWihYWB/tZRvLCI2kWV+7PhNx/QYHpsyrRcqQVqGOI2th5kwFO5574sC+dncKtj7pYDbfQUXvsNdcI9Hd2bmH4729ZTQ0CgYEA3tXiEpOK7+t5Y6VAsvkGYF2Y6lPsYIRlbQ9wGsHGD2rXfMm/DoXiMIM7Brg87TOIiZq1Kb5ZQZBX4m8tfvUJCEBqwHmOzmsJeeYfgETNEQz/xb+gk0B0WM4jSidy36jCXJx26mWt3uzPQaqq9iVIcu8KgiavgxSUiVz+3wCCo+sCgYA7hIf539N7wOcEmECx95lb3/vCvnsdV7oidyOsKLGH4xjtqo6RKieZTUuj6jRV866qgJoKmwRgjrfg6q/DiabZVa58qkHr+gHrVCOrHwY5X2yk7Qb1vNp0/2netSdvOZDUI7hCQ7Pcu4Hv2AR5hXQIVHqNaTaEm+jwvAtYP1WYfQKBgQDdNdBWhpd/cvAvhmZDBUlaMrBRtvxKGESjzpaISWK3/WVr4M9EuI4BgNUA57a79tONXsKHbQooNwW3qIyuosvToDcZWdL1gXaHx3XwHNzCF3h1s7TRWUPmwMRxodW8yEgiLcUzDbtcORPKaYWMExjn1/tDlunXI4ANGP9/G3+U/wKBgQChZp3k8rqrUWc+dYbG2FRWW1+98WjhLASS0Wvg043qhfMngW8LpJ7UsmOjX5PV1+ls9eks7lbXiTjLjYNmf3t8sXRfbt0aumDkT8RuL8pwii7I7PT0meE425wW6YWuKZLLFxUauleABZp6vtLXn6aR6x7lkpOtFdVA7S3buqdd/w==";

    /**
     * ================================== 非对称加解密 ====================================
     */
    private static final Map<String, RSAKeyPair> keyPairMap = new ConcurrentHashMap<>();

    private static KeyPair generateRSAKeyPair(int keySize) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keySize);
        return keyPairGenerator.generateKeyPair();
    }

    public static String getDefaultPublicKey() {
        return DEFAULT_PUBLIC_KEY;
    }

    /**
     * 生成公钥和私钥
     */
    public static RSAKeyPair generateKeyPair() throws NoSuchAlgorithmException {
        // 生成公钥和私钥
        return generateKeyPair(null, 2048);
    }

    /**
     * 生成公钥和私钥
     *
     * @param keySize 密钥长度 推荐使用 2048 位 或更长（如 4096 位），1024 位已不安全
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static RSAKeyPair generateKeyPair(int keySize) throws NoSuchAlgorithmException {
        // 生成公钥和私钥
        return generateKeyPair(null, keySize);
    }

    public static RSAKeyPair generateKeyPair(String mark) throws NoSuchAlgorithmException {
        // 生成公钥和私钥
        return generateKeyPair(mark, 2048);
    }

    /**
     * 生成临时公钥和私钥,不会缓存，不支持mark相关操作
     */
    public static RSAKeyPair generateTemporaryKeyPair(String mark) throws NoSuchAlgorithmException {
        // 生成公钥和私钥
        RSAKeyPair rsaKeyPair = generateKeyPair(mark, 2048);
        keyPairMap.remove(mark);
        return rsaKeyPair;
    }

    public static RSAKeyPair generateKeyPair(String mark, int keySize) throws NoSuchAlgorithmException {
        if (StringUtils.isBlank(mark)) {
            mark = UUID.randomUUID().toString();
        }
        RSAKeyPair rsaKeyPair = new RSAKeyPair();
        rsaKeyPair.setMark(mark);
        KeyPair keyPair = generateRSAKeyPair(keySize);
        rsaKeyPair.setKeyPair(keyPair);
        rsaKeyPair.setPublicKey(getPublicKeyAsString(keyPair));
        rsaKeyPair.setPrivateKey(getPrivateKeyAsString(keyPair));

        keyPairMap.put(mark, rsaKeyPair);
        return rsaKeyPair;
    }

    public static RSAKeyPair getKeyPair(String mark) {
        return keyPairMap.get(mark);
    }

    private static String getPublicKeyAsString(KeyPair keyPair) {
        PublicKey publicKey = keyPair.getPublic();
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    private static String getPrivateKeyAsString(KeyPair keyPair) {
        PrivateKey privateKey = keyPair.getPrivate();
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    public static String encryptByMark(String content, String mark) throws Exception {
        RSAKeyPair keyPair = getKeyPair(mark);
        return encryptByPublicKey(content, keyPair.getPublicKey());
    }

    public static String decryptByMark(String content, String mark) throws Exception {
        RSAKeyPair keyPair = getKeyPair(mark);
        return decryptByPrivateKey(content, keyPair.getPrivateKey());
    }


    public static String encryptByPublicKey(String content, String publicKey) throws Exception {
        PublicKey punKey = loadPublicKey(publicKey);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, punKey);
        byte[] encryptedBytes = cipher.doFinal(content.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decryptByPrivateKey(String content, String privateKey) throws Exception {
        PrivateKey priKey = loadPrivateKey(privateKey);
        byte[] encryptedBytes = Base64.getDecoder().decode(content);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes);
    }

    public static String encrypt(String content) throws Exception {
        return encryptByPublicKey(content, DEFAULT_PUBLIC_KEY);
    }

    public static String decrypt(String content) throws Exception {
        return decryptByPrivateKey(content, DEFAULT_PRIVATE_KEY);
    }

    private static PublicKey loadPublicKey(String publicKeyStr) throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStr);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    private static PrivateKey loadPrivateKey(String privateKeyStr) throws Exception {
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyStr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }


    @Data
    public static class RSAKeyPair {
        /**
         * 标识符
         */
        private String mark;

        private String publicKey;

        private String privateKey;

        private KeyPair keyPair;
    }

    /**
     * ================================= MD5加密 ================================
     */
    public static String md5(String str) {
        return org.apache.commons.codec.digest.DigestUtils.md5Hex(str);
    }
}
