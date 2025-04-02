package com.github.dawndev.orion.core.lang.callback;

public abstract class DefaultFutureCallback<T> implements FutureCallback<T> {

    @Override
    public void onComplete(CallbackFuture<T> future) throws Exception {
        T obj;
        try {
            obj = future.get();
        } catch (Exception e) {
            onException(e);
            return;
        }
        onSuccess(obj);
    }

    public abstract void onSuccess(T result) throws Exception;

    public void onException(Throwable cause) throws Exception {
    }

}