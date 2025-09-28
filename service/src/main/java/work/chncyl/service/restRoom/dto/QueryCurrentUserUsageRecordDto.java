package work.chncyl.service.restRoom.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import work.chncyl.base.global.pojo.PagedInputPojo;

import java.util.Date;

@Data
public class QueryCurrentUserUsageRecordDto  extends PagedInputPojo {
    @ApiModelProperty(value = "休息房id")
    private Integer restRoomId;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endTime;
}
