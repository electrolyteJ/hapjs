/*
 * Copyright (C) 2019, hapjs.org. All rights reserved.
 */

package com.nearme.instant.jlc;

import androidx.annotation.NonNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

public class Executors {
    private static final int DEFAULT_CORE_THREAD_COUNT = 5;
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = Math.min(DEFAULT_CORE_THREAD_COUNT, CPU_COUNT / 2);
    private static final int MAX_POOL_SIZE_BACKGROUND = 10;
    private static final int MAX_POOL_SIZE_IO = 256 - MAX_POOL_SIZE_BACKGROUND;
    private static final int MAX_POOL_SIZE_COMPUTATION = CPU_COUNT * 2 + 1;

    private static final String THREAD_NAME_IO = "[io]-";
    private static final String THREAD_NAME_COMPUTATION = "[computation]-";
    private static final String THREAD_NAME_SINGLE_THREAD = "[single]-";
    private static final String THREAD_NAME_SCHEDULED_EXECUTOR = "[scheduled-executor]-";
    protected static final String THREAD_NAME_BACKGROUND = "[background]-";

    private static final long KEEP_ALIVE_TIME = 3000L;

    private static class IoHolder {
        private static final IExecutor INSTANCE = new ConcurrentExecutor(CORE_POOL_SIZE,
                                                                        MAX_POOL_SIZE_IO,
                                                                        KEEP_ALIVE_TIME,
                                                                        new DefaultThreadFactory(THREAD_NAME_IO));
    }

    private static class ComputationHolder {
        private static final ScheduledExecutor INSTANCE = new ScheduledExecutorImpl(CPU_COUNT,
                new DefaultThreadFactory(THREAD_NAME_COMPUTATION));
    }

    private static class ScheduledExecutorHolder {
        private static final ScheduledExecutor INSTANCE = new ScheduledExecutorImpl(CORE_POOL_SIZE, new DefaultThreadFactory(THREAD_NAME_SCHEDULED_EXECUTOR));
    }

    private static class UiThreadHolder {
        private static final DelayedExecutor INSTANCE = new UiExecutor();
    }

    public static IExecutor io() {
        return IoHolder.INSTANCE;
    }

    public static ScheduledExecutor computation() {
        return ComputationHolder.INSTANCE;
    }

    public static ScheduledExecutor scheduled() {
        return ScheduledExecutorHolder.INSTANCE;
    }

    public static DelayedExecutor ui() {
        return UiThreadHolder.INSTANCE;
    }

    public static ScheduledExecutor createSingleThreadExecutor() {
        return new ScheduledExecutorImpl(1, new DefaultThreadFactory(THREAD_NAME_SINGLE_THREAD));
    }

    public static IExecutor backgroundExecutor() {
        return new ConcurrentExecutor(CORE_POOL_SIZE,
                MAX_POOL_SIZE_BACKGROUND,
                KEEP_ALIVE_TIME,
                new DefaultThreadFactory(THREAD_NAME_BACKGROUND));
    }

    private static class DefaultThreadFactory extends AtomicLong implements ThreadFactory {
        private final String namePrefix;

        DefaultThreadFactory(@NonNull String prefix) {
            namePrefix = prefix;
        }

        @Override
        public Thread newThread(@NonNull Runnable r) {
            Thread t = new Thread(r);
            t.setName(namePrefix + getAndIncrement());
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            return t;
        }
    }
}
