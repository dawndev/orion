package com.github.dawndev.orion.core.state;

import com.github.dawndev.orion.core.enums.ApplicationState;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class ApplicationStateManager {

    private final AtomicReference<ApplicationState> currentState =
            new AtomicReference<>(ApplicationState.INITIALIZING);

    private final ApplicationEventPublisher eventPublisher;

    public ApplicationStateManager(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public ApplicationState getCurrentState() {
        return currentState.get();
    }

    public boolean performStateTransition(ApplicationState newState, String reason) {
        ApplicationState previous = currentState.get();

        if (isValidTransition(previous, newState)) {
            if (currentState.compareAndSet(previous, newState)) {
                // 发布状态变更事件
                eventPublisher.publishEvent(
                        new ApplicationStateSideEffectEvent(this, previous, newState, reason)
                );
                return true;
            }
        }
        return false;
    }

    private boolean isValidTransition(ApplicationState from, ApplicationState to) {
        // 实现状态转换规则校验
        return true;
    }
}
