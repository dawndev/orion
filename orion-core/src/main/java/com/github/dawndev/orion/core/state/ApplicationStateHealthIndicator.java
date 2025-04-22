//package com.github.dawndev.orion.core.state;
//
//import com.github.dawndev.orion.core.enums.ApplicationState;
//import org.springframework.boot.actuate.health.Health;
//import org.springframework.boot.actuate.health.HealthIndicator;
//import org.springframework.stereotype.Component;
//
//@Component
//public class ApplicationStateHealthIndicator implements HealthIndicator {
//
//    private final ApplicationStateManager stateManager;
//
//    public ApplicationStateHealthIndicator(ApplicationStateManager stateManager) {
//        this.stateManager = stateManager;
//    }
//
//    @Override
//    public Health health() {
//        ApplicationState state = stateManager.getCurrentState();
//        switch (state) {
//            case RUNNING:
//                // 如果应用程序状态为 RUNNING，则返回一个健康状态为 UP 的 Health 对象。
//                return Health.up().withDetail("state", state).build();
//            case DEGRADED:
//                // 如果应用程序状态为 DEGRADED，则返回一个状态为 DEGRADED 的 Health 对象。
//                return Health.status("DEGRADED").withDetail("state", state).build();
//            case MAINTENANCE:
//                // 如果应用程序状态为 MAINTENANCE，则返回一个处于维护状态的 Health 对象。
//                return Health.outOfService().withDetail("state", state).build();
//            default:
//                // 对于其他未知状态，默认返回一个健康状态为 DOWN 的 Health 对象。
//                return Health.down().withDetail("state", state).build();
//        }
//    }
//}
