package com.github.dawndev.orion.core.concurrent;

public class TimerExecutors {
    public static TimerExecutor newSingleTimerExecutor() {
        return new TimerThreadPoolExecutor(1);
    }

    public static TimerExecutor newTimerExecutorPool(int corePoolSize) {
        return new TimerThreadPoolExecutor(corePoolSize);
    }


}