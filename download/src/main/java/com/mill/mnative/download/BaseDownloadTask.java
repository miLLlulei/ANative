package com.mill.mnative.download;

import android.util.Log;
import android.util.SparseArray;

import com.mill.mnative.utils.FileUtils;
import com.mill.mnative.utils.LogUtils;
import com.mill.mnative.utils.Md5Utils;
import com.mill.mnative.utils.ThreadUtils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public class BaseDownloadTask implements Runnable {
    public static final String TAG = DownloadMgr.TAG;


    protected String mId;
    protected String mUrl;
    protected boolean mForceReDownload;
    protected int mAutoRetryCountMax;
    protected Map<String, String> mHeader;
    protected String mSavePath;
    protected FileDownloadSampleListener mListener;
    public Future future;
    protected int mCurBytes;
    protected int mTotalBytes;
    protected int mStart;
    protected int mEnd;
    protected int mRetryCount;
    protected int mStatus;

    private SparseArray<BaseChunkTask> mChunkTasks = new SparseArray<>();

    public BaseDownloadTask() {
    }

    public BaseDownloadTask(final String url) {
        this.mUrl = url;
    }

    @Override
    public void run() {
        if (LogUtils.isDebug()) {
            LogUtils.i(TAG, "start " + this + " " + mStatus);
        }
        if (FileDownloadStatus.isOver(mStatus)) {
            remove();
            if (LogUtils.isDebug()) {
                LogUtils.i(TAG, "BaseDownloadTask 任务 isOver true");
            }
            return;
        }
        if (mForceReDownload) {
            boolean delSave = FileUtils.deleteFile(mSavePath);
            if (LogUtils.isDebug()) {
                LogUtils.i(TAG, "ForceReDownload D 删除文件: " + this + " " + delSave + " " + mSavePath);
            }
        }
        // 文件已存在
        long totalByte = FileUtils.getFileLen(mSavePath);
        if (totalByte > 1024) {
            BaseDownloadTask.this.completed(BaseDownloadTask.this, (int) totalByte);
            return;
        }
        mTotalBytes = getTotalLength(mUrl);
        if (mTotalBytes <= 10 * 1024 * 1024) {
            // 小于 10m 的文件，不用 分块下载；
            synchronized (BaseDownloadTask.class) {
                BaseChunkTask chunkTask = new BaseChunkTask(mUrl, mSavePath, 0, 0, mForceReDownload, mAutoRetryCountMax, mHeader, new FileDownloadSampleListener() {
                    @Override
                    protected void completed(BaseDownloadTask task) {
                        BaseDownloadTask.this.completed(BaseDownloadTask.this, task.mTotalBytes);
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) throws RuntimeException {
                        BaseDownloadTask.this.progress(BaseDownloadTask.this, soFarBytes, totalBytes);
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        BaseDownloadTask.this.error(BaseDownloadTask.this, e);
                    }
                });
                chunkTask.mStatus = mStatus;
                chunkTask.run();
            }
        } else {
            int count = FileDownloader.CHUNK_COUNT;
            for (int i = 0; i < count; i++) {
                final int inval = mTotalBytes / 3;
                final int start = inval * i + (i == 0 ? 0 : 1);
                final int end = i == count - 1 ? mTotalBytes : inval * (i + 1);
                final String tempPath = mSavePath + "." + i;
                BaseChunkTask chunkTask = new BaseChunkTask(mUrl, tempPath, start, end, mForceReDownload, mAutoRetryCountMax, mHeader, new FileDownloadSampleListener() {
                    @Override
                    protected void completed(BaseDownloadTask task) {
                        // 多线程下载完成后，合并文件
                        boolean isComplited = true;
                        List<String> tempPaths = new ArrayList<>();
                        for (int i = 0; i < mChunkTasks.size(); i++) {
                            BaseChunkTask ct = mChunkTasks.get(i);
                            tempPaths.add(ct.getPath());
                            if (ct.mStatus != FileDownloadStatus.completed) {
                                isComplited = false;
                            }
                        }
                        if (isComplited) {
                            boolean merge = FileUtils.mergeTempFile(tempPaths, mSavePath, true);
                            if (merge) {
                                BaseDownloadTask.this.completed(BaseDownloadTask.this, BaseDownloadTask.this.getChunkTotalBytes());
                            }
                        }
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) throws RuntimeException {
                        BaseDownloadTask.this.progress(BaseDownloadTask.this, BaseDownloadTask.this.getChunkCurBytes(), BaseDownloadTask.this.getChunkTotalBytes());
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        BaseDownloadTask.this.error(BaseDownloadTask.this, e);
                    }
                });
                synchronized (BaseDownloadTask.class) {
                    mChunkTasks.put(i, chunkTask);
                    chunkTask.mStatus = mStatus;
                    chunkTask.future = FileDownloader.getImpl().getExecutor().submit(chunkTask);
                }
            }
        }
    }

    public void progress(BaseDownloadTask task, int cur, int total) throws RuntimeException {
        mStatus = FileDownloadStatus.progress;
        mCurBytes = cur;
        mTotalBytes = total;
        if (mListener != null) {
            mListener.progress(task, mCurBytes, mTotalBytes);
        }
    }

    public void completed(BaseDownloadTask task, int total) {
        mStatus = FileDownloadStatus.completed;
        mTotalBytes = total;
        mCurBytes = mTotalBytes;
        if (mListener != null) {
            mListener.completed(task);
        }
        remove();
    }

    public void error(BaseDownloadTask task, Throwable e) {
        mStatus = FileDownloadStatus.error;
        if (mListener != null) {
            mListener.error(task, e);
        }
        remove();
    }

    public int getChunkCurBytes() {
        int cur = 0;
        for (int i = 0; i < mChunkTasks.size(); i++) {
            BaseChunkTask ct = mChunkTasks.get(i);
            cur += ct.mCurBytes;
        }
        return cur;
    }

    public int getChunkTotalBytes() {
        if (mTotalBytes == 0) {
            int total = 0;
            for (int i = 0; i < mChunkTasks.size(); i++) {
                BaseChunkTask ct = mChunkTasks.get(i);
                total += ct.mTotalBytes;
            }
            return total;
        }
        return mTotalBytes;
    }

    public String start() {
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (LogUtils.isDebug()) {
                    LogUtils.i(TAG, "pending " + BaseDownloadTask.this + " " + mStatus);
                }
                mStatus = FileDownloadStatus.pending;
                FileDownloader.getImpl().execute(BaseDownloadTask.this);
            }
        });
        return getId();
    }

    public void pause() {
        if (LogUtils.isDebug()) {
            LogUtils.i(TAG, "pause " + BaseDownloadTask.this + " " + mStatus);
        }
        synchronized (BaseDownloadTask.class) {
            mStatus = FileDownloadStatus.paused;
            if (future != null) {
                future.cancel(true);
                future = null;
            }
            for (int i = 0; i < mChunkTasks.size(); i++) {
                BaseChunkTask ct = mChunkTasks.get(i);
                ct.mStatus = FileDownloadStatus.paused;
                if (ct.future != null) {
                    ct.future.cancel(true);
                    ct.future = null;
                }
            }
        }
    }

    public void deleteSaveFile() {
        FileUtils.deleteFile(mSavePath);
    }

    public void cancel() {
        pause();
        for (int i = 0; i < mChunkTasks.size(); i++) {
            BaseChunkTask ct = mChunkTasks.get(i);
            ct.deleteSaveFile();
        }
        deleteSaveFile();
    }

    public void remove() {
        FileDownloader.getImpl().removeBean(this);
        future = null;
        mChunkTasks.clear();
        mListener = null;
    }

    private int getTotalLength(String urlStr) {
        HttpURLConnection connection = null;
        long time = System.currentTimeMillis();
        try {
            URL url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(600000);
            connection.connect();

            int totalByte = connection.getContentLength();
            if (LogUtils.isDebug()) {
                Log.i(TAG, "getTotalLength ok: " + urlStr + " totalSize " + FileUtils.formatFileSize(totalByte) + " " + (System.currentTimeMillis() - time));
            }
            return totalByte;
        } catch (Exception e) {
            if (LogUtils.isDebug()) {
                Log.i(TAG, "getTotalLength error: " + e.toString());
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return 0;
    }

    public String getPath() {
        return mSavePath;
    }

    public String getId() {
        if (mId == null) {
            mId = Md5Utils.md5(mUrl + mSavePath);
        }
        return mId;
    }

    public void setAutoRetryTimes(int autoRetryCountMax) {
        this.mAutoRetryCountMax = autoRetryCountMax;
    }

    public void addHeader(String key, String value) {
        if (mHeader == null) {
            mHeader = new HashMap<>();
        }
        mHeader.put(key, value);
    }

    public void setPath(String savePath) {
        this.mSavePath = savePath;
    }

    public void setForceReDownload(boolean forceReDownload) {
        this.mForceReDownload = forceReDownload;
    }

    public void setListener(FileDownloadSampleListener listener) {
        this.mListener = listener;
    }
}
