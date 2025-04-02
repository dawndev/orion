package com.github.dawndev.orion.core.exception;

public class MethodInvokeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public MethodInvokeException() {
    }

    public MethodInvokeException(String message) {
        super(message);
    }

    public MethodInvokeException(Throwable cause) {
        super(cause);
    }

    public MethodInvokeException(String message, Throwable cause) {
        super(message, cause);
    }

}
