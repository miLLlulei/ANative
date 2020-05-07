package com.mill.mnative.imageload.resource;

import android.graphics.Bitmap;

import com.mill.mnative.utils.BitmapUtils;

public class BitmapResource implements Resource<Bitmap> {
    private final Bitmap bitmap;

    public BitmapResource(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public Class<Bitmap> getResourceClass() {
        return Bitmap.class;
    }

    @Override
    public Bitmap get() {
        return bitmap;
    }

    @Override
    public int getSize() {
        return BitmapUtils.getBitmapSize(bitmap);
    }

    @Override
    public void recycle() {

    }
}

