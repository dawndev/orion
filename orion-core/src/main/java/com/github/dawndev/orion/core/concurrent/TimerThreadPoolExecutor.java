package com.github.dawndev.orion.core.concurrent;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;


public class TimerThreadPoolExecutor extends ScheduledThreadPoolExecutor implements TimerExecutor {
    private final AtomicLong futureIdx = new AtomicLong(0);
    private final Map<Long, ScheduledFuture<?>> futureMap = new ConcurrentHashMap<Long, ScheduledFuture<?>>();

    public TimerThreadPoolExecutor(int corePoolSize) {
        super(corePoolSize);
    }

    public TimerThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory) {
        super(corePoolSize, threadFactory);
    }

    @Override
    protected <V> RunnableScheduledFuture<V> decorateTask(Runnable runnable, RunnableScheduledFuture<V> task) {
        if (runnable instanceof TimerRunnable) {
            futureMap.put(((TimerRunnable) runnable).getId(), task);
        }
        return super.decorateTask(runnable, task);
    }

    @Override
    public long addTimer(TimerListener listener, long initialDelay, long delay, Object... params) {
        final long id = futureIdx.incrementAndGet();
        final TimerRunnable command = new TimerRunnable(this, id, listener, params, delay > 0);
        if (delay > 0) {
            scheduleWithFixedDelay(command, initialDelay, delay, TimeUnit.MILLISECONDS);
        } else {
            schedule(command, initialDelay, TimeUnit.MILLISECONDS);
        }
        return id;
    }

    @Override
    public long addFixedRateTimer(TimerListener listener, long initialDelay, long period, Object... params) {
        final long id = futureIdx.incrementAndGet();
        final TimerRunnable command = new TimerRunnable(this, id, listener, params, period > 0);
        if (period > 0) {
            scheduleAtFixedRate(command, initialDelay, period, TimeUnit.MILLISECONDS);
        } else {
            schedule(command, initialDelay, TimeUnit.MILLISECONDS);
        }
        return id;
    }

    @Override
    public void clearTimer(long id) {
        ScheduledFuture<?> future = futureMap.remove(id);
        if (future != null) {
            future.cancel(false);
        }
    }

    @Override
    public void dispose() {
        shutdown();
        futureMap.clear();
    }

}