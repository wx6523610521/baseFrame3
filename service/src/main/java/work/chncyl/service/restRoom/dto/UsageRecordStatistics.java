package work.chncyl.service.restRoom.dto;

import lombok.Data;

import java.util.Date;

@Data
public class UsageRecordStatistics {
    private Integer roomId;

    private String roomName;

    private Integer restRoomUserId;

    private String sex;

    private Date useTime;
}
