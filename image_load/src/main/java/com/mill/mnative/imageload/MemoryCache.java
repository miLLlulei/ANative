package com.mill.mnative.imageload;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.mill.mnative.utils.BitmapUtils;

public class MemoryCache extends LruCache<String, Bitmap> {

    public MemoryCache(int maxSize) {
        super(maxSize);
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return BitmapUtils.getBitmapSize(value);
    }
}
