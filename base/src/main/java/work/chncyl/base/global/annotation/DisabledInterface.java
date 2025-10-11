package work.chncyl.base.global.annotation;

import org.springframework.core.annotation.AliasFor;
import springfox.documentation.annotations.ApiIgnore;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiIgnore
public @interface DisabledInterface {
    @AliasFor("environment")
    String[] value() default {};

    /**
     * 适用环境
     */
    @AliasFor("value")
    String[] environment() default {};

    /**
     * 排除环境
     */
    String[] excludeEnvironment() default {};
}