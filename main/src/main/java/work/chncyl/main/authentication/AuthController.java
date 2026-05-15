package work.chncyl.main.authentication;

import com.wf.captcha.SpecCaptcha;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import work.chncyl.base.global.tools.RedisUtils;
import work.chncyl.base.security.annotation.AnonymousAccess;
import work.chncyl.base.security.entity.LoginRequest;
import work.chncyl.base.security.entity.LoginSuccessVo;

import java.awt.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping({"/TokenAuth"})
@Tag(name = "身份验证", description = "用户登录、登出、验证码相关接口")
public class AuthController {

    @GetMapping({"/VierificationCode"})
    @AnonymousAccess
    @Operation(
            summary = "获取验证码",
            description = "获取图片验证码，用于登录时校验。验证码有效期3分钟"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "返回验证码图片")
    })
    public void VierificationCode(
            @Parameter(description = "验证码ID，用于关联验证码会话", required = true) 
            @RequestParam String codeId, 
            HttpServletResponse response) throws IOException, FontFormatException {
        SpecCaptcha specCaptcha = new SpecCaptcha(105, 35, 4);
        specCaptcha.setCharType(6);
        specCaptcha.setFont(9);
        String verCode = specCaptcha.text().toLowerCase();
        RedisUtils.set(codeId, verCode, 3L, TimeUnit.MINUTES);
        specCaptcha.out(response.getOutputStream());
    }

    @PostMapping({"/login"})
    @AnonymousAccess
    @Operation(
            summary = "用户登录",
            description = "使用用户名密码登录，需先获取验证码。登录成功后返回JWT令牌"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "登录成功",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginSuccessVo.class))
            ),
            @ApiResponse(responseCode = "401", description = "用户名或密码错误"),
            @ApiResponse(responseCode = "500", description = "验证码错误或为空")
    })
    public void login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "登录请求参数",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LoginRequest.class))
            )
            @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {
    }

    @PostMapping({"/logout"})
    @Operation(
            summary = "用户登出",
            description = "退出登录，使当前JWT令牌失效"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "登出成功")
    })
    public void logout(HttpServletResponse response) {
    }
}
