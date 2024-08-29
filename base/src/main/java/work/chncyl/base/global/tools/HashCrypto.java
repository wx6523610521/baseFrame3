package work.chncyl.base.global.tools;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;

import java.util.Arrays;
import java.util.Random;

/**
 * Hash算法加密、比对工具
 */
public class HashCrypto {
    private static void WriteNetworkByteOrder(byte[] buffer, int offset, int value) {
        buffer[offset] = (byte) (value >> 24);
        buffer[offset + 1] = (byte) (value >> 16);
        buffer[offset + 2] = (byte) (value >> 8);
        buffer[offset + 3] = (byte) (value);
    }

    private static int ReadNetworkByteOrder(byte[] buffer, int offset) {
        return buffer[offset] << 24 | buffer[offset + 1] << 16 | buffer[offset + 2] << 8 | buffer[offset + 3];
    }

    public static boolean VerifyHashedPassword(String hashPwd, String pwd) {
        byte[] hashedPassword = Base64.decodeBase64(hashPwd);
        int iterCount = ReadNetworkByteOrder(hashedPassword, 5);
        int saltLength = ReadNetworkByteOrder(hashedPassword, 9);
        byte[] salt = new byte[saltLength];
        System.arraycopy(hashedPassword, 13, salt, 0, salt.length);
        int subkeyLength = hashedPassword.length - 13 - salt.length;
        byte[] expectedSubkey = new byte[subkeyLength];
        System.arraycopy(hashedPassword, 13 + salt.length, expectedSubkey, 0, expectedSubkey.length);
        PKCS5S2ParametersGenerator gen = new PKCS5S2ParametersGenerator(new SHA256Digest());
        byte[] secretData = pwd.getBytes();
        gen.init(secretData, salt, iterCount);
        byte[] derivedKey = ((KeyParameter) gen.generateDerivedParameters(256)).getKey();
        return Arrays.equals(derivedKey, expectedSubkey);
    }

    public static String hashPassword(String pwd) {
        Random random = new Random();
        int iterCount = 10000;
        int saltSize = 16;
        int numBytesRequested = 256;
        byte[] salt = new byte[saltSize];
        random.nextBytes(salt);
        long etime = System.currentTimeMillis();
        PKCS5S2ParametersGenerator gen = new PKCS5S2ParametersGenerator(new SHA256Digest());
        gen.init(pwd.getBytes(), salt, iterCount);
        byte[] subkey = ((KeyParameter) gen.generateDerivedParameters(numBytesRequested)).getKey();
        byte[] outputBytes = new byte[13 + salt.length + subkey.length];
        outputBytes[0] = 1;
        WriteNetworkByteOrder(outputBytes, 1, 1);
        WriteNetworkByteOrder(outputBytes, 5, iterCount);
        WriteNetworkByteOrder(outputBytes, 9, saltSize);
        System.arraycopy(salt, 0, outputBytes, 13, salt.length);
        System.arraycopy(subkey, 0, outputBytes, 13 + saltSize, subkey.length);
        return Base64.encodeBase64String(outputBytes);
    }

    public static String hashMd5Password(String pwd) {
        return hashPassword(DigestUtils.md5Hex(pwd).toLowerCase());
    }
}