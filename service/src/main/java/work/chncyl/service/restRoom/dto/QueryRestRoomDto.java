package work.chncyl.service.restRoom.dto;

import lombok.Data;
import work.chncyl.base.global.pojo.PagedInputPojo;

@Data
public class QueryRestRoomDto extends PagedInputPojo {

    private Boolean hasQRCode;

    private String name;

    private Boolean restrictions;

    private Integer state;
}
