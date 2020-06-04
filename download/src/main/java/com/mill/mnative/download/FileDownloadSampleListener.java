package com.mill.mnative.download;

public class FileDownloadSampleListener {

    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {

    }

    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {

    }

    protected void blockComplete(BaseDownloadTask task) {

    }

    protected void completed(BaseDownloadTask task) {

    }

    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

    }

    protected void error(BaseDownloadTask task, Throwable e) {

    }

    protected void warn(BaseDownloadTask task) {

    }

    protected void retry(BaseDownloadTask task, Throwable ex, int retryingTimes, int soFarBytes) {

    }
}