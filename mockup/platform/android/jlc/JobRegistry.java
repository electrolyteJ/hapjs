package com.nearme.instant.jlc;

import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class JobRegistry {
    private static final List<InitJob> chainOnAttachContext = new ArrayList<InitJob>();
    private static final List<InitJob> chainOnCreate = new ArrayList<InitJob>();
    private static final List<InitJob> chainOnActivityAtIdle = new ArrayList<InitJob>();

    public static void addOnCreateJob(@NonNull InitJob job) {
        chainOnCreate.add(job);
    }

    public static void addOnAttachContextJob(@NonNull InitJob job) {
        chainOnAttachContext.add(job);
    }

    public static void addOnIdleJob(@NonNull InitJob job) {
        chainOnActivityAtIdle.add(job);
    }

    public static void executeAttachJob() {
        executeJobChain(chainOnAttachContext);
    }

    public static void executeOnCreateJob() {
        executeJobChain(chainOnCreate);
        Executors.ui().executeWithDelay(new Runnable() {
            @Override
            public void run() {
                executeOnIdleJob();
            }
        }, 1000);
    }

    public static void executeOnIdleJob() {
        executeJobChain(chainOnActivityAtIdle);
    }

    private static void executeJobChain(@NonNull List<InitJob> chainOnAttachContext) {
        for (InitJob job : chainOnAttachContext) {
            long start = SystemClock.uptimeMillis();
            if (job.needAsync()) {
                Executors.io().execute(job);
            } else {
                job.run();
            }
            Log.i("InitChain", String.format("execute %s use: %d", job.toString(), (SystemClock.uptimeMillis() - start)));
        }
        chainOnAttachContext.clear();
    }
}
