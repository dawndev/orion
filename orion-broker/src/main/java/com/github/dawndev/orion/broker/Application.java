package com.github.dawndev.orion.broker;

import com.github.dawndev.orion.core.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.beans.factory.annotation.Value;

@SpringBootApplication(scanBasePackages={"com.github.dawndev.orion.core", "com.github.dawndev.orion.broker"})
@ImportResource("classpath:application-context-broker.xml")
public class Application implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    @Value("${spring.application.name}")
    private String appName;

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Application.class);
        application.setRegisterShutdownHook(false);  // 禁用默认钩子
        application.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("application:{} , localAddress:{}, isLinuxPlatform: {}", appName, SystemUtils.getLocalAddress(), SystemUtils.isLinuxPlatform());
    }
}
