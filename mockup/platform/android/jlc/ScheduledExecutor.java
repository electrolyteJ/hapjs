/*
 * Copyright (C) 2019, hapjs.org. All rights reserved.
 */

package com.nearme.instant.jlc;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public interface ScheduledExecutor extends DelayedExecutor {

    Future scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit);

    Future scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit);

    ScheduledExecutorService getScheduledExecutorService();
}
