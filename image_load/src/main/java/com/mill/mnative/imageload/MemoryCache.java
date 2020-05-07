package com.mill.mnative.imageload;

import android.util.LruCache;

import com.mill.mnative.imageload.resource.Resource;

public class MemoryCache extends LruCache<String, Resource> {

    public MemoryCache(int maxSize) {
        super(maxSize);
    }

    @Override
    protected int sizeOf(String key, Resource value) {
        return value.getSize();
    }
}
