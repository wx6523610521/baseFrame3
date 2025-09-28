package work.chncyl.service.restRoom.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class CreateRestRoomUsageRecordByManageDto {
    private Integer restRoomUserId;

    private Integer restRoomId;

    /**
     * 签到时间
     */
    @ApiModelProperty(value = "签到时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date useTime;

    /**
     * 记录状态 0 禁止 1 正常 2 非使用时间 3 重复记录 4 无效记录
     */
    @ApiModelProperty(value = "记录状态 0 禁止 1 正常 2 非使用时间 3 重复记录 4 无效记录")
    private Byte state;

    /**
     * 签到时纬度
     */
    @ApiModelProperty(value = "签到时纬度")
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal latitude;

    /**
     * 签到时经度
     */
    @ApiModelProperty(value = "签到时经度")
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal longitude;
}
