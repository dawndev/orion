package com.github.dawndev.orion.broker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication(scanBasePackages={"com.github.dawndev.orion.core", "com.github.dawndev.orion.broker"})
@ImportResource("classpath:application-context-broker.xml")
public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Application.class);
        application.setRegisterShutdownHook(false);  // 禁用默认钩子
        application.run(args);
    }
}
