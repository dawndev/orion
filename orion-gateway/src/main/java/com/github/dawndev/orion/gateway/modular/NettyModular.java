package com.github.dawndev.orion.gateway.modular;

import com.github.dawndev.orion.core.annotation.Modular;
import com.github.dawndev.orion.gateway.config.NettyConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Modular
public class NettyModular {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private NettyConfig nettyConfig;

    public void init() {
        logger.info("初始化netty, {}, {}, {}", nettyConfig.getPort(), nettyConfig.getBossThreadCount(), nettyConfig.getWorkThreadCount());
    }
}
