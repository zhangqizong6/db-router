package com.zqz.dbrouter.annotation;

import java.lang.annotation.*;

/**
 * @Retention
 * RetentionPolicy.RUNTIME : 注解不仅被保存到class文件中，jvm加载class文件之后，仍然存在；
 *
 * @Target ElementType.TYPE ：该注解只能声明在一个类前。
 * ElementType.METHOD：该注解只能声明在一个类的方法前。
 *
 * @Documented
 * 指明修饰的注解，可以被例如javadoc此类的工具文档化，只负责标记，没有成员取值。
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface DBRouter {

    String key() default "";

}
