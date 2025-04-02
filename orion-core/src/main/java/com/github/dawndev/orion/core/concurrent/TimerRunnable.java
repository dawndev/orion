package com.github.dawndev.orion.core.concurrent;


public class TimerRunnable implements Runnable {
    //private static final Logger logger = Logger.getLogger(TimerRunnable.class);
    private final TimerExecutor executor;
    private final long id;
    private final TimerListener listener;
    private final Object[] params;
    private final boolean periodic;

    public TimerRunnable(TimerExecutor executor, long id, TimerListener listener, Object[] params, boolean periodic) {
        this.executor = executor;
        this.id = id;
        this.listener = listener;
        this.params = params;
        this.periodic = periodic;
    }

    public long getId() {
        return id;
    }

    @Override
    public void run() {
        if (!periodic) {
            executor.clearTimer(id);
        }
        try {
            listener.onTimer(id, params);
        } catch (Throwable t) {
            //logger.error("Executor onTimer exception for timer id: " + id, t);
        }
    }
}