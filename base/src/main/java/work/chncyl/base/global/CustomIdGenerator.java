package work.chncyl.base.global;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import org.springframework.stereotype.Component;
import work.chncyl.base.global.tools.IdGeneratorUtils;

@Component
public class CustomIdGenerator implements IKeyGenerator {
    @Override
    public String executeSql(String incrementerName) {
        return null;
    }

    @Override
    public DbType dbType() {
        return null;
    }
}