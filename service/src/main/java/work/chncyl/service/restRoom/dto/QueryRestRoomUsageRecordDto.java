package work.chncyl.service.restRoom.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import work.chncyl.base.global.pojo.PagedInputPojo;

import java.util.Date;

@Data
public class QueryRestRoomUsageRecordDto extends PagedInputPojo {

    private Integer restRoomUserId;

    private String phone;

    private Integer restRoomId;

    @ApiModelProperty(value = "记录状态 0 禁止 1 正常 2 非使用时间 3 重复记录 4 无效记录")
    private Byte state;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endTime;
}
