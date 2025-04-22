package com.github.dawndev.orion.broker.modular;

import com.github.dawndev.orion.broker.config.AkkaConfig;
import com.github.dawndev.orion.core.annotation.Modular;
import com.github.dawndev.orion.core.modular.AbstractModular;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Modular
@Component
public class AkkaModular extends AbstractModular {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AkkaConfig akkaConfig;

    public void init() {
        logger.info("初始化Akka, {}", akkaConfig.getSystemName());

    }

    @Override
    public void start() {
        logger.info("AAkka start");
    }

    @Override
    public void stop() {
        logger.info("准备关闭Akka");
    }

    @Override
    public int getPhase() {
        return 0;
    }
}
