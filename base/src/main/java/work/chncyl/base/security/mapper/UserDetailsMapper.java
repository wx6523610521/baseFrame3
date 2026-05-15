package work.chncyl.base.security.mapper;

import org.apache.ibatis.annotations.Param;
import work.chncyl.base.security.entity.LoginAuthorityInfo;
import work.chncyl.base.security.entity.LoginMenuButtonInfo;
import work.chncyl.base.security.entity.LoginUserDetail;

import java.util.List;

public interface UserDetailsMapper {
    LoginUserDetail getUserDetail(@Param("userName") String userName, @Param("phoneNum") String phoneNum);

    List<LoginAuthorityInfo> getUserAuthorityInfo(@Param("userId") Long userId);

    List<LoginMenuButtonInfo> getRoleMenuButtonInfo(@Param("roleId") Long roleId);
}