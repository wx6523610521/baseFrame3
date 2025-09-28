package work.chncyl.system.authentication.dto;

import lombok.Data;

@Data
public class WeChatAuthenticateModel {

    public String openId;
    public String appName;
    public String userName;
    public String password;
}
