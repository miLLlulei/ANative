package com.mill.mnative.imageload;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Utils {
    private static volatile ExecutorService mSingleExecutor;
    public static ExecutorService getSingleExecutorService() {
        if (mSingleExecutor == null) {
            synchronized (Utils.class) {
                if (mSingleExecutor == null) {
                    mSingleExecutor = Executors.newSingleThreadExecutor();
                }
            }
        }
        return mSingleExecutor;
    }
}
