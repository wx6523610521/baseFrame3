package work.chncyl.base.security.annotation;

import java.lang.annotation.*;

/**
 * 匿名接口注解，该注解标注的接口可以匿名访问
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AnonymousAccess {
}