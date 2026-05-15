package work.chncyl.base.security.annotation;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 禁用接口
 * 可以根据环境变量值的true/false来禁用接口：为true时禁用接口，为false时启用接口
 * 当禁用接口时，接口将返回404错误
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Hidden
public @interface DisabledInterface {
    @AliasFor("env")
    String value() default "";

    @AliasFor("value")
    String env() default "";

}
