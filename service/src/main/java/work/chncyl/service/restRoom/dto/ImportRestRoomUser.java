package work.chncyl.service.restRoom.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ImportRestRoomUser {
    @ExcelProperty("用户名称")
    private String userName;

    @ExcelProperty("手机号")
    private String phoneNumber;

    @ExcelProperty("性别")
    private String sex;

    @ExcelProperty("状态")
    private String state;

    @ExcelProperty("微信OpenId")
    private String wxOpenId;
}
