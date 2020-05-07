package com.mill.mnative.imageload;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.mill.mnative.utils.FileUtils;
import com.mill.mnative.utils.MemoryInfoUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ImageLoaderImp {
    public static final String TAG = "ImageLoaderImp";

    public static volatile ImageLoaderImp sInstance;
    public static boolean isDebug = true;

    private Context mContext;
    private ImageDispatch mDispatch;

    public static ImageLoaderImp getInstance() {
        if (sInstance == null) {
            synchronized (ImageLoaderImp.class) {
                if (sInstance == null) {
                    sInstance = new ImageLoaderImp();
                }
            }
        }
        return sInstance;
    }

    private ImageLoaderImp() {
    }

    public void init(Context context) {
        if (mContext == null) {
            this.mContext = context.getApplicationContext();
            ExecutorService mExecutor = Executors.newFixedThreadPool(3, new ThreadFactory() {
                private final AtomicInteger mThreadId = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r);
                    t.setName(String.format("Mn_imageload_%d", this.mThreadId.getAndIncrement()));
                    t.setPriority(Thread.NORM_PRIORITY);
                    return t;
                }
            });
            DiskCache mDiskCache = new DiskCache(mContext, ImageLoadConfig.MAX_DISK_SIZE);
            int maxCache = (int) (Runtime.getRuntime().maxMemory());
            int cacheSize = maxCache / 8;
            MemoryCache mMemoryCache = new MemoryCache(cacheSize);
            if (ImageLoaderImp.isDebug) {
                Log.i(ImageLoaderImp.TAG, "MemoryCache size: " + FileUtils.formatFileSize(cacheSize) + " DiskCache size: " + FileUtils.formatFileSize(ImageLoadConfig.MAX_DISK_SIZE));
            }
            mDispatch = new ImageDispatch(mExecutor, mMemoryCache, mDiskCache);
        }
    }

    public Context getAppContext() {
        return this.mContext;
    }

    public void setImageUrl(final ImageView imageView, String url) {
        cancel(imageView);
        ImageRequest request = new ImageRequest();
        request.tag = imageView;
        request.url = url;
        request.callback = new ImageCallback() {
            @Override
            public void onImageSuccess(String url, Bitmap result) {
                imageView.setImageBitmap(result);
            }

            @Override
            public void onImageFail(String url, String error) {
                imageView.setImageBitmap(null);
            }
        };
        mDispatch.loadImage(request);
    }

    public void getBitmap(Object tag, String url, ImageCallback callback) {
        ImageRequest request = new ImageRequest();
        request.tag = tag;
        request.url = url;
        request.callback = callback;
        mDispatch.loadImage(request);
    }

    public void cancel(Object tag) {
        mDispatch.cancel(tag);
    }

    public void cancel(ImageRequest request) {
        mDispatch.cancel(request, false, true);
    }

    public void clearCache() {

    }
}
