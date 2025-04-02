package com.github.dawndev.orion.core.modular;

import com.github.dawndev.orion.core.annotation.Modular;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import java.lang.reflect.Method;
import java.util.LinkedList;

public abstract class AbstractHandlerRegister implements BeanPostProcessor {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    protected LinkedList<Object> modularList = new LinkedList<>();


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        try {
            Class<?> beanClass = bean.getClass();

            // 判断对象是否带有AppInit注解，若有该注解，则需要执行该类的初始化方法
            Modular m = beanClass.getAnnotation(Modular.class);
            if (m != null) {
                Method initMethod = beanClass.getDeclaredMethod(m.initMethod());
                if (initMethod != null) {
                    modularList.add(bean);
                }
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

}
