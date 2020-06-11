package com.mill.mnative.download;

import android.content.Context;

import java.util.Map;

public interface IDownloadInfoMgr {

    public void init(Context context, boolean isDebug);

    public void startDownload(String url);

    public String startDownload(BaseDownloadBean info);

    public void pauseDownload(String id);

    public void pauseAll();

    public void cancelDownload(String id);

    public BaseDownloadBean getDownloadInfo(String id);

    public BaseDownloadBean getDownloadInfoByUrl(String url);

    public Map<String, BaseDownloadBean> getDownloadInfos();

    /**
     * 添加监听
     * 记得 deleteObserver，否则可能内存泄漏
     */
    public void addObserver(DownloadObserver observer);

    public void deleteObserver(DownloadObserver observer);
}