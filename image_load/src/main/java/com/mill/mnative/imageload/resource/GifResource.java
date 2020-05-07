package com.mill.mnative.imageload.resource;

import pl.droidsonroids.gif.GifDrawable;

public class GifResource implements Resource<GifDrawable> {
    private final GifDrawable gifDrawable;

    public GifResource(GifDrawable gifDrawable) {
        this.gifDrawable = gifDrawable;
    }

    @Override
    public Class<GifDrawable> getResourceClass() {
        return GifDrawable.class;
    }

    @Override
    public GifDrawable get() {
        return gifDrawable;
    }

    @Override
    public int getSize() {
        return (int) gifDrawable.getAllocationByteCount();
    }

    @Override
    public void recycle() {
        if (!gifDrawable.isRecycled()) {
//            gifDrawable.recycle();
        }
    }
}

