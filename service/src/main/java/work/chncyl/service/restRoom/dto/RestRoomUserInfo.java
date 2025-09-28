package work.chncyl.service.restRoom.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RestRoomUserInfo {
    private Integer id;

    @ApiModelProperty(value = "用户名称")
    private String userName;

    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号")
    private String phoneNumber;

    /**
     * 微信唯一id
     */
    @ApiModelProperty(value = "微信唯一id")
    private String wxOpenId;

    /**
     * 状态 0 禁用 1 启用
     */
    @ApiModelProperty(value = "状态 0 禁用 1 启用")
    private Byte state;

    /**
     * 性别
     */
    @ApiModelProperty(value = "性别")
    private String sex;

    @ApiModelProperty(value = "记录数")
    private Integer usageCount;
}
