package com.github.dawndev.orion.core.enums;

public enum ApplicationState {
    INITIALIZING,   // 初始化中
    RUNNING,        // 运行中
    MAINTENANCE,    // 维护中
    STOPPING,       // 停止中
    TERMINATED,     // 已停止
    DEGRADED        // 降级运行
}
