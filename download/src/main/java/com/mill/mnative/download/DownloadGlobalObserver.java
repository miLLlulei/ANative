package com.mill.mnative.download;

import com.mill.mnative.utils.FileUtils;
import com.mill.mnative.utils.LogUtils;
import com.mill.mnative.utils.ThreadUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class DownloadGlobalObserver extends FileDownloadSampleListener {
    public static final String TAG = DownloadGlobalObserver.class.getSimpleName();
    private volatile static DownloadGlobalObserver mInstance;

    private DownloadGlobalObserver() {
    }

    public static DownloadGlobalObserver getInstance() {
        if (mInstance == null) {
            synchronized (DownloadGlobalObserver.class) {
                if (mInstance == null) {
                    mInstance = new DownloadGlobalObserver();
                }
            }
        }
        return mInstance;
    }

    private final List<DownloadObserver> observers = new ArrayList<>();

    public void addObserver(DownloadObserver observer) {
        if (observer != null) {
            if (!observers.contains(observer)) {
                observers.add(observer);
            }
        }
    }

    public void deleteObserver(DownloadObserver observer) {
        observers.remove(observer);
    }

    public int getObserverSize() {
        return observers.size();
    }

    public void notifyObservers(final BaseDownloadBean info) {
        if (!observers.isEmpty()) {
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (DownloadObserver listener : observers) {
                        listener.onDownloadChange(info);
                    }
                }
            });
        }
    }

    @Override
    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        if (LogUtils.isDebug()) {
            LogUtils.d(TAG, "  pending  " + task.getPath() + "  ");
        }
        BaseDownloadBean info = DownloadMgr.getInstance().getDownloadInfo(String.valueOf(task.getId()));
        if (info != null) {
            info.status = FileDownloadStatus.pending;
            info.totalByte = totalBytes > 0 ? totalBytes : info.totalByte;
            info.curByte = soFarBytes <= info.totalByte ? soFarBytes : info.totalByte;
            DownloadMgr.getInstance().saveDownloadInfo(info, false);

            notifyObservers(info);
        }
    }

    @Override
    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        if (LogUtils.isDebug()) {
            LogUtils.d(TAG, "  paused  " + task.getPath() + "  ");
        }

        BaseDownloadBean info = DownloadMgr.getInstance().getDownloadInfo(String.valueOf(task.getId()));
        if (info != null) {
            info.status = FileDownloadStatus.paused;
            DownloadMgr.getInstance().saveDownloadInfo(info, false);

            notifyObservers(info);
        }
    }

    @Override
    protected void completed(BaseDownloadTask task) {
        BaseDownloadBean info = DownloadMgr.getInstance().getDownloadInfo(String.valueOf(task.getId()));
        if (info != null) {
            info.status = FileDownloadStatus.completed;
            long totalBytes = FileUtils.getFileLen(info.savePath);
            info.totalByte = totalBytes > 0 ? totalBytes : info.totalByte;
            info.curByte = info.totalByte;
            DownloadMgr.getInstance().saveDownloadInfo(info, false);

            notifyObservers(info);
        }

        if (LogUtils.isDebug()) {
            LogUtils.d(TAG, "  completed   " + info.toString());
        }
    }

    @Override
    protected void error(BaseDownloadTask task, Throwable e) {
        if (LogUtils.isDebug()) {
            LogUtils.d(TAG, "  error  " + task.getPath() + "  " + e);
        }
        BaseDownloadBean info = DownloadMgr.getInstance().getDownloadInfo(String.valueOf(task.getId()));
        if (info != null) {
            info.status = FileDownloadStatus.error;
            info.errorMsg = (e == null ? "unknown" : e.getMessage());
            DownloadMgr.getInstance().saveDownloadInfo(info, false);

            notifyObservers(info);
        }
    }

    @Override
    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        if (LogUtils.isDebug()) {
            LogUtils.d(TAG, "  progress  " + task.getPath() + "   " + FileUtils.formatFileSize(totalBytes) + "   " + FileUtils.formatFileSize(soFarBytes));
        }
        BaseDownloadBean info = DownloadMgr.getInstance().getDownloadInfo(String.valueOf(task.getId()));
        if (info != null) {
            info.status = FileDownloadStatus.progress;
            info.totalByte = totalBytes > 0 ? totalBytes : info.totalByte;
            info.curByte = soFarBytes <= info.totalByte ? soFarBytes : info.totalByte;
            DownloadMgr.getInstance().saveDownloadInfo(info, false);

            notifyObservers(info);
        }
    }

    @Override
    protected void retry(BaseDownloadTask task, Throwable ex, int retryingTimes, int soFarBytes) {
        if (LogUtils.isDebug()) {
            LogUtils.d(TAG, "  retry  " + task.getPath() + "   " + ex + "   " + retryingTimes + "   " + soFarBytes);
        }
    }
}
