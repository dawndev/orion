package com.github.dawndev.orion.core.concurrent;

import java.util.concurrent.Executor;

/**
 * timer executor
 *
 * @author bezy 2012-9-15
 *
 */
public interface TimerExecutor extends Executor {
    public long addTimer(TimerListener listener, long initialDelay, long delay, Object... params);

    public long addFixedRateTimer(TimerListener listener, long initialDelay, long period, Object... params);

    public void clearTimer(long id);

    public void dispose();
}