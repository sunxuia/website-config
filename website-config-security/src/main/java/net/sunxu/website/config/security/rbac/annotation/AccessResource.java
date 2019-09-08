package net.sunxu.website.config.security.rbac.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 资源注解声明, 注解在类或方法上表明这是一个资源.
 * 注解在类上表示这个类内的所有方法访问都需要这个资源授权.
 * 资源名称会被"." 分隔开来建立层级关系, 每层的名称区分大小写, 且不能全是数字.
 * 同时注解在类和方法上会将类的资源名和方法的资源名通过"." 连接起来.
 * 如果在一个类/ 方法上同时注解了多个@Resouce 表示这个类/ 方法有多个资源名, 只需要一个资源名有权限就可以访问.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@Repeatable(AccessResources.class)
public @interface AccessResource {

    String value();

    String remark() default "";
}
