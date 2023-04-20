/*
 * Copyright (C) 2019, hapjs.org. All rights reserved.
 */

package com.nearme.instant.jlc;

import java.util.concurrent.Callable;

public interface IExecutor extends java.util.concurrent.Executor {
    void execute(Runnable runnable);
    <T> Future<T> submit(Callable<T> task);
}
