package com.github.dawndev.orion.broker.config;

import akka.actor.ActorSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "akka")
public class AkkaConfig {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String systemName = "";

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    @Bean(destroyMethod = "terminate")
    public ActorSystem actorSystem() {
        logger.info("Create ActorSystem: {}", systemName);
        return ActorSystem.create(systemName);
    }


}
