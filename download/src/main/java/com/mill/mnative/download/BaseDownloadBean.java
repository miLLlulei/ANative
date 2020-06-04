package com.mill.mnative.download;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.mill.mnative.utils.SqliteUtils;

import java.util.Map;


/**
 * Created by lulei-ms on 2018/3/6.
 */
public class BaseDownloadBean {
    public static final String KEY_RES_TYPE = "res_type";
    public static final String TYPE_APK = "type_apk";
    public static final String TYPE_FILE = "type_file";
    public String taskId; //下载任务ID

    public String downloadUrl; //下载url
    public boolean fouceReDownload;
    public String savePath; //下载保存的路径
    public int status; //下载状态

    public String errorMsg; //失败日志,暂时不存数据库

    public long curByte;
    public long totalByte;
    public long firstDownloadTime;

    public boolean hasInDb;

    public Map<String, String> mapVal; //预留 extral字段

    public String getDownloadType(){
        String resType;
        if(mapVal != null && !TextUtils.isEmpty((resType = mapVal.get(KEY_RES_TYPE)))){
            return resType;
        }
        return TYPE_FILE;
    }

    public String getValue(String key){
        return mapVal != null? mapVal.get(key) : null;
    }

    @Override
    public String toString() {
        return "BaseDownloadBean{" +
                "taskId='" + taskId + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", fouceReDownload=" + fouceReDownload +
                ", savePath='" + savePath + '\'' +
                ", status=" + status +
                ", curByte=" + curByte +
                ", totalByte=" + totalByte +
                ", firstDownloadTime=" + firstDownloadTime +
                ", hasInDb=" + hasInDb +
                ", mapVal=" + mapVal +
                '}';
    }

    public static BaseDownloadBean queryData(Cursor c) {
        BaseDownloadBean info = new BaseDownloadBean();

        info.taskId = SqliteUtils.queryCursorStringVal(c, DownloadSqlConsts.COLUMN_DOWNLOAD_ID);
        info.savePath = SqliteUtils.queryCursorStringVal(c, DownloadSqlConsts.COLUMN_SAVED_PATH);
        info.curByte = SqliteUtils.queryCursorLongVal(c, DownloadSqlConsts.COLUMN_CURRENT_BYTES);
        info.totalByte = SqliteUtils.queryCursorLongVal(c, DownloadSqlConsts.COLUMN_TOTALBYTES);
        info.status = SqliteUtils.queryCursorIntVal(c, DownloadSqlConsts.COLUMN_DOWNLOAD_STATUS);
        info.downloadUrl = SqliteUtils.queryCursorStringVal(c, DownloadSqlConsts.COLUMN_DOWNLOAD_URL);
        info.firstDownloadTime = SqliteUtils.queryCursorLongVal(c, DownloadSqlConsts.COLUMN_START_DOWNLOAD_TIME);

        String extral = SqliteUtils.queryCursorStringVal(c, DownloadSqlConsts.COLUMN_EXTRA);
        if (!TextUtils.isEmpty(extral)) {
//            info.mapVal = GsonHelper.fromJson(extral, new TypeToken<HashMap<String, String>>() {
//            }.getType());
        }
        return info;
    }

    public static ContentValues composeValue(BaseDownloadBean info) {
        ContentValues value = new ContentValues();

        value.put(DownloadSqlConsts.COLUMN_DOWNLOAD_ID, info.taskId);
        value.put(DownloadSqlConsts.COLUMN_DOWNLOAD_URL, info.downloadUrl);
        value.put(DownloadSqlConsts.COLUMN_SAVED_PATH, info.savePath);
        value.put(DownloadSqlConsts.COLUMN_CURRENT_BYTES, info.curByte);
        value.put(DownloadSqlConsts.COLUMN_TOTALBYTES, info.totalByte);
        value.put(DownloadSqlConsts.COLUMN_START_DOWNLOAD_TIME, info.firstDownloadTime);
        value.put(DownloadSqlConsts.COLUMN_DOWNLOAD_STATUS, info.status);

        if (info.mapVal != null && !info.mapVal.isEmpty()) {
//            String extra = GsonHelper.getGsonInstance().toJson(info.mapVal);
//            value.put(DownloadSqlConsts.COLUMN_EXTRA, extra);
        }
        return value;
    }

}
