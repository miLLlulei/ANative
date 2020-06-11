package com.mill.mnative.download;

import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.mill.mnative.utils.ContextUtils;
import com.mill.mnative.utils.LogUtils;
import com.mill.mnative.utils.LooperHandlerThread;
import com.mill.mnative.utils.ThreadUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by lulei-ms on 2018/3/2.
 */
public class DownloadMgr implements IDownloadInfoMgr, DownloadDb.LoadDownloadDbFinish {
    public static final String TAG = DownloadGlobalObserver.class.getSimpleName();

    public static final int STATUS_DB_LOADALL_SUC = -6;
    public static final int AUTO_RETRY_COUNT_MAX = 3;

    private volatile static DownloadMgr mInstance;

    public static boolean DEBUG = true;
    private LooperHandlerThread dbThread = LooperHandlerThread.getGlobalThread();
    private HashMap<String, BaseDownloadBean> mapDownloadResInfo = new HashMap<>();
    private AtomicBoolean isInit = new AtomicBoolean(false);

    private DownloadMgr() {
        DownloadDb.getInstance().initialize(ContextUtils.getApplicationContext(), dbThread);
    }

    public static DownloadMgr getInstance() {
        if (mInstance == null) {
            synchronized (DownloadMgr.class) {
                if (mInstance == null) {
                    mInstance = new DownloadMgr();
                }
            }
        }
        return mInstance;
    }


