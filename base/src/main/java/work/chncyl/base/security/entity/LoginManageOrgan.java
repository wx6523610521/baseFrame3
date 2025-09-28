package work.chncyl.base.security.entity;

import lombok.Data;

@Data
public class LoginManageOrgan {
    private int roleId;

    private int roleLevel;

    private String organId;

    private String organName;

    private String organPath;
}