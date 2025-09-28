package work.chncyl.service.restRoom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import work.chncyl.service.restRoom.dto.QueryRestRoomDto;
import work.chncyl.service.restRoom.entity.RestRoom;

public interface RestRoomMapper extends BaseMapper<RestRoom> {
    Page<RestRoom> queryRestRoom(IPage<RestRoom> page, @Param("queryInfo") QueryRestRoomDto queryInfo);
}