package work.chncyl.main.authentication;

import com.wf.captcha.SpecCaptcha;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import work.chncyl.base.global.tools.RedisUtils;
import work.chncyl.base.security.annotation.AnonymousAccess;

import java.awt.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping({"/TokenAuth"})
@Tag(name = "身份验证")
public class AuthController {
    @GetMapping({"/VierificationCode"})
    @AnonymousAccess
    @Operation(description = "获取验证码")
    public void VierificationCode(HttpServletResponse response, @RequestParam String codeId) throws IOException, FontFormatException {
        SpecCaptcha specCaptcha = new SpecCaptcha(105, 35, 4);
        specCaptcha.setCharType(6);
        specCaptcha.setFont(9);
        String verCode = specCaptcha.text().toLowerCase();
        RedisUtils.set(codeId, verCode, 3L, TimeUnit.MINUTES);
        specCaptcha.out(response.getOutputStream());
    }
}
