package com.github.dawndev.orion.core.lang.callback;


import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;


public class CallbackFuture<T> implements Future<T> {
    //private static final Logger logger = Logger.getLogger(CallbackFuture.class);

    private volatile boolean cancelled;
    private final CountDownLatch latch;
    private final AtomicReference<T> objRef;
    private final AtomicReference<Throwable> causeRef;
    private final FutureCallback<T> callback;

    public CallbackFuture() {
        this(null);
    }

    public CallbackFuture(final FutureCallback<T> callback) {
        this.cancelled = false;
        this.latch = new CountDownLatch(1);
        this.objRef = new AtomicReference<T>(null);
        this.causeRef = new AtomicReference<Throwable>(null);
        this.callback = callback;
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        synchronized (this) {
            if (isDone()) {
                return false;
            }
            cancelled = true;
            latch.countDown();
        }

        notifyCallback();

        return true;
    }

    public T get() throws InterruptedException, ExecutionException {
        latch.await();

        return getData();
    }

    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        boolean notOT = latch.await(timeout, unit);

        if (!notOT) {// overtime
            throw new TimeoutException("Future exception for overtime!");
        }

        return getData();
    }

    private T getData() throws ExecutionException {
        if (isCancelled()) {
            throw new CancellationException("Future is cancelled!");
        }

        Throwable cause = getCause();
        if (cause != null) {
            throw new ExecutionException(cause);
        }

        return objRef.get();
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public boolean isDone() {
        return latch.getCount() == 0;
    }

    @SuppressWarnings("unchecked")
    public boolean setSuccess(Object o) {
        synchronized (this) {
            if (isDone()) {
                return false;
            }
            objRef.set((T) o);
            latch.countDown();
        }

        notifyCallback();
        return true;
    }

    public boolean setFailure(Throwable cause) {
        synchronized (this) {
            if (isDone()) {
                return false;
            }
            causeRef.set(cause);
            latch.countDown();
        }

        notifyCallback();
        return true;
    }

    public Throwable getCause() {
        return causeRef.get();
    }

    public boolean isSuccess() {
        return isDone() && !isCancelled() && getCause() == null;
    }

    public CallbackFuture<T> rethrowIfFailed() throws Exception {
        if (!isDone()) {
            return this;
        }
        Throwable cause = getCause();
        if (cause == null) {
            return this;
        }

        if (cause instanceof Exception) {
            throw (Exception) cause;
        }

        if (cause instanceof Error) {
            throw (Error) cause;
        }

        throw new RuntimeException(cause);
    }

    private void notifyCallback() {
        try {
            if (callback != null) {
                callback.onComplete(this);
            }
        } catch (Throwable t) {
            //logger.warn("An exception was thrown by " + FutureCallback.class.getSimpleName() + ".", t);
        }
    }

}