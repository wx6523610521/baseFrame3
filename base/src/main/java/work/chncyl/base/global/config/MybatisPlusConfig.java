package work.chncyl.base.global.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import work.chncyl.base.global.tools.IdGeneratorUtils;
import work.chncyl.base.global.tools.SessionUtils;

import java.util.Objects;

@Configuration
@MapperScan("work.chncyl.**.mapper")
public class MybatisPlusConfig {

    /**
     * 添加分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    /**
     * mybatis-plus 自动填充
     * 自动填充 id, createBy, updateBy, createTime, updateTime 等
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                this.strictInsertFill(metaObject, "id", String.class, IdGeneratorUtils.nextId());
                this.strictInsertFill(metaObject, "createTime", java.time.LocalDateTime.class, java.time.LocalDateTime.now());
                if (SessionUtils.getLoginUserDetail() != null) {
                    this.strictInsertFill(metaObject, "createUser", String.class, Objects.requireNonNull(SessionUtils.getLoginUserDetail()).getUserId());
                    this.strictInsertFill(metaObject, "createBy", String.class, Objects.requireNonNull(SessionUtils.getLoginUserDetail()).getNickName());
                    this.strictInsertFill(metaObject, "creator", String.class, Objects.requireNonNull(SessionUtils.getLoginUserDetail()).getNickName());
                }
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                this.strictInsertFill(metaObject, "updateTime", java.time.LocalDateTime.class, java.time.LocalDateTime.now());
                this.strictInsertFill(metaObject, "lastUpdateTime", java.time.LocalDateTime.class, java.time.LocalDateTime.now());
                if (SessionUtils.getLoginUserDetail() != null) {
                    this.strictInsertFill(metaObject, "updateUser", String.class, Objects.requireNonNull(SessionUtils.getLoginUserDetail()).getUserId());
                    this.strictInsertFill(metaObject, "updateBy", String.class, Objects.requireNonNull(SessionUtils.getLoginUserDetail()).getNickName());
                    this.strictInsertFill(metaObject, "lastUpdateUser", String.class, Objects.requireNonNull(SessionUtils.getLoginUserDetail()).getUserId());
                    this.strictInsertFill(metaObject, "lastUpdateBy", String.class, Objects.requireNonNull(SessionUtils.getLoginUserDetail()).getNickName());
                }
            }
        };
    }
}