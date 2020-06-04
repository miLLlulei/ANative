package com.mill.mnative.download;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabaseCorruptException;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.mill.mnative.utils.FileUtils;
import com.mill.mnative.utils.LogUtils;
import com.mill.mnative.utils.LooperHandlerThread;

import java.util.HashMap;

public class DownloadDb {

    private static final String TAG = "DownloadResDB";
    private Context mContext;
    private int mTimes = 0;

    private final Handler mMainHandler = new Handler(Looper.getMainLooper());

    DownloadProvider downloadProvider = new DownloadProvider();

    public interface LoadDownloadDbFinish {
        void onLoadDbFinish(HashMap<String, BaseDownloadBean> mapDownloadResInfo);
    }

    private LooperHandlerThread looperThread;

    private DownloadDb() {

    }

    private static final DownloadDb instance = new DownloadDb();

    public static DownloadDb getInstance() {
        return instance;
    }

    public void initialize(Context context, LooperHandlerThread thread) {
        mContext = context;
        looperThread = thread;
    }

    public void loadAll(final LoadDownloadDbFinish loadDownloadDbFinish) {
        Cursor cursor = null;

        LogUtils.d(TAG, "loadAll begin");

        downloadProvider.onCreate();


        final HashMap<String, BaseDownloadBean> mapVals = new HashMap<>();
        try {
            cursor = downloadProvider.query(null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    BaseDownloadBean info = BaseDownloadBean.queryData(cursor);

                    if (FileUtils.IsFileExist(info.savePath)) {
                        if (FileUtils.getFileLen(info.savePath) == info.totalByte) {
                            info.status = FileDownloadStatus.completed;
                            info.curByte = info.totalByte;
                        }
                    }

                    if (info.status != FileDownloadStatus.completed) {
                        if (!TextUtils.isEmpty(info.savePath)) {
                            String p2pFile = info.savePath + ".temp";
                            long fileLen = FileUtils.getFileLen(p2pFile);
                            info.curByte = fileLen;
                        }
                    }

                    info.hasInDb = true;
                    LogUtils.d(TAG, "loadAll " + mapVals.size() + "   " + info.savePath + "   " + info.status);
                    mapVals.put(info.taskId, info);
                    cursor.moveToNext();
                } // end while
            }
        } catch (SQLiteDatabaseCorruptException e) {
//            android.database.sqlite.SQLiteDatabaseCorruptException: database disk image is malformed (code 11)
        } finally {
            if (cursor != null) {
                if (!cursor.isClosed()) {
                    cursor.close();
                }
            }
        }

        LogUtils.d(TAG, "loadAll end " + mapVals.size());
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                loadDownloadDbFinish.onLoadDbFinish(mapVals);
            }
        });
    }

    public static ContentValues ComposeReplaceValues(BaseDownloadBean info) {
        return BaseDownloadBean.composeValue(info);
    }

    private ContentValues ComposeUpdateValues(BaseDownloadBean info) {
        return BaseDownloadBean.composeValue(info);
    }

    public void replace(final BaseDownloadBean info) {
        looperThread.post(new Runnable() {
            @Override
            public void run() {
                ContentValues cv = ComposeReplaceValues(info);
                try {
                    downloadProvider.replace(cv);
                    info.hasInDb = true;

                    LogUtils.d(TAG, "replace " + info.taskId + " " + info.status + " mTotalBytes: " + info.totalByte + " " + info.savePath);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void updateDownloadInfo(final BaseDownloadBean info) {
        looperThread.post(new Runnable() {
            @Override
            public void run() {
                boolean bUpdateDb = false;
                if (info.status == FileDownloadStatus.progress) {
                    if (++mTimes == 10) {
                        mTimes = 0;
                        bUpdateDb = true;
                    }
                } else {
                    bUpdateDb = true;
                }

                if (bUpdateDb) {
                    mTimes = 0;
                    String where = String.format("%s = ?",
                            DownloadSqlConsts.COLUMN_DOWNLOAD_ID);
                    try {
                        downloadProvider.update(ComposeUpdateValues(info), where, new String[]{info.taskId});

                        LogUtils.d(TAG, "update " + info.taskId + " " + info.status + " mTotalBytes: " + info.totalByte + " " + info.savePath);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void delete(final String downloadId) {
        looperThread.post(new Runnable() {
            @Override
            public void run() {
                String whereClause = String.format("%s = ?", DownloadSqlConsts.COLUMN_DOWNLOAD_ID);
                downloadProvider.delete(whereClause, new String[]{downloadId});
            }
        });
    }
}
