package work.chncyl.base.security.entity;

import lombok.Data;

@Data
public class LoginSuccessVo {
    private String accessToken;

    private String headImage;

    private String nickName;

    private String organId;

    private String organName;

    private String organPath;

    private String userId;

    private String userName;

    private Integer userType;
}
