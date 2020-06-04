package com.mill.mnative.download;

import android.util.Log;

import com.mill.mnative.utils.FileUtils;
import com.mill.mnative.utils.LogUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;


/**
 * 多线程下载
 * 分块下载
 */
public class BaseChunkTask extends BaseDownloadTask {

    public BaseChunkTask(String url, String savePath, int start, int end, boolean forceReDownload, int autoRetryCountMax, Map<String, String> header, FileDownloadSampleListener listener) {
        this.mUrl = url;
        this.mSavePath = savePath;
        this.mStart = start;
        this.mEnd = end;
        this.mForceReDownload = forceReDownload;
        this.mAutoRetryCountMax = autoRetryCountMax;
        this.mHeader = header;
        this.mListener = listener;
    }

    @Override
    public void run() {
        if (FileDownloadStatus.isOver(mStatus)) {
            remove();
            if (LogUtils.isDebug()) {
                LogUtils.i(TAG, "BaseChunkTask 任务 S isOver true");
            }
            return;
        }
        if (mForceReDownload) {
            boolean delSave = FileUtils.deleteFile(mSavePath);
            boolean delTemp = FileUtils.deleteFile(buildTempFilePath(mSavePath));
            if (LogUtils.isDebug()) {
                LogUtils.i(TAG, "ForceReDownload C 删除文件: " + delSave + " " + delTemp + " " + mSavePath);
            }
        }
        downloadImp(mUrl, mSavePath, mStart, mEnd);
    }

    private String buildTempFilePath(String savePath) {
        return savePath + ".temp";
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
        super.completed(task, total);
    }

    public void error(BaseDownloadTask task, Throwable e) {
        if (mRetryCount < mAutoRetryCountMax) {
            downloadImp(mUrl, mSavePath, mStart, mEnd);
        } else {
            mStatus = FileDownloadStatus.error;
            if (mListener != null) {
                mListener.error(task, e);
            }
            remove();
        }
    }

    private void downloadImp(String urlStr, String savePath, long start, long end) {
        mRetryCount++;
        HttpURLConnection connection = null;
        InputStream in = null;
        RandomAccessFile os = null;
        long time = System.currentTimeMillis();
        try {
            // 文件已存在
            long totalByte = FileUtils.getFileLen(savePath);
            if (totalByte > 1024) {
                completed(this, (int) totalByte);
                return;
            }
            String tempFilePath = buildTempFilePath(savePath);
            long curByte = FileUtils.getFileLen(tempFilePath);
            if (LogUtils.isDebug()) {
                LogUtils.i(TAG, "download request: " + urlStr + " " + start + " " + curByte + " " + end);
            }
            if (curByte + start == end && end != 0) {
                // temp 文件已下完
            } else {
                // 支持断点下载
                if (curByte + start > end && end != 0) {
                    FileUtils.deleteFile(tempFilePath);
                    curByte = 0;
                }
                URL url = new URL(urlStr);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(600000);
                connection.addRequestProperty("RANGE", "bytes=" + (curByte + start) + "-" + (end == 0 ? "" : end));
                connection.connect();

                in = connection.getInputStream();
                int bufferSize = 8 * 1024;
                int totalSize = connection.getContentLength();
                totalByte = curByte + totalSize;
                os = new RandomAccessFile(tempFilePath, "rwd");
                os.seek(curByte);
                byte[] data = new byte[bufferSize];
                int count = -1;
                int current = (int) curByte;
                long curTime = System.currentTimeMillis();
                while ((count = in.read(data, 0, bufferSize)) != -1) {
                    current += count;
                    os.write(data, 0, count);
                    if (FileDownloadStatus.isOver(mStatus)) {
                        remove();
                        if (LogUtils.isDebug()) {
                            LogUtils.i(TAG, "BaseChunkTask 任务 P isOver true");
                        }
                        return;
                    }
                    if (System.currentTimeMillis() - curTime >= 300) {
                        curByte = current;
                        progress(this, (int) curByte, (int) totalByte);
                        curTime = System.currentTimeMillis();
                    }
                }
                os.close();
            }
            FileUtils.deleteFile(savePath);
            boolean rename = new File(tempFilePath).renameTo(new File(savePath));
            if (rename) {
                completed(this, (int) (totalByte));
            } else {
                FileUtils.deleteFile(tempFilePath);
                error(this, new Exception("rename error"));
            }
            if (LogUtils.isDebug()) {
                Log.i(TAG, "download ok: " + rename + " " + urlStr + " " + savePath + " " + FileUtils.formatFileSize(totalByte) + " " + (System.currentTimeMillis() - time));
            }
        } catch (Exception e) {
            error(this, e);
            if (LogUtils.isDebug()) {
                Log.i(TAG, "download error: " + e.toString());
            }
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
