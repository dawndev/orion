package com.github.dawndev.orion.core.state;

import com.github.dawndev.orion.core.enums.ApplicationState;
import org.springframework.context.ApplicationEvent;

public class StateChangeCommandEvent extends ApplicationEvent {
    private final ApplicationState targetState;
    private final String reason;

    public StateChangeCommandEvent(
            Object source,
            ApplicationState targetState,
            String reason
    ) {
        super(source);
        this.targetState = targetState;
        this.reason = reason;
    }
    // getters

    public ApplicationState getTargetState() {
        return targetState;
    }

    public String getReason() {
        return reason;
    }
}

