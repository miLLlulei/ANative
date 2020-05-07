package com.mill.mnative.utils;

import android.os.Handler;
import android.os.Looper;

/**
 *
 */
public class ThreadUtils {
    private static final String TAG = "ThreadUtils";
    private static final Handler mMainHandler = new Handler(Looper.getMainLooper());

    /**
     * 在UI线程执行Runnable
     *
     * @param action
     */
    public static void runOnUiThread(Runnable action) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            action.run();
        } else {
            mMainHandler.post(action);
        }
    }

    /**
     * 取消 UI Runnable
     *
     * @param action
     */
    public static void cancelUiRunnable(Runnable action) {
        if (mMainHandler != null && action != null) {
            mMainHandler.removeCallbacks(action);
        }
    }

    /**
     * 在UI线程执行Runnable
     *
     * @param action
     */
    public static void postOnUiThread(Runnable action, long delayTime) {
        mMainHandler.postDelayed(action, delayTime);
    }
}