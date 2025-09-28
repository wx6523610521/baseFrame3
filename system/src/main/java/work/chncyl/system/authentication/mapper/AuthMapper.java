package work.chncyl.system.authentication.mapper;

import work.chncyl.base.global.security.entity.LoginedUserInfo;
import org.apache.ibatis.annotations.Param;
import work.chncyl.service.user.entity.Account;

public interface AuthMapper {
    LoginedUserInfo getLoginUserInfo(@Param("userName") String userName);

    Account getLoginUserInfoByOpenId(@Param("userOpenId") String userOpenId);
}
