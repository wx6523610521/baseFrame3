package work.chncyl.service.restRoom.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;

@Data
public class RecordStatisticsOutput {

    /**
     * 总签到人次
     */
    private int totalCheckInCount;
    /**
     * 男性签到人次
     */
    private int maleCheckInCount;
    /**
     * 女性签到人次
     */
    private int femaleCheckInCount;
    /**
     * 未知性别签到人次
     */
    private int unknownGenderCheckInCount;

    /**
     * 按房屋统计的签到人次
     */
    private List<UsageRecordStatisticsByRoom> usageRecordStatisticsByRoom;

    /**
     * 按日期统计的签到人次
     */
    private List<UsageRecordStatisticsByDate> usageRecordStatisticsByDate;

    @Data
    public static class UsageRecordStatisticsByRoom {
        private String roomName;
        /**
         * 总签到人次
         */
        private int totalCheckInCount;
        /**
         * 男性签到人次
         */
        private int maleCheckInCount;
        /**
         * 女性签到人次
         */
        private int femaleCheckInCount;

        /**
         * 未知性别签到人次
         */
        private int unknownGenderCheckInCount;

    }

    @Data
    public static class UsageRecordStatisticsByDate {
        @JsonFormat(pattern = "yyyy-MM-dd")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private String date;
        /**
         * 总签到人次
         */
        private int totalCheckInCount;

        private List<BaseRoomRecord> roomRecords;
    }

    @Data
    public static class BaseRoomRecord {
        private Integer roomId;
        private String roomName;
        private int totalCheckInCount;
    }
}
