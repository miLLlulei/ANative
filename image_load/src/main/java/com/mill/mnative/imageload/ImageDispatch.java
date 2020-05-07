package com.mill.mnative.imageload;


import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;

import com.mill.mnative.utils.Md5Utils;
import com.mill.mnative.utils.ThreadUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class ImageDispatch {
    public List<ImageRequest> mRequestList = new ArrayList<>();
    private ExecutorService mSingleExecutor = Utils.getSingleExecutorService();
    private ExecutorService mExecutor;
    private MemoryCache mMemoryCache;
    private DiskCache mDiskCache;

    public ImageDispatch(ExecutorService mExecutor, MemoryCache mMemoryCache, DiskCache mDiskCache) {
        this.mExecutor = mExecutor;
        this.mMemoryCache = mMemoryCache;
        this.mDiskCache = mDiskCache;
    }

    public void notifySuccess(final String key, final Bitmap bitmap) {
        synchronized (ImageDispatch.class) {
            Iterator<ImageRequest> it = mRequestList.iterator();
            while (it.hasNext()) {
                final ImageRequest request = it.next();
                if (TextUtils.equals(request.key, key)) {
                    ThreadUtils.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (request.callback != null) {
                                request.callback.onImageSuccess(request.url, bitmap);
                            }
                            cancel(request, false, false);
                        }
                    });
                    it.remove();
                }
            }
        }
    }

    public void notifyFail(final String key, final String error) {
        synchronized (ImageDispatch.class) {
            Iterator<ImageRequest> it = mRequestList.iterator();
            while (it.hasNext()) {
                final ImageRequest request = it.next();
                if (TextUtils.equals(request.key, key)) {
                    ThreadUtils.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (request.callback != null) {
                                request.callback.onImageFail(request.url, error);
                            }
                            cancel(request, false, false);
                        }
                    });
                    it.remove();
                }
            }
        }
    }

    public void addRequest(ImageRequest request) {
        synchronized (ImageDispatch.class) {
            mRequestList.add(request);
        }
    }

    public ImageRequest getFirstImageRequest(String key) {
        synchronized (ImageDispatch.class) {
            for (ImageRequest request : mRequestList) {
                if (TextUtils.equals(request.key, key)) {
                    return request;
                }
            }
        }
        return null;
    }

    public void removeRequest(ImageRequest request) {
        synchronized (ImageDispatch.class) {
            mRequestList.remove(request);
        }
    }

    public void cancel(Object tag) {
        synchronized (ImageDispatch.class) {
            if (tag != null) {
                if (!mRequestList.isEmpty()) {
                    Iterator<ImageRequest> it = mRequestList.iterator();
                    while (it.hasNext()) {
                        ImageRequest request = it.next();
                        if (request.tag == tag || (request.tag instanceof View && ((View) request.tag).getContext() == tag)) {
                            cancel(request, false, false);
                            it.remove();
                        }
                    }
                }
            }
        }
    }

    public void cancel(ImageRequest request, boolean cancelRunnable, boolean removeFromList) {
        if (cancelRunnable) {
            if (request.future != null) {
                try {
                    request.future.cancel(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        request.tag = null;
        request.key = null;
        request.url = null;
        request.future = null;
        request.callback = null;
        if (removeFromList) {
            removeRequest(request);
        }
    }

    public void clearAllCache() {
        mMemoryCache.evictAll();
    }

    public void clearCache(String url) {
        String key = Md5Utils.md5(url);
        mMemoryCache.remove(key);
        mDiskCache.remove(key);
    }

    public ImageRequest loadImage(final ImageRequest request) {
        mSingleExecutor.execute(new Runnable() {
            @Override
            public void run() {
                ImageRequest first = getFirstImageRequest(request.getKey());
                Future future = null;
                if (first != null) {
                    future = first.future;
                } else {
                    future = mExecutor.submit(new ImageGetRunnable(request, mMemoryCache, mDiskCache, ImageDispatch.this));
                }
                request.future = future;
                addRequest(request);
            }
        });
        return request;
    }
}
