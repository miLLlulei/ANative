package com.mill.mnative.imageload.resource;

import com.mill.mnative.utils.BitmapUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;

import pl.droidsonroids.gif.GifDrawable;

public class ResourceUtils {

    public static Resource getResource(byte[] bytes, ImageHeaderParser headerParser) {
        try {
            ImageHeaderParser.ImageType type = headerParser.getType(new ByteArrayInputStream(bytes));
            if (type == ImageHeaderParser.ImageType.GIF) {
                return new GifResource(new GifDrawable(bytes));
            } else {
                return new BitmapResource(BitmapUtils.getBitmapFromBytes(bytes, 720, 1280));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new BitmapResource(BitmapUtils.getBitmapFromBytes(bytes, 720, 1280));
    }

    public static Resource getResource(File file, ImageHeaderParser headerParser) {
        try {
            ImageHeaderParser.ImageType type = headerParser.getType(new FileInputStream(file));
            if (type == ImageHeaderParser.ImageType.GIF) {
                return new GifResource(new GifDrawable(file));
            } else {
                return new BitmapResource(BitmapUtils.getBitmapFromFile(file, 720, 1280));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
