package com.mill.mnative;


import android.app.Application;
import android.content.Context;

import com.github.moduth.blockcanary.BlockCanary;
import com.github.moduth.blockcanary.BlockCanaryContext;
import com.mill.mnative.utils.ContextUtils;
import com.mill.mnative.utils.DeviceUtils;
import com.mill.mnative.utils.LogUtils;
import com.squareup.leakcanary.LeakCanary;

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

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
