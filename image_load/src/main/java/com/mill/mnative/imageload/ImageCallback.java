package com.mill.mnative.imageload;

import com.mill.mnative.imageload.resource.Resource;

public interface ImageCallback {
    void onImageSuccess(String url, Resource result);

    void onImageFail(String url, String error);
}
