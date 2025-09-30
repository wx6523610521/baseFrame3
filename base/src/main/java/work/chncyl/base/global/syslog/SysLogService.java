package work.chncyl.base.global.syslog;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import work.chncyl.base.global.syslog.mapper.SysLogMapper;

@Service
public class SysLogService extends ServiceImpl<SysLogMapper, SysLog> {

    public void addLog(SysLog dto) {
        baseMapper.insert(dto);
    }
}
