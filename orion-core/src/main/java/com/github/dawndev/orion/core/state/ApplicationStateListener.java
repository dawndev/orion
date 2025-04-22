package com.github.dawndev.orion.core.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStateListener {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @EventListener
    public void handleStateChange(ApplicationStateSideEffectEvent event) {
        logger.info("状态变更: {} → {}, 原因:{}", event.getFromState(), event.getToState(), event.getReason());

        // 根据状态执行不同操作
        switch (event.getToState()) {
            case MAINTENANCE:
                enterMaintenanceMode();
                break;
            case DEGRADED:
                enterDegradedMode();
                break;
        }
    }

    private void enterMaintenanceMode() {
        // 进入维护模式的逻辑
    }

    private void enterDegradedMode() {
        // 进入降级模式的逻辑
    }
}
