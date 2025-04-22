package com.github.dawndev.orion.core.state;

import com.github.dawndev.orion.core.enums.ApplicationState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
public class ApplicationLifecycleStateManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());


    private final ApplicationStateManager stateManager;

    public ApplicationLifecycleStateManager(ApplicationStateManager stateManager) {
        this.stateManager = stateManager;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        stateManager.performStateTransition(ApplicationState.RUNNING, "应用启动完成");
    }

    @EventListener(ContextClosedEvent.class)
    public void onApplicationClosed() {
        stateManager.performStateTransition(ApplicationState.TERMINATED, "应用关闭");
    }

    @Order(Ordered.HIGHEST_PRECEDENCE)
    public void handleStateChangeCommand(StateChangeCommandEvent event) {
        boolean success = stateManager.performStateTransition(event.getTargetState(), event.getReason());
        if (success) {
            logger.info("系统状态变更成功: {} → {}, source: {}, 原因: {}",
                    stateManager.getCurrentState(),
                    event.getTargetState(),
                    event.getSource().getClass().getName(),
                    event.getReason());
        } else {
            logger.warn("系统状态变更失败: 当前状态 {} 不能直接转到 {}",
                    stateManager.getCurrentState(),
                    event.getTargetState());
        }
    }
}
