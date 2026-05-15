package work.chncyl.base.security.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "登录请求参数")
public class LoginRequest {
    
    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED, example = "admin")
    private String userName;
    
    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "password123")
    private String password;
    
    @Schema(description = "验证码ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "uuid-xxx-xxx")
    private String codeId;
    
    @Schema(description = "验证码", requiredMode = Schema.RequiredMode.REQUIRED, example = "abcd")
    private String code;
    
    @Schema(description = "加密标识(不做密码强度校验可不传，有密码强度校验则必传)", example = "encrypted")
    private String encodeStr;
}
