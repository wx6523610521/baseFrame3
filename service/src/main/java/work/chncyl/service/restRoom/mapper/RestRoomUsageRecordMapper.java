package work.chncyl.service.restRoom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;
import work.chncyl.service.restRoom.dto.*;
import work.chncyl.service.restRoom.entity.RestRoomUsageRecord;

import java.util.List;

public interface RestRoomUsageRecordMapper extends BaseMapper<RestRoomUsageRecord> {
    List<UsageRecordStatistics> selectList(@Param("statistics") RecordStatisticsInput statistics);

    Page<UsageRecord> queryRestRoomUsageRecord(Page<UsageRecord> page, @Param("queryUsageInfo") QueryRestRoomUsageRecordDto queryUsageInfo);

    UserRecordStatisticsOutput selectUserUsageRecordStatistics(Integer userId);

    List<work.chncyl.service.restRoom.entity.RestRoom> selectAllRooms();
}