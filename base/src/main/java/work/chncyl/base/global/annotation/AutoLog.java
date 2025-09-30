package work.chncyl.base.global.annotation;

import work.chncyl.base.global.enums.LogType;

import java.lang.annotation.*;

/**
 * 系统日志注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AutoLog {

    /**
     * 日志记录
     */
    String value() default "";

    /**
     * 日志类型
     */
    LogType logType() default LogType.OPERATION;

    /**
     * 日志记录失效是否影响结果正常返回
     */
    boolean affectsResults() default false;

}
