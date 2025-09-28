package work.chncyl.base.global.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import work.chncyl.base.global.enums.CacheConstant;
import work.chncyl.base.global.redis.RedisUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class RandomStringGenerate {
    private static final String[] scale = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "g", "h",
            "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C",
            "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X",
            "Y", "Z"};
    private static final Map<Character, Integer> charToIndex = new HashMap<>();

    private static Integer START_YEAR = null;
    private static int startYear = 2025;

    @Value("${random-year-start:2025}")
    public void setStartYear(int year) {
        startYear = year;
    }

    static {
        for (int i = 0; i < scale.length; i++) {
            charToIndex.put(scale[i].charAt(0), i);
        }
    }

    /**
     * 时间精度枚举
     */
    public enum TimePrecision {
        DAY, // 精确到日
        HOUR, // 精确到时
        MINUTE, // 精确到分
        SECOND // 精确到秒
    }

    /**
     * 生成全局唯一的含创建日期信息的字符串
     *
     * @param size      字符串长度
     * @param sequence  自定义序列组，同组字符串生成的随机数100%不同
     * @param precision 时间精度
     */
    public static String generateGlobalStr(Integer size, String sequence, TimePrecision precision) {
        return generateStr(size, sequence, precision);
    }

    /**
     * 生成全局唯一的含创建日期信息的字符串
     *
     * @param size      字符串长度
     * @param precision 时间精度
     * @return 随机字符串
     */
    public static String generateGlobalStr(Integer size, TimePrecision precision) {
        return generateStr(size, null, precision);
    }

    /**
     * 生成全局唯一的含创建日期信息的字符串（默认精确到分钟）
     *
     * @param size 字符串长度
     * @return 随机字符串
     */
    public static String generateGlobalStr(Integer size) {
        return generateStr(size, null, TimePrecision.MINUTE);
    }

    private static String generateStr(Integer size, CharSequence sequence, TimePrecision precision) {
        if (START_YEAR == null) {
            START_YEAR = startYear;
        }
        LocalDateTime dateTime = LocalDateTime.now();
        ThreadLocalRandom random = ThreadLocalRandom.current();

        // 计算年月 - 修正年份计算逻辑
        int yearOffset = (dateTime.getYear() - START_YEAR) % 4;
        String yearBin = String.format("%2s", Integer.toString(yearOffset, 2)).replaceAll(" ", "0");
        String monthBin = String.format("%4s", Integer.toString(dateTime.getMonth().getValue() - 1, 2)).replaceAll(" ",
                "0");
        String ymBin = yearBin + monthBin;
        String ym = scale[Integer.valueOf(ymBin, 2)];

        // 计算日
        int dayIdx = dateTime.getDayOfMonth() - 1;
        boolean dayOffset = random.nextBoolean();
        if (dayOffset) {
            dayIdx += 31;
        }
        String d = scale[dayIdx];

        // 根据精度动态计算时间戳
        String timeStamp = "";
        int randomLength = 0;
        boolean hourOffset;
        int minute = 0;
        int second = 0;
        int hour = 0;

        if (precision.compareTo(TimePrecision.DAY) > 0) {
            // 获取小时（带偏移）
            hour = dateTime.getHour();
            hourOffset = random.nextBoolean();
            if (hourOffset) {
                hour += 24;
            }
        }

        if (precision.compareTo(TimePrecision.HOUR) > 0) {
            minute = dateTime.getMinute();
            if (precision == TimePrecision.SECOND) {
                second = dateTime.getSecond();
            }
        }

        // 根据精度计算时间戳结构和随机部分长度
        switch (precision) {
            case DAY:
                timeStamp = ym + d;
                randomLength = size - 2;
                break;
            case HOUR:
                timeStamp = ym + d + scale[hour];
                randomLength = size - 3;
                break;
            case MINUTE:
                timeStamp = ym + d + scale[hour];
                randomLength = size - 4; // 分钟(1) + timeStamp(3) = 4
                break;
            case SECOND:
                timeStamp = ym + d + scale[hour] + scale[second];
                randomLength = size - 5; // 分钟(1) + timeStamp(4) = 5
                break;
        }

        if (sequence == null) {
            StringBuilder randomPart = new StringBuilder(randomLength);
            for (int i = 0; i < randomLength; i++) {
                randomPart.append(scale[random.nextInt(62)]);
            }

            // 不同精度采用不同的拼接策略
            switch (precision) {
                case DAY:
                case HOUR:
                    return randomPart.toString() + timeStamp;
                case MINUTE:
                case SECOND:
                    return scale[minute] + randomPart + timeStamp;
            }
        } else {
            long randomVal = RedisUtils.increment(CacheConstant.RANDOM_CHAR + (":" + sequence), 1);
            StringBuilder sb = new StringBuilder();
            while (randomVal != 0) {
                int digit = (int) randomVal % 62;
                sb.insert(0, scale[digit]);
                randomVal = randomVal / 62;
            }
            String randomStr = String.format("%" + randomLength + "s", sb).replaceAll(" ", "0");
            return timeStamp + randomStr;
        }
        return ""; // 永远不会执行但作为fallback
    }

    public static LocalDateTime getCreateTime(String s) {
        return getCreateTime(s, TimePrecision.MINUTE);
    }

    /**
     * 解析创建时间
     *
     * @param s         生成的字符串
     * @param precision 时间精度
     * @return 创建时间
     */
    public static LocalDateTime getCreateTime(String s, TimePrecision precision) {
        if (START_YEAR == null) {
            START_YEAR = startYear;
        }
        int len = s.length();
        int year = 0, month = 0, day = 0, hour = 0, minute = 0, second = 0;

        // 根据精度从不同位置解析时间戳
        switch (precision) {
            case DAY:
                // 最后2位：年月(ym)+日(d)
                year = START_YEAR + (charToIndex.get(s.charAt(len - 2)) >> 4);
                month = (charToIndex.get(s.charAt(len - 2)) & 15) + 1;
                day = (charToIndex.get(s.charAt(len - 1)) % 31) + 1;
                break;
            case HOUR:
                // 最后3位：ym + d + hour
                year = START_YEAR + (charToIndex.get(s.charAt(len - 3)) >> 4);
                month = (charToIndex.get(s.charAt(len - 3)) & 15) + 1;
                day = (charToIndex.get(s.charAt(len - 2)) % 31) + 1;
                hour = charToIndex.get(s.charAt(len - 1)) % 24;
                break;
            case MINUTE:
                // 第一位: 分钟, 最后3位: ym+d+hour
                minute = charToIndex.get(s.charAt(0));
                year = START_YEAR + (charToIndex.get(s.charAt(len - 3)) >> 4);
                month = (charToIndex.get(s.charAt(len - 3)) & 15) + 1;
                day = (charToIndex.get(s.charAt(len - 2)) % 31) + 1;
                hour = charToIndex.get(s.charAt(len - 1)) % 24;
                break;
            case SECOND:
                // 第一位: 分钟, 最后4位: ym+d+hour+second
                minute = charToIndex.get(s.charAt(0));
                year = START_YEAR + (charToIndex.get(s.charAt(len - 4)) >> 4);
                month = (charToIndex.get(s.charAt(len - 4)) & 15) + 1;
                day = (charToIndex.get(s.charAt(len - 3)) % 31) + 1;
                hour = charToIndex.get(s.charAt(len - 2)) % 24;
                second = charToIndex.get(s.charAt(len - 1));
                break;
        }

        return LocalDateTime.of(year, month, day, hour, minute, second);
    }

    /**
     * 单字符62进制转10进制
     */
    private static int parse62To10(char c) {
        Integer index = charToIndex.get(c);
        if (index == null) {
            throw new IllegalArgumentException("Invalid character: " + c);
        }
        return index;
    }

    /**
     * 62进制字符串转10进制
     */
    private static long parse62To10(String c) {
        long result = 0;
        for (int i = 0; i < c.length(); i++) {
            result = result * 62 + parse62To10(c.charAt(i));
        }
        return result;
    }

    /**
     * 10进制转62进制
     */
    private static String parse10To62(long n) {
        if (n == 0)
            return "0";

        StringBuilder sb = new StringBuilder();
        while (n != 0) {
            int digit = (int) (n % 62);
            sb.insert(0, scale[digit]);
            n = n / 62;
        }
        return sb.toString();
    }
}