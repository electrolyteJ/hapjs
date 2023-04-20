package com.nearme.instant.jlc;

import android.app.Application;
import android.content.Context;

import androidx.annotation.Keep;

@Keep
public class JlcAppDelegate {
    Context appContext;
    Application application;

    public void attachBaseContext(Context base, Application app) {
        appContext = base;
        application = app;
        if (ProcessUtil.isMainProc(base)) {
            registerInMainProc(base, app);
        } else if (ProcessUtil.isLauncherProc(base)) {
            registerInLauncherProc(base, app);
        } else {
            registerInOtherProc(base, app);
        }
        JobRegistry.executeAttachJob();
    }

    public void onCreate() {
        JobRegistry.executeOnCreateJob();
    }

    public void onActivityAtIdle() {
        JobRegistry.executeOnIdleJob();
    }

    /**
     * app 主进程
     * 插桩代码
     */
    public void registerInMainProc(Context appContext, Application application) {
//        JobRegistry.addOnAttachContextJob(new PlatformMainOnAttachContextJob(application, context));//HybridDatabaseHelper数据库初始化
//        JobRegistry.addOnCreateJob(new PlatformMainOnCreateContextJob(application, context));//JsThreadFactory preload预热 与包package安装回调
//        JobRegistry.addOnIdleJob(new PlatformMainOnIdleContextJob(application, context)); //ShortcutUtils初始化
//        JobRegistry.addOnAttachContextJob(new MainOnAttachContextSyncJob(application, context));//路由sdk初始化
//        JobRegistry.addOnCreateJob(new com.nearme.instant.platform.initjob.MainOnCreateJob(application, context));//很多重要初始化需要拆解一下
//        JobRegistry.addOnIdleJob(new MainOnIdleJob(application, context));//SkyEyeManager 、PushProcessMessageManager初始化
//        JobRegistry.addOnCreateJob(new InitPayJob(application, context));//支付模块初始化
//        JobRegistry.addOnCreateJob(new MainOnCreateAsyJob(application, context));//GameLauncherUtil初始化
//        JobRegistry.addOnCreateJob(new com.nearme.instant.quickgame.initjob.MainOnCreateJob(application, context));//快游戏自己的初始化代码
//        JobRegistry.addOnCreateJob(new WebViewOnAppCreateJob(application, context));//tbl初始化 web初始化
    }

    /**
     * 快应用进程
     * 插桩代码
     */
    public void registerInLauncherProc(Context appContext, Application application) {
//        JobRegistry.addOnCreateJob(new PlatformLauncherOnCreateJob(application, context));
//        JobRegistry.addOnAttachContextJob(new PreLoadJsThreadJob(application, context));//JsThreadFactory preload预热
//        JobRegistry.addOnAttachContextJob(new LauncherOnAttachContextJob(application, context));//SentryDebuggerReporterImpl初始化
//        JobRegistry.addOnAttachContextJob(new LauncherOnAttachContextSyncJob(application, context));// WebView.setDataDirectorySuffix
//        JobRegistry.addOnCreateJob(new LauncherOnCreateJob(application, context));
//        JobRegistry.addOnIdleJob(new LauncherOnIdleJob(application, context));//SkyEyeManager start
//        JobRegistry.addOnCreateJob(new InitPayJob(application, context));
//        JobRegistry.addOnCreateJob(new WebViewOnAppCreateJob(application, context));
    }

    /**
     * 其他进程
     * 插桩代码
     */
    public void registerInOtherProc(Context appContext, Application application) {
//        JobRegistry.addOnAttachContextJob(new OtherOnAttachContextJob(application, context));// WebView.setDataDirectorySuffix
//        JobRegistry.addOnCreateJob(new com.nearme.instant.platform.initjob.OtherOnCreateContextJob(application, context));//AdManager
//        JobRegistry.addOnCreateJob(new InitPayJob(application, context));
//        JobRegistry.addOnCreateJob(new OtherOnCreateAsyJob(application, context));//游戏日志初始化 游戏账号初始化
//        JobRegistry.addOnCreateJob(new com.nearme.instant.quickgame.initjob.OtherOnCreateContextJob(application, context));
//        JobRegistry.addOnCreateJob(new WebViewOnAppCreateJob(application, context));
    }

}
