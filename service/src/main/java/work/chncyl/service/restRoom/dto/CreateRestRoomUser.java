package work.chncyl.service.restRoom.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class CreateRestRoomUser {

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号", required = true)
    private String phoneNumber;

    /**
     * 微信唯一id
     */
    private String wxOpenId;

    @ApiModelProperty(value = "状态 0 禁用 1 启用")
    private Byte state;

    private String sex;
}
