package com.github.dawndev.orion.core.modular;

import com.github.dawndev.orion.core.annotation.Modular;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;

@Component
public class ModularRegister extends AbstractHandlerRegister implements CommandLineRunner {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void run(String... args) throws Exception {
        logger.info("开始注册初始化modular, 总数量{}", modularList.size());
        for (Object bean : modularList) {
            Class<?> beanClass = bean.getClass();
            Modular m = beanClass.getAnnotation(Modular.class);
            if (m == null) {
                continue;
            }
            Method initMethod = bean.getClass().getDeclaredMethod(m.initMethod());

            try {
                initMethod.invoke(bean);
            } catch (Exception e) {
                logger.error("modular:{} 初始化失败", beanClass.getName());
                System.exit(1);
                return;
            }
        }
    }
}
