package com.github.dawndev.orion.broker.config;

import akka.actor.ActorSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
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

        // 加载配置文件
        Config config = ConfigFactory.parseString(
                "akka.remote.artery.canonical.hostname = " + "hostname" + "\n" +
                        "akka.remote.artery.canonical.port = " + "port"
        ).withFallback(ConfigFactory.load("akka-broker.conf"));
        // 创建 ActorSystem

        ActorSystem actorSystem = ActorSystem.create(systemName, config);

        logger.info("Started actor system '{}', member {}", actorSystem, actorSystem.provider().getDefaultAddress());
        return actorSystem;
    }


}
