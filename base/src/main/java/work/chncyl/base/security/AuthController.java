package work.chncyl.base;

import com.wf.captcha.SpecCaptcha;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import work.chncyl.base.global.tools.RedisUtils;
import work.chncyl.base.security.annotation.AnonymousAccess;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping({"/TokenAuth"})
@Tag(name = "work.chncyl.base.security.", description = "")
public class AuthController {
    @GetMapping({"/VierificationCode"})
    @AnonymousAccess
    @Operation(description = "")
    public void VierificationCode(HttpServletResponse response, @RequestParam String codeId) throws IOException, FontFormatException {
        SpecCaptcha specCaptcha = new SpecCaptcha(105, 35, 4);
        specCaptcha.setCharType(6);
        specCaptcha.setFont(9);
        String verCode = specCaptcha.text().toLowerCase();
        RedisUtils.set(codeId, verCode, 3L, TimeUnit.MINUTES);
        specCaptcha.out((OutputStream) response.getOutputStream());
    }

    @GetMapping({"/get"})
    @Operation(description = "")
    public String g() {
        return "get";
    }

    @GetMapping({"/no"})
    public String no() {
        return "no";
    }

    @GetMapping({"/sign"})
    public String sign() {
        return "no";
    }
}
