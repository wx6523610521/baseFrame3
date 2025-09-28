package work.chncyl.service.restRoom.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 * 休息房使用记录
 */
@ApiModel(description = "休息房使用记录")
@Getter
@Setter
@TableName(value = "rest_room_usage_record")
public class RestRoomUsageRecord {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "")
    private Integer id;

    @TableField(value = "CreateUserId")
    @ApiModelProperty(value = "")
    private Integer createUserId;

    /**
     * 创建时间
     */
    @TableField(value = "CreateTime")
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @TableField(value = "UpdateUserId")
    @ApiModelProperty(value = "")
    private Integer updateUserId;

    @TableField(value = "UpdateTime")
    @ApiModelProperty(value = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @TableField(value = "Deleted")
    @ApiModelProperty(value = "")
    private Boolean deleted;

    @TableField(value = "DeletedTime")
    @ApiModelProperty(value = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deletedTime;

    @TableField(value = "DeletedUserId")
    @ApiModelProperty(value = "")
    private Integer deletedUserId;

    @TableField(value = "RestRoomUserId")
    @ApiModelProperty(value = "")
    private Integer restRoomUserId;

    @TableField(value = "RestRoomId")
    @ApiModelProperty(value = "")
    private Integer restRoomId;

    /**
     * 签到时间
     */
    @TableField(value = "UseTime")
    @ApiModelProperty(value = "签到时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date useTime;

    /**
     * 记录状态 0 禁止 1 正常 2 非使用时间 3 重复记录 4 无效记录
     */
    @TableField(value = "`State`")
    @ApiModelProperty(value = "记录状态 0 禁止 1 正常 2 非使用时间 3 重复记录 4 无效记录")
    private Byte state;

    /**
     * 签到时纬度
     */
    @TableField(value = "Latitude")
    @ApiModelProperty(value = "签到时纬度")
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal latitude;

    /**
     * 签到时经度
     */
    @TableField(value = "Longitude")
    @ApiModelProperty(value = "签到时经度")
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal longitude;
}