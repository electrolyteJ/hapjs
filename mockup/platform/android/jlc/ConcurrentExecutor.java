/*
 * Copyright (C) 2019, hapjs.org. All rights reserved.
 */

package com.nearme.instant.jlc;

import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ConcurrentExecutor implements IExecutor {

    private ThreadPoolExecutor mExecutor;

    private final WorkQueue<Runnable> mWorkQueue = new WorkQueue<>();

    private final RejectedExecutionHandler mRejectedPolicy = (r, e) -> mWorkQueue.superOffer(r);

    public ConcurrentExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, ThreadFactory threadFactory) {
        mExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS, mWorkQueue, threadFactory, mRejectedPolicy);
    }

    @Override
    public void execute(Runnable runnable) {
        mExecutor.execute(runnable);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return new FutureImpl<T>(mExecutor.submit(task));
    }

    class WorkQueue<E> extends LinkedBlockingQueue<E> {
        @Override
        public boolean offer(E e) {
            if (mExecutor.getActiveCount() < mExecutor.getPoolSize()) {
                return super.offer(e);
            }
            return false;
        }

        public void superOffer(E e) {
            super.offer(e);
        }
    }
}
