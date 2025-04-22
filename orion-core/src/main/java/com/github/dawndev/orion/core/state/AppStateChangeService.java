//package com.github.dawndev.orion.core.state;
//
//import com.github.dawndev.orion.core.enums.ApplicationState;
//import org.springframework.context.ApplicationEventPublisher;
//import org.springframework.stereotype.Service;
//
//@Service
//public class AppStateChangeService {
//
//    private final ApplicationEventPublisher eventPublisher;
//
//    public AppStateChangeService(ApplicationEventPublisher eventPublisher) {
//        this.eventPublisher = eventPublisher;
//    }
//
//    public void changeAppState(ApplicationState newState) {
//        eventPublisher.publishEvent(new AppStateChangeEvent(this, newState));
//    }
//}