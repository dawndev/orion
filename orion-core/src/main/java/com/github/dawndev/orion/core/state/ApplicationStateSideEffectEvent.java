package com.github.dawndev.orion.core.state;

import com.github.dawndev.orion.core.enums.ApplicationState;
import org.springframework.context.ApplicationEvent;

public class ApplicationStateSideEffectEvent extends ApplicationEvent {

    private final ApplicationState fromState;
    private final ApplicationState toState;
    private final String reason;

    public ApplicationStateSideEffectEvent(Object source,
                                           ApplicationState previousState,
                                           ApplicationState newState,
                                           String reason) {
        super(source);
        this.fromState = previousState;
        this.toState = newState;
        this.reason = reason;
    }

    public ApplicationState getFromState() {
        return fromState;
    }

    public ApplicationState getToState() {
        return toState;
    }

    public String getReason() {
        return reason;
    }
}
