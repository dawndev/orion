package com.github.dawndev.orion.core.annotation;

import org.springframework.stereotype.Controller;

import java.lang.annotation.*;

/**
 * 系统所有的组件的统一注解
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Controller
public @interface Modular {

    /**
     * 初始化方法名
     */
    String initMethod() default "init";

}
