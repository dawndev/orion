package com.github.dawndev.orion.core.concurrent;


import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {
    private final String namePattern;  // 线程名称格式（如 "worker-%d"）
    private final boolean daemon;      // 是否为守护线程
    private final int priority;        // 线程优先级
    private final AtomicInteger nextId = new AtomicInteger(1); // 线程编号生成器

    // 私有构造函数，通过 Builder 创建实例
    private NamedThreadFactory(Builder builder) {
        this.namePattern = builder.namePattern;
        this.daemon = builder.daemon;
        this.priority = builder.priority;
    }

    @Override
    public Thread newThread(Runnable r) {
        String threadName = String.format(namePattern, nextId.getAndIncrement());
        Thread thread = new Thread(r, threadName);
        thread.setDaemon(daemon);
        thread.setPriority(priority);
        return thread;
    }

    // Builder 模式（支持链式调用）
    public static class Builder {
        private String namePattern = "thread-%d"; // 默认名称格式
        private boolean daemon = false;           // 默认非守护线程
        private int priority = Thread.NORM_PRIORITY; // 默认优先级

        public Builder namingPattern(String pattern) {
            this.namePattern = pattern;
            return this;
        }

        public Builder daemon(boolean isDaemon) {
            this.daemon = isDaemon;
            return this;
        }

        public Builder priority(int priority) {
            if (priority < Thread.MIN_PRIORITY || priority > Thread.MAX_PRIORITY) {
                throw new IllegalArgumentException("Invalid priority");
            }
            this.priority = priority;
            return this;
        }

        public NamedThreadFactory build() {
            return new NamedThreadFactory(this);
        }
    }
}
