package net.sunxu.website.config.security.rbac.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 要求当前请求者的id 与当前资源的特定属性一致.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface ValueMatch {

    int paramenterIndex() default 0;

    Class<?> resourceQueryClass();

    String resourceQueryMethod();

}
