package work.chncyl.base.security.annotation;

import java.lang.annotation.*;

/**
 * 匿名接口注解
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AnonymousAccess {
}