package work.chncyl.service;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.chncyl.base.security.annotation.AnonymousAccess;
import work.chncyl.base.security.annotation.DisabledInterface;
import work.chncyl.base.security.annotation.DisabledInterfaceInTime;

@RestController
@RequestMapping("/demo")
@RequiredArgsConstructor
public class DemoController {

    @GetMapping("/ordinary")
    public String ordinary() {
        return "普通接口访问成功";
    }

    @AnonymousAccess
    @GetMapping("/Anonymous")
    public String anonymous() {
        return "匿名访问接口访问成功";
    }

    @DisabledInterface("security.prod")
    @GetMapping("/disable")
    public String disable() {
        return "手动禁止接口访问成功";
    }

    @DisabledInterfaceInTime(begainTime = "15:00:00", endTime = "15:10:00")
    @GetMapping("/disableTime")
    public String disableTime() {
        return "时间段禁止接口访问成功";
    }
}
