package com.github.dawndev.orion.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.beans.factory.annotation.Value;

@SpringBootApplication(scanBasePackages={"com.github.dawndev.orion.core", "com.github.dawndev.orion.gateway"})
@ImportResource("classpath:application-context-gateway.xml")
public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

//    @Value("${spring.application.name}")
//    private String applicationName;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
