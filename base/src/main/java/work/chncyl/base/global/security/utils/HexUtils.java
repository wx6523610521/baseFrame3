package work.chncyl.base.global.security.utils;

public class HexUtils {
    private static final char[] DIGITS = {
            '0', '1', '2', '3', '4', '5', '6', '7'
            , '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * 常用于加密后的 byte[]转string
     * @param data byte数组
     * @return 转为16进制后的字符串
     */
    public static String bytesToHexString(byte[] data) {
        String string = "";
        try {
            char[] chars = new char[data.length << 1];
            //十六进制数一个四位，byte一个八位
            for (int i = 0, j = 0; i < data.length; i++) {
                chars[j++] = DIGITS[(data[i] >> 4) & 0x0F];
                chars[j++] = DIGITS[data[i] & 0x0F];
            }
            string = new String(chars);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return string;
    }

    public static byte[] hexStringToBytes(String data) {
        //两个四位十六进制字符合成一个八位byte
        byte[] bytes = new byte[data.length() / 2];
        char[] chars = data.toCharArray();
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) ((hexCharToByte(chars[i * 2]) << 4) | hexCharToByte(chars[i * 2 + 1]));
        }
        return bytes;
    }

    private static byte hexCharToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
}