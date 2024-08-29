package work.chncyl.base.security.entity;

import lombok.Data;

import java.util.List;

@Data
public class LoginMenuButtonInfo {
    private String id;

    private String group;

    private Integer type;

    private String parentId;

    private String permissionName;

    private String displayName;

    private String description;

    private String url;

    private String iconPath;

    private String className;

    private Integer sort;

    private String tags;

    private String tags1;

    private List<LoginMenuButtonInfo> children;

    private List<LoginMenuButtonInfo> permissions;
}