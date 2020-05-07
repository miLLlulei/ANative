package com.mill.mnative.imageload;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.mill.mnative.utils.BitmapUtils;
import com.mill.mnative.utils.FileUtils;

import java.io.File;

public class DiskCache {
    private Context context;
    private int size;
    private int maxSize;

    public DiskCache(Context context, int maxSize) {
        this.context = context;
        this.maxSize = maxSize;
        Utils.getSingleExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                DiskCache.this.size = getDiskCurrentSize();
            }
        });
    }

    private int getDiskCurrentSize() {
        if (this.size == 0) {
            synchronized (DiskCache.class) {
                if (this.size == 0) {
                    String dir = getDiskCacheDir(context);
                    this.size = (int) FileUtils.getDirectorySize(dir);
                    if (ImageLoaderImp.isDebug) {
                        Log.i(ImageLoaderImp.TAG, "DiskCache first_size " + dir + " " + FileUtils.formatFileSize(size));
                    }
                }
            }
        }
        return this.size;
    }

    public Bitmap get(String key) {
        if (key != null) {
            getDiskCurrentSize();
            File file = new File(DiskCache.getDiskCacheDir(context), key);
            if (file.exists() && file.length() > 0) {
                try {
                    file.setLastModified(System.currentTimeMillis());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return BitmapUtils.getBitmapFromFile(file, 720, 1280);
            }
        }
        return null;
    }

    public void put(String key, byte[] obj) {
        if (obj != null) {
            getDiskCurrentSize();
            File file = new File(DiskCache.getDiskCacheDir(context), key);
            FileUtils.writeBytesToFile(file, obj, 0, obj.length);
            size += file.length();
            if (ImageLoaderImp.isDebug) {
                Log.i(ImageLoaderImp.TAG, "DiskCache size " + key + " " + FileUtils.formatFileSize(size));
            }
            if (size > maxSize) {
                File oldFile = getOldestFile();
                size -= oldFile.length();
                try {
                    oldFile.delete();
                    if (ImageLoaderImp.isDebug) {
                        Log.i(ImageLoaderImp.TAG, "DiskCache over max " + oldFile.getName() + " " + FileUtils.formatFileSize(size));
                    }
                } catch (Exception e) {
                    if (ImageLoaderImp.isDebug) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void remove(String key) {
        try {
            File file = new File(DiskCache.getDiskCacheDir(context), key);
            file.delete();
        } catch (Exception e) {
            if (ImageLoaderImp.isDebug) {
                e.printStackTrace();
            }
        }
    }

    public File getOldestFile() {
        File diskDir = new File(getDiskCacheDir(context));
        long min = 0;
        File oldFile = null;
        for (String fileName : diskDir.list()) {
            File file = new File(diskDir, fileName);
            long flm = file.lastModified();
            if (min == 0 || min > flm) {
                min = flm;
                oldFile = file;
            }
        }
        return oldFile;
    }

    public static String getDiskCacheDir(Context context) {
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            if (Build.VERSION.SDK_INT >= 23 && context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                cachePath = context.getExternalCacheDir().getPath();
            } else if (context.getApplicationInfo().targetSdkVersion < 23) {
                cachePath = context.getExternalCacheDir().getPath();
            }
        }
        if (cachePath == null) {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath + File.separator + ImageLoadConfig.DISK_CACHE_DIR;
    }
}
