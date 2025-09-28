package work.chncyl.base.global.utils;

import org.apache.commons.lang3.StringUtils;

public class GlobalUtil {
    private static final char[] NUM = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private static final char[] LOWER = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    private static final char[] UPPER = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    private static final char[] LETTER = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    private static final String CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String randomNum(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(NUM[(int) (Math.random() * NUM.length)]);
        }
        return sb.toString();
    }

    public static String randomLower(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(LOWER[(int) (Math.random() * LOWER.length)]);
        }
        return sb.toString();
    }

    public static String randomUpper(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(UPPER[(int) (Math.random() * UPPER.length)]);
        }
        return sb.toString();
    }

    public static String randomLetter(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(LETTER[(int) (Math.random() * LETTER.length)]);
        }
        return sb.toString();
    }

    public static String randomChar(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARS.charAt((int) (Math.random() * CHARS.length())));
        }
        return sb.toString();
    }

    private static final int SCALE = 62;

    /**
     * 将数字转为62进制
     *
     * @param num    Long 型数字
     * @param length 转换后的字符串长度，不足则左侧补0
     * @return 62进制字符串
     */
    public static String convertTo62(long num, Integer length) {
        StringBuilder sb = new StringBuilder();
        int remainder = 0;

        while (num > SCALE - 1) {
            // 对 scale 进行求余，然后将余数追加至 sb 中，由于是从末位开始追加的，因此最后需要反转（reverse）字符串
            remainder = Long.valueOf(num % SCALE).intValue();
            sb.append(CHARS.charAt(remainder));

            num = num / SCALE;
        }

        sb.append(CHARS.charAt(Long.valueOf(num).intValue()));
        String value = sb.reverse().toString();
        if (length != null && length > value.length()) {
            return StringUtils.leftPad(value, length, '0');
        }
        return value;
    }

    public static String convertTo62(long num) {
        return convertTo62(num, null);
    }

    /**
     * 62进制字符串转为数字
     *
     * @param str 编码后的62进制字符串
     * @return 解码后的 10 进制字符串
     */
    public static long deconvert62(String str) {
        // 将 0 开头的字符串进行替换
        str = str.replace("^0*", "");
        long num = 0;
        int index;
        for (int i = 0; i < str.length(); i++) {
            // 查找字符的索引位置
            index = CHARS.indexOf(str.charAt(i));
            // 索引位置代表字符的数值
            num += (long) (index * (Math.pow(SCALE, str.length() - i - 1)));
        }
        return num;
    }

    /**
     * 模糊字符串
     *
     * @param str
     * @param prefix 保留前缀长度
     * @param suffix 保留后缀长度
     * @return
     */
    public static String blur(String str, Integer prefix, Integer suffix) {
        if (StringUtils.isBlank(str)) {
            return str;
        }
        if (prefix == null) {
            prefix = 0;
        }
        if (suffix == null) {
            suffix = 0;
        }

        int length = str.length();
        int start = prefix;
        int end = length - suffix;
        if (start > end) {
            return str;
        }
        String regex = "^(\\w{" + prefix + "}).*(\\w{" + suffix + "})$";
        return str.replaceAll(regex, "$1****$2");
    }
}
