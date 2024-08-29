package work.chncyl.base.security.config;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 弱密码检查配置
 */
@Getter
@Configuration
@ConfigurationProperties(prefix = "weak-password-check")
@Data
public class WeakPasswordCheckConfig {

    private Boolean enable = Boolean.TRUE;

    private Boolean checkContainType = Boolean.TRUE;

    private int checkContainTypeNum = 2;

    private Boolean checkPasswordLength = Boolean.TRUE;

    private int minLength = 6;

    private int maxLength = 20;

    private Boolean checkContainDigit = Boolean.TRUE;

    private Boolean checkContainUpperLowerCase = Boolean.TRUE;

    private Boolean checkContainLowerCase = Boolean.TRUE;

    private Boolean checkContainUpperCase = Boolean.TRUE;

    private Boolean checkContainSpecialChar = Boolean.FALSE;

    private String specialCharSet = "!\"//$%&'()*+,-./:;<=>?@[\\]^_`{|}~";

    private Boolean checkHorizontalKeySequential = Boolean.TRUE;

    private int horizontalKeyLimitNum = 3;

    private Boolean checkSlopeKeySequential = Boolean.TRUE;

    private int slopeKeyLimitNum = 3;

    private Boolean checkLogicSequential = Boolean.TRUE;

    private int logicLimitNum = 3;

    private Boolean checkSequentialCharSame = Boolean.TRUE;

    private int sequentialCharNum = 3;

    public static String[] KEYBOARD_HORIZONTAL_ARR = new String[]{"01234567890", "qwertyuiop", "asdfghjkl", "zxcvbnm"};

    public static String[] KEYBOARD_SLOPE_ARR = new String[]{
            "1qaz", "2wsx", "3edc", "4rfv", "5tgb", "6yhn", "7ujm", "8ik,", "9ol.", "0p;/",
            "=[;.", "-pl,", "0okm", "9ijn", "8uhb", "7ygv", "6tfc", "5rdx", "4esz"};

    public void setCheckContainTypeNum(int checkContainTypeNum) {
        this.checkContainTypeNum = Math.max(checkContainTypeNum, 0);
        if (checkContainTypeNum > 4)
            this.checkContainTypeNum = 4;
    }
}