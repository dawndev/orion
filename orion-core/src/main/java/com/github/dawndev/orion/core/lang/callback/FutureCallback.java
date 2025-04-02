package com.github.dawndev.orion.core.lang.callback;

//@FunctionalInterface
public interface FutureCallback<T> {

    void onComplete(CallbackFuture<T> future) throws Exception;

}