package work.chncyl.base.security.mapper;

import org.apache.ibatis.annotations.Param;
import work.chncyl.base.security.entity.LoginAuthorityInfo;
import work.chncyl.base.security.entity.LoginManageOrgan;
import work.chncyl.base.security.entity.LoginMenuButtonInfo;
import work.chncyl.base.security.entity.LoginUserDetail;

import java.util.List;

public interface UserDetailsMapper {
    LoginUserDetail getUserDetail(@Param("userName") String paramString1, @Param("phoneNum") String paramString2);

    List<LoginAuthorityInfo> getUserAuthorityInfo(@Param("userId") String paramString);

    List<LoginManageOrgan> getUserManageOrganization(@Param("userId") String paramString);

    List<LoginMenuButtonInfo> getRoleMenuButtonInfo(@Param("roleId") String paramString);
}