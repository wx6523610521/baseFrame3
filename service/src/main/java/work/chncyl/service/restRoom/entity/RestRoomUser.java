package work.chncyl.service.restRoom.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 * 休息房用户
 */
@ApiModel(description = "休息房用户")
@Getter
@Setter
@TableName(value = "rest_room_user")
public class RestRoomUser {
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
     * 用户名称
     */
    @TableField(value = "UserName")
    @ApiModelProperty(value = "用户名称")
    private String userName;

    /**
     * 手机号
     */
    @TableField(value = "PhoneNumber")
    @ApiModelProperty(value = "手机号")
    private String phoneNumber;

    /**
     * 微信唯一id
     */
    @TableField(value = "WxOpenId")
    @ApiModelProperty(value = "微信唯一id")
    private String wxOpenId;

    /**
     * 状态 0 禁用 1 启用
     */
    @TableField(value = "`State`")
    @ApiModelProperty(value = "状态 0 禁用 1 启用")
    private Byte state;

    /**
     * 性别
     */
    @TableField(value = "Sex")
    @ApiModelProperty(value = "性别")
    private String sex;

    @TableField(value = "UserId")
    @ApiModelProperty(value = "")
    private Integer userId;
}