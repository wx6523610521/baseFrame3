package work.chncyl.service.restRoom.dto;

import lombok.Data;

@Data
public class UserRecordStatisticsOutput {
    private Integer restRoomUserId;

    private Integer totalCount;
    /**
     * 本月使用次数
     */
    private Integer monthCount;
    /**
     * 今日是否已使用
     */
    private Boolean todayUsed;
}
