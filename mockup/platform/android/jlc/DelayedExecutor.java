/*
 * Copyright (C) 2019, hapjs.org. All rights reserved.
 */

package com.nearme.instant.jlc;

public interface DelayedExecutor extends IExecutor {
    Future executeWithDelay(Runnable runnable, long delay);
}
