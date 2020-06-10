package com.mill.mnative;


import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.github.moduth.blockcanary.BlockCanary;
import com.github.moduth.blockcanary.BlockCanaryContext;
import com.mill.mnative.download.DownloadMgr;
import com.mill.mnative.imageload.ImageLoaderImp;
import com.mill.mnative.utils.ContextUtils;
import com.mill.mnative.utils.LogUtils;
import com.squareup.leakcanary.LeakCanary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class BaseApplication extends Application {

    private static final String TAG = BaseApplication.class.getName();

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ContextUtils.init(this);
        LogUtils.init(this);
    }

    @Override
    public void onCreate() {
        leakBlockCanary();
        super.onCreate();
        String processName = getProcessName();
        if (getPackageName().equals(processName)) {
            startService(new Intent(this, CoreService.class));
            DownloadMgr.getInstance().init(this, LogUtils.isDebug());
            ImageLoaderImp.getInstance().init(this);
        }
    }

    private void leakBlockCanary() {
        if (LogUtils.isDebug()) {
            if (LeakCanary.isInAnalyzerProcess(this)) {
                return;
            }
            LeakCanary.install(BaseApplication.this);
            BlockCanary.install(BaseApplication.this, new BlockCanaryContext() {
                @Override
                public int provideBlockThreshold() {
                    return 3000;
                }
            }).start();
        }
    }

    public static String getProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
