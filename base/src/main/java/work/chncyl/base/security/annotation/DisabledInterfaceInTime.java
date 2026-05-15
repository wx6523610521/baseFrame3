package work.chncyl.base.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口禁用时间段
 * 在开始时间到结束时间之间，接口禁止访问
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DisabledInterfaceInTime {
    /**
     * 开始时间
     */
    String begainTime() default "21:00:00";

    /**
     * 结束时间
     */
    String endTime() default "09:00:00";
}
