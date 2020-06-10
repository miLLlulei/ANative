package com.mill.mnative.download;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class FileDownloader {
    public static final int CHUNK_COUNT = 3;
    private ThreadPoolExecutor CHUNK_EXECUTOR = new ThreadPoolExecutor(CHUNK_COUNT, CHUNK_COUNT, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(128), new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "FileDownloader #" + mCount.getAndIncrement());
        }
    });
    private Map<String, BaseDownloadTask> mTaskMap = new HashMap<>();

    private static final class HolderClass {
        private static final FileDownloader INSTANCE = new FileDownloader();
    }

    public static FileDownloader getImpl() {
        return HolderClass.INSTANCE;
    }

    private FileDownloader() {
    }

    public ThreadPoolExecutor getExecutor() {
        return CHUNK_EXECUTOR;
    }

    public String execute(BaseDownloadTask task) {
        if (task != null) {
            BaseDownloadTask old = mTaskMap.put(task.getId(), task);
            if (old != null) {
                old.pause();
            }
            task.future = getExecutor().submit(task);
            return task.getId();
        }
        return "";
    }

    public BaseDownloadTask create(final String url) {
        return new BaseDownloadTask(url);
    }

    public int getTaskSize() {
        return mTaskMap.size();
    }

    public void pause(String id) {
        BaseDownloadTask task = mTaskMap.get(id);
        if (task != null) {
            task.pause();
            mTaskMap.remove(id);
        }
    }

    public void pauseAll() {
        for (BaseDownloadTask task : mTaskMap.values()) {
            task.pause();
        }
        mTaskMap.clear();
    }

    public void removeBean(BaseDownloadTask task) {
        if (mTaskMap.containsValue(task)) {
            mTaskMap.remove(task.getId());
        }
    }

    public void clear(String id) {
        BaseDownloadTask task = mTaskMap.get(id);
        if (task != null) {
            task.cancel();
            removeBean(task);
        }
    }
}
