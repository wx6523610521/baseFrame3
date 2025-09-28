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
 * 休息房
 */
@ApiModel(description = "休息房")
@Getter
@Setter
@TableName(value = "rest_room")
public class RestRoom {
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

    /**
     * 房间标识
     */
    @TableField(value = "`Name`")
    @ApiModelProperty(value = "房间标识")
    private String name;

    /**
     * 二维码地址
     */
    @TableField(value = "QRCode")
    @ApiModelProperty(value = "二维码地址")
    private String qRCode;

    /**
     * 状态 0 禁用 1 启用
     */
    @TableField(value = "`State`")
    @ApiModelProperty(value = "状态 0 禁用 1 启用")
    private Byte state;

    /**
     * 经度
     */
    @TableField(value = "Longitude")
    @ApiModelProperty(value = "经度")
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal longitude;

    /**
     * 纬度
     */
    @TableField(value = "Latitude")
    @ApiModelProperty(value = "纬度")
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal latitude;

    /**
     * 是否启用签到位置限制
     */
    @TableField(value = "Restrictions")
    @ApiModelProperty(value = "是否启用签到位置限制")
    private Boolean restrictions;

    /**
     * 签到限制距离（米）
     */
    @TableField(value = "Distance")
    @ApiModelProperty(value = "签到限制距离（米）")
    private Integer distance;

    /**
     * 备注
     */
    @TableField(value = "Remark")
    @ApiModelProperty(value = "备注")
    private String remark;
}