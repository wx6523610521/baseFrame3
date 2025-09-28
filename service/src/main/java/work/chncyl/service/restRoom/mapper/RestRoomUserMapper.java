package work.chncyl.service.restRoom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import work.chncyl.service.restRoom.dto.QueryRestRoomUserDto;
import work.chncyl.service.restRoom.dto.RestRoomUserInfo;
import work.chncyl.service.restRoom.entity.RestRoomUser;

public interface RestRoomUserMapper extends BaseMapper<RestRoomUser> {
    Page<RestRoomUserInfo> queryRestRoomUser(Page<RestRoomUserInfo> page, @Param("queryInfo") QueryRestRoomUserDto queryInfo);
}