package com.github.dawndev.orion.core.modular;

import org.springframework.context.SmartLifecycle;

public abstract class AbstractModular implements SmartLifecycle {


    protected volatile boolean running = false;

    @Override
    public boolean isAutoStartup() {
        return false; // 不自动启动
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
