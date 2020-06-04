package com.mill.mnative.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;

public class LooperHandlerThread extends HandlerThread {

    private final static String TAG = "LooperHandlerThread";
    private static volatile LooperHandlerThread sLooperHandlerThread;
    private Handler mHandler;

    /**
     * 获取一个全局static的后台单线程执行器
     * 注意：大家共用一个 Looper，不要执行 耗时操作
     * 适合 数据库操作，IO操作
     */
    public static LooperHandlerThread getGlobalThread() {
        if (sLooperHandlerThread == null) {
            synchronized (LooperHandlerThread.class) {
                if (sLooperHandlerThread == null) {
                    sLooperHandlerThread = new LooperHandlerThread("Global LHT");
                    sLooperHandlerThread.start();
                }
            }
        }
        return sLooperHandlerThread;
    }

    public LooperHandlerThread(String threadName) {
        super(threadName, Process.THREAD_PRIORITY_BACKGROUND);
    }

    /**
     * 外部不能调用
     */
    @Override
    public synchronized void start() {
        super.start();
        mHandler = new Handler(getLooper());
    }

    public void post(Runnable runnable) {
        if (mHandler != null) {
            mHandler.post(runnable);
        }
    }

    public void postDelayed(Runnable runnable, long delayMillis) {
        if (mHandler != null) {
            mHandler.postDelayed(runnable, delayMillis);
        }
    }

    public void cancel(Runnable runnable) {
        if (mHandler != null) {
            mHandler.removeCallbacks(runnable);
        }
    }

    /**
     * 注意：getGlobalThread() 别使用 cancelAll， 因为不止你一个人在用 Global 的
     * 要么 cancel(Runnable)， 要么自己new LooperHandlerThread
     */
    public void cancelAll() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}
