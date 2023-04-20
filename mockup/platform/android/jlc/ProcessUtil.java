package com.nearme.instant.jlc;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Process;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

public final class ProcessUtil {
    public static boolean isLauncherProc(Context ctx) {
        String pname = getMyProcessName(ctx);
        return pname != null && pname.startsWith(ctx.getPackageName() + ":Launcher");
    }

    public static boolean isMainProc(Context ctx) {
        return ctx.getPackageName().equals(getMyProcessName(ctx));
    }

    @Nullable
    public static String getMyProcessName(Context ctx) {
        String name = getProcessNameByFile(Process.myPid());
        if (TextUtils.isEmpty(name)) {
            name = getProcessNameByAms(ctx, Process.myPid());
        }
        return name;
    }

    @Nullable
    public static String getProcessNameByFile(int pid) {
        try (BufferedReader reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"))) {
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static String getProcessNameByAms(Context ctx, int pid) {
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        if (runningAppProcesses == null || runningAppProcesses.isEmpty()) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo processInfo : runningAppProcesses) {
            if (processInfo.pid == pid && processInfo.processName != null) {
                return processInfo.processName;
            }
        }
        return null;
    }
}
