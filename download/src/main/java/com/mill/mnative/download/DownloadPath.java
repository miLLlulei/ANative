package com.mill.mnative.download;

import android.content.Context;

import com.mill.mnative.utils.ContextUtils;
import com.mill.mnative.utils.Md5Utils;

import java.io.File;


public class DownloadPath {
    private static final String TAG = "DownloadPath";

    private static final String DIR = "download";

    public static String getDefaultDownloadDir() {
        return ContextUtils.getApplicationContext().getDir(DIR, Context.MODE_PRIVATE).getPath();
    }

    public static String getDefaultSaveFilePath(String url) {
        return getDefaultDownloadDir() + File.separator + Md5Utils.md5(url);
    }
}
