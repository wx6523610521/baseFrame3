package work.chncyl.service.restRoom.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class Associated {

    @ApiModelProperty(value = "手机号")
    private String phoneNumber;

    /**
     * 微信唯一id
     */
    private String wxOpenId;

}
