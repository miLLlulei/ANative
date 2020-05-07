package com.mill.mnative.imageload;

import com.mill.mnative.utils.Md5Utils;

import java.util.concurrent.Future;

public class ImageRequest {

    public Object tag;
    String key;
    public String url;
    public int overWidth;
    public int overHeight;
    public Future future;
    public ImageCallback callback;

    public String getKey() {
        if (key == null) {
            key = Md5Utils.md5(url);
        }
        return key;
    }

    @Override
    public String toString() {
        return "ImageRequest{" +
                "key='" + key + '\'' +
                ", url='" + url + '\'' +
                ", overWidth=" + overWidth +
                ", overHeight=" + overHeight +
                '}';
    }
}
