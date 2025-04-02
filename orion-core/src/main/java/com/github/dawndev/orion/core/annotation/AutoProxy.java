package com.github.dawndev.orion.core.annotation;

import org.springframework.stereotype.Controller;

import java.lang.annotation.*;

/**
 * 自动注册代理的注解
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Controller
public @interface AutoProxy {

    /**
     * 检查方法名
     */
    String checkMethod() default "checker";

}
