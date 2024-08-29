package work.chncyl.base.security.entity;

import lombok.Data;

import java.util.List;

@Data
public class LoginAuthorityInfo {
    private Integer roleId;

    private String roleName;

    private String displayName;

    private Boolean isDefault;

    private List<LoginMenuButtonInfo> menus;

}