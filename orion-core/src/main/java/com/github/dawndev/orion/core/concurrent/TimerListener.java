package com.github.dawndev.orion.core.concurrent;

public interface TimerListener {
    public void onTimer(long id, Object[] params) throws Exception;
}