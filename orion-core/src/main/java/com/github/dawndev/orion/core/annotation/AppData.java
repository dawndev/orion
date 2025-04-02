package com.github.dawndev.orion.core.annotation;

import java.lang.annotation.*;

/**
 * 应用数据
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
//@Controller
public @interface AppData {

    /**
     * 文件路径
     */
    String fileUrl();

    /**
     * 文件编码
     */
    String charSet() default "GBK";

}
