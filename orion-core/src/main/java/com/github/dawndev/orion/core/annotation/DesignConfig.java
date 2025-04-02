package com.github.dawndev.orion.core.annotation;

import org.springframework.stereotype.Controller;

import java.lang.annotation.*;

/**
 * 配置数据
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Controller
public @interface DesignConfig {

    /**
     * 文件路径
     */
    String fileUrl();

    /**
     * 文件编码
     */
    String charSet() default "UTF-8";

}
