package com.mill.mnative.net;

import android.text.TextUtils;
import android.util.Log;

import java.util.Map;

public class MnativeNetUtils {

    public static String buildUrl(String url, Map<String, String> params) {
        if (!TextUtils.isEmpty(url) && params != null && !params.isEmpty()) {
            StringBuilder sb = new StringBuilder(url);
            int index = 0;
            int whIndex = url.indexOf("?");
            for (Map.Entry<String, String> param : params.entrySet()) {
                if (!TextUtils.isEmpty(param.getValue()) && !url.contains(param.getKey() + "=")) {
                    if (whIndex == -1 && index == 0) {
                        sb.append('?');
                    } else if (whIndex != url.length() - 1) {
                        sb.append('&');
                    }
                    sb.append(param.getKey());
                    sb.append('=');
                    sb.append(param.getValue());
                    index++;
                }
            }
            if (HttpClientImp.isDebug) {
                Log.d(HttpClientImp.TAG, "url: " + url + " " + sb.toString());
            }
            return sb.toString();
        }
        return url;
    }

    public static String buildStr(Map<String, String> params) {
        if (params != null && !params.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            int index = 0;
            for (Map.Entry<String, String> param : params.entrySet()) {
                if (!TextUtils.isEmpty(param.getValue())) {
                    if (index != 0) {
                        sb.append('&');
                    }
                    sb.append(param.getKey());
                    sb.append('=');
                    sb.append(param.getValue());
                    index++;
                }
            }
            if (HttpClientImp.isDebug) {
                Log.d(HttpClientImp.TAG, "buildStr: " + sb.toString());
            }
            return sb.toString();
        }
        return "";
    }
}
