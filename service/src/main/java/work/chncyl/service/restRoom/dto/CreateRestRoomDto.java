package work.chncyl.service.restRoom.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateRestRoomDto {
    /**
     * 房间标识
     */
    @ApiModelProperty(value = "房间标识")
    private String name;

    /**
     * 二维码地址
     */
    @ApiModelProperty(value = "二维码地址")
    private String qRCode;

    @ApiModelProperty(value = "状态 0 禁用 1 启用")
    private Byte state;

    /**
     * 经度
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal longitude;

    /**
     * 纬度
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal latitude;

    /**
     * 是否启用签到位置限制
     */
    @ApiModelProperty(value = "是否启用签到位置限制")
    private Boolean restrictions;

    /**
     * 签到限制距离（米）
     */
    @ApiModelProperty(value = "签到限制距离（米）")
    private Integer distance;

    private String remark;

}