    public void init(Context context, boolean isDebug) {
        if (!isInit.get()) {
            synchronized (DownloadMgr.class) {
                if (!isInit.get()) {
                    DEBUG = isDebug;
                    //跟数据库同步下
                    dbThread.post(new Runnable() {
                        @Override
                        public void run() {
                            DownloadDb.getInstance().loadAll(DownloadMgr.this);
                        }
                    });

                    if (DownloadMgr.DEBUG) {
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                if (DownloadMgr.DEBUG) {
                                    Log.d(DownloadMgr.TAG, " " + mapDownloadResInfo.size() + " " + DownloadGlobalObserver.getInstance().getObserverSize() + " " + FileDownloader.getImpl().getTaskSize());
                                    ThreadUtils.postOnUiThread(this, 5000);
                                }
                            }
                        };
                        ThreadUtils.postOnUiThread(runnable, 5000);
                    }
                    isInit.set(true);
                }
            }
        }
    }

    public void startDownload(String url) {
        init(ContextUtils.getApplicationContext(), LogUtils.isDebug());
        if (!TextUtils.isEmpty(url)) {
            BaseDownloadBean info = new BaseDownloadBean();
            info.downloadUrl = url;
            startDownload(info);
        }
    }

    private boolean checkInfo(BaseDownloadBean info) {
        if (info == null || TextUtils.isEmpty(info.downloadUrl)) {
            info.status = FileDownloadStatus.error;
            info.errorMsg = "url is null";
            DownloadGlobalObserver.getInstance().notifyObservers(info);
            return false;
        }
        return true;
    }

    public String startDownload(BaseDownloadBean info) {
        init(ContextUtils.getApplicationContext(), LogUtils.isDebug());
        if (!checkInfo(info)) {
            return "";
        }
        if (TextUtils.isEmpty(info.savePath)) {
            info.savePath = DownloadPath.getDefaultSaveFilePath(info.downloadUrl);
        }
        final BaseDownloadTask task = FileDownloader.getImpl().create(info.downloadUrl);
        task.setAutoRetryTimes(AUTO_RETRY_COUNT_MAX);
        task.addHeader("User-Agent", "HttpClientImp.getUserAgent()");
        task.setPath(info.savePath);
        task.setForceReDownload(info.fouceReDownload);
        task.setListener(DownloadGlobalObserver.getInstance());
        String taskId = task.start();
        info.taskId = String.valueOf(taskId);
        if (mapDownloadResInfo.get(taskId) != null) {
            info = mapDownloadResInfo.get(taskId);
        }
        if (info.firstDownloadTime == 0) {
            info.firstDownloadTime = System.currentTimeMillis();
        }
        mapDownloadResInfo.put(info.taskId, info);
        return info.taskId;
    }

    public void pauseDownload(final String id) {
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                init(ContextUtils.getApplicationContext(), LogUtils.isDebug());
                FileDownloader.getImpl().pause(id);
            }
        });
    }

    public void pauseAll() {
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (LogUtils.isDebug()) {
                    LogUtils.i(TAG, "pauseAll ");
                }
                init(ContextUtils.getApplicationContext(), LogUtils.isDebug());
                FileDownloader.getImpl().pauseAll();
            }
        });
    }

    public void cancelDownload(final String id) {
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                init(ContextUtils.getApplicationContext(), LogUtils.isDebug());
                BaseDownloadBean info = mapDownloadResInfo.get(id);
                if (info != null) {
                    FileDownloader.getImpl().clear(id);
                }
            }
        });
    }

    public BaseDownloadBean getDownloadInfo(String id) {
        init(ContextUtils.getApplicationContext(), LogUtils.isDebug());
        return mapDownloadResInfo.get(id);
    }

    public BaseDownloadBean getDownloadInfoByUrl(String url) {
        init(ContextUtils.getApplicationContext(), LogUtils.isDebug());
        if (!TextUtils.isEmpty(url)) {
            return getDownloadInfoByUrl(url, DownloadPath.getDefaultSaveFilePath(url));
        }
        return null;
    }

    public BaseDownloadBean getDownloadInfoByUrl(String url, String savePath) {
        init(ContextUtils.getApplicationContext(), LogUtils.isDebug());
        if (!TextUtils.isEmpty(url) && !TextUtils.isEmpty(savePath)) {
            Iterator<BaseDownloadBean> iterator = mapDownloadResInfo.values().iterator();
            while (iterator.hasNext()) {
                BaseDownloadBean item = iterator.next();
                if (item != null) {
                    if (url.equals(item.downloadUrl) && savePath.equals(item.savePath)) {
                        return item;
                    }
                }
            }
        }
        return null;
    }

    public Map<String, BaseDownloadBean> getDownloadInfos() {
        init(ContextUtils.getApplicationContext(), LogUtils.isDebug());
        return mapDownloadResInfo;
    }

    public BaseDownloadBean updateData(BaseDownloadBean info) {
        init(ContextUtils.getApplicationContext(), LogUtils.isDebug());

        if (info != null) {
            BaseDownloadBean infoInMap = getDownloadInfo(info.taskId);
            if (infoInMap != null) {
                saveDownloadInfo(infoInMap, false);
                DownloadGlobalObserver.getInstance().notifyObservers(info);
                return infoInMap;
            }
        }
        return null;
    }

    public void saveDownloadInfo(final BaseDownloadBean info, boolean bForceUpdateDb) {
        init(ContextUtils.getApplicationContext(), LogUtils.isDebug());

        if (bForceUpdateDb) {
            if (LogUtils.isDebug()) {
                LogUtils.d(TAG, "SaveDownloadInfo force true insert to db " + info.totalByte + " " + info.downloadUrl);
            }
            DownloadDb.getInstance().replace(info);
            return;
        }

        if (!info.hasInDb) {
            if (LogUtils.isDebug()) {
                LogUtils.d(TAG, "SaveDownloadInfo first time insert to db " + info.totalByte + " " + info.downloadUrl);
            }
            DownloadDb.getInstance().replace(info);
        } else {
            if (LogUtils.isDebug()) {
                LogUtils.d(TAG, "SaveDownloadInfo update to db " + info.totalByte + " " + info.downloadUrl);
            }
            DownloadDb.getInstance().updateDownloadInfo(info);
        }
    }

    public void addObserver(final DownloadObserver observer) {
        init(ContextUtils.getApplicationContext(), LogUtils.isDebug());
        if (LogUtils.isDebug()) {
            if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
                if (LogUtils.isDebug()) {
                    throw new RuntimeException("addObserver not in main thread!");
                }
            }
        }
        DownloadGlobalObserver.getInstance().addObserver(observer);
    }

    public void deleteObserver(final DownloadObserver observer) {
        init(ContextUtils.getApplicationContext(), LogUtils.isDebug());
        if (LogUtils.isDebug()) {
            if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
                throw new RuntimeException("deleteObserver not in main thread!");
            }
        }
        DownloadGlobalObserver.getInstance().deleteObserver(observer);
    }

    @Override
    public void onLoadDbFinish(HashMap<String, BaseDownloadBean> mapDownloadResInfo) {
        mergeIntoDownloadMap(mapDownloadResInfo);

        BaseDownloadBean bean = new BaseDownloadBean();
        bean.status = STATUS_DB_LOADALL_SUC;
        DownloadGlobalObserver.getInstance().notifyObservers(bean);
    }

    private void mergeIntoDownloadMap(Map<String, BaseDownloadBean> mapVals) {
        if (mapVals != null && mapDownloadResInfo != null) {
            for (Map.Entry<String, BaseDownloadBean> entry : mapVals.entrySet()) {
                if (entry != null) {
                    String key = entry.getKey();
                    BaseDownloadBean info = entry.getValue();
                    if (!TextUtils.isEmpty(key) && !mapDownloadResInfo.containsKey(key) && info != null) {
                        mapDownloadResInfo.put(key, info);
                    }
                }
            }
        }
    }
}
