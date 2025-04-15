package com.github.dawndev.orion.broker.modular;

import com.github.dawndev.orion.broker.config.AkkaConfig;
import com.github.dawndev.orion.core.annotation.Modular;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.Closeable;
import java.io.IOException;

@Modular
@Component
public class AkkaModular implements Closeable {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AkkaConfig akkaConfig;

    public void init() {
        logger.info("初始化Akka, {}", akkaConfig.getSystemName());

    }


    @Override
    public void close() throws IOException {

    }
}
