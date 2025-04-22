package com.github.dawndev.orion.core.modular;

import com.github.dawndev.orion.core.annotation.Modular;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.LifecycleProcessor;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;
import java.util.Map;

@Component
public class ModularRegister extends AbstractHandlerRegister implements CommandLineRunner, DisposableBean,
        ApplicationListener<ContextRefreshedEvent> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ApplicationContext context;

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

        // 获取 LifecycleProcessor
        LifecycleProcessor processor = context.getBean(LifecycleProcessor.class);

        logger.info("手动启动所有 SmartLifecycle 组件.");
        // 手动启动所有 SmartLifecycle 组件
        processor.start();

        logger.info("all modular started synchronously.");
    }

    @Override
    public void destroy() throws Exception {
        logger.info("Application is shutting down. Performing graceful shutdown...");

        // 获取 LifecycleProcessor
        LifecycleProcessor processor = context.getBean(LifecycleProcessor.class);
        processor.stop();
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Map<String, SmartLifecycle> lifecycleBeans = context.getBeansOfType(SmartLifecycle.class);
        boolean allRunning = lifecycleBeans.values().stream().allMatch(SmartLifecycle::isRunning);
        if (allRunning) {
            System.out.println("All SmartLifecycle components have started.");
        } else {
            System.out.println("Some SmartLifecycle components are not running.");
        }
    }
}
