package com.mill.mnative.imageload;

import android.graphics.Bitmap;

public interface ImageCallback {
    void onImageSuccess(String url, Bitmap result);

    void onImageFail(String url, String error);
}
