package work.chncyl.service.restRoom.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import work.chncyl.base.global.pojo.PagedInputPojo;

@Data
public class QueryRestRoomUserDto extends PagedInputPojo {

    private String userName;

    private String phoneNumber;

    @ApiModelProperty(value = "是否已绑定微信")
    private Boolean associated;
}
