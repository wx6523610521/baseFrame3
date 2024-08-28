package work.chncyl.base.security.entity;

import lombok.Data;

@Data
public class LoginAuthorityInfo {
  private Integer roleId;

  private String roleName;

  private String displayName;

  private Boolean isDefault;

  private List<LoginMenuButtonInfo> menus;

}