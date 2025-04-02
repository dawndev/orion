package com.github.dawndev.orion.core.annotation;

import com.github.dawndev.orion.core.rpc.MsgType;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Rpc {

    /**
     * 消息码
     */
    MsgType msgCode();

    /**
     * 检查方法
     */
    String checker() default "";

    /**
     * 处理方法附加值
     */
    long exp() default 0;

    String alias() default "";

}
