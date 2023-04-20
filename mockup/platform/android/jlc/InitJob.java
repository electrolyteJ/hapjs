package com.nearme.instant.jlc;
import android.app.Application;
import android.content.Context;

public interface InitJob extends Runnable {
    boolean needAsync();

    //同步任务
    abstract class SyncInitJob implements InitJob {
        public Application application;
        public Context context;

        public SyncInitJob(Application application, Context context) {
            this.application = application;
            this.context = context;
        }

        @Override
        public boolean needAsync() {
            return false;
        }
    }

    //异步任务
    abstract class AsyncInitJob implements InitJob {

        public Application application;
        public Context context;

        public AsyncInitJob(Application application, Context context) {
            this.application = application;
            this.context = context;
        }

        @Override
        public boolean needAsync() {
            return true;
        }
    }
}
