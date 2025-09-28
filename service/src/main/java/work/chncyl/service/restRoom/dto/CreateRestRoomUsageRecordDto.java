package work.chncyl.service.restRoom.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class CreateRestRoomUsageRecordDto {
    @ApiModelProperty(value = "openId,手机号或者用户id 至少有一个")
    private String openId;

    private String phone;

    private Integer restRoomUserId;

    private Integer restRoomId;

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
