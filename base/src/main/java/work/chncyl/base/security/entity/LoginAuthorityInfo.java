package work.chncyl.base.security.entity;

import lombok.Data;

import java.util.List;

@Data
public class LoginAuthorityInfo {
    private Integer roleId;

    private String mark;

    private String name;

    private Boolean isDefault;

    private List<LoginMenuButtonInfo> menus;
}