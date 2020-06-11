package com.mill.mnative.imageload;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.mill.mnative.imageload.resource.DefaultImageHeaderParser;
import com.mill.mnative.imageload.resource.ImageHeaderParser;
import com.mill.mnative.imageload.resource.Resource;
import com.mill.mnative.utils.BitmapUtils;
import com.mill.mnative.utils.ContextUtils;
import com.mill.mnative.utils.FileUtils;
import com.mill.mnative.utils.LooperHandlerThread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import pl.droidsonroids.gif.GifDrawable;

public class ImageLoaderImp {
    public static final String TAG = "ImageLoaderImp";
    public static boolean isDebug = true;

    private static volatile ImageLoaderImp sInstance;
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
        DiskCache mDiskCache = new DiskCache(ContextUtils.getApplicationContext(), ImageLoadConfig.MAX_DISK_SIZE);
        int maxCache = (int) (Runtime.getRuntime().maxMemory());
        int cacheSize = maxCache / 8;
        MemoryCache mMemoryCache = new MemoryCache(cacheSize);
        if (ImageLoaderImp.isDebug) {
            Log.i(ImageLoaderImp.TAG, "MemoryCache size: " + FileUtils.formatFileSize(cacheSize) + " DiskCache size: " + FileUtils.formatFileSize(ImageLoadConfig.MAX_DISK_SIZE));
        }
        ImageHeaderParser mHeaderParser = new DefaultImageHeaderParser();
        mDispatch = new ImageDispatch(mExecutor, mMemoryCache, mDiskCache, mHeaderParser);
    }

    public void init(Context context, boolean debug) {
        isDebug = debug;
    }

    public void setImageUrl(final ImageView imageView, final String url) {
        LooperHandlerThread.getGlobalThread().post(new Runnable() {
            @Override
            public void run() {
                cancel(imageView);
                final ImageRequest request = new ImageRequest();
                request.tag = imageView;
                request.url = url;
                request.callback = new ImageCallback() {
                    @Override
                    public void onImageSuccess(String url, Resource result) {
                        if (ImageLoaderImp.isDebug) {
//                            Log.i(ImageLoaderImp.TAG, "setImageUrl: " + url + " " + result);
                        }
                        if (result.getResourceClass() == Bitmap.class) {
                            imageView.setImageBitmap((Bitmap) result.get());
                        } else if (result.getResourceClass() == GifDrawable.class) {
                            imageView.setImageDrawable((GifDrawable) result.get());
                        }
                    }

                    @Override
                    public void onImageFail(String url, String error) {
                        if (ImageLoaderImp.isDebug) {
//                            Log.i(ImageLoaderImp.TAG, "setImageUrl: " + url + " " + error);
                        }
                        imageView.setImageBitmap(null);
                    }
                };
                mDispatch.loadImage(request);
            }
        });
    }

    public void getBitmap(final Object tag, final String url, final ImageCallback callback) {
        LooperHandlerThread.getGlobalThread().post(new Runnable() {
            @Override
            public void run() {
                ImageRequest request = new ImageRequest();
                request.tag = tag;
                request.url = url;
                request.callback = callback;
                mDispatch.loadImage(request);
            }
        });
    }

    public void cancel(Object tag) {
        mDispatch.cancel(tag);
    }

    public void cancel(ImageRequest request) {
        mDispatch.cancel(request, false, true);
    }

    public void clearAllCache() {
        mDispatch.clearAllCache();
    }

    public void onTerminate() {
        mDispatch.clearMemoryCache();
    }
}
