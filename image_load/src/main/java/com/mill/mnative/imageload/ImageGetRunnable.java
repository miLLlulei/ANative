package com.mill.mnative.imageload;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.mill.mnative.utils.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageGetRunnable implements Runnable {
    private ImageRequest mRequest;
    private MemoryCache mMemoryCache;
    private DiskCache mDiskCache;
    private ImageDispatch mImageDispatch;

    public ImageGetRunnable(ImageRequest request, MemoryCache memoryCache, DiskCache diskCache, ImageDispatch imageDispatch) {
        this.mRequest = request;
        this.mMemoryCache = memoryCache;
        this.mDiskCache = diskCache;
        this.mImageDispatch = imageDispatch;
    }

    @Override
    public void run() {
        String key = mRequest.getKey();
        Bitmap result = getFromMemory(key);
        if (result != null) {
            if (ImageLoaderImp.isDebug) {
                Log.i(ImageLoaderImp.TAG, "image get run: Memory " + mRequest.url + " " + result);
            }
        }
        if (result == null) {
            result = getFromDisk(key);
            if (result != null) {
                putToMemory(key, result);
                if (ImageLoaderImp.isDebug) {
                    Log.i(ImageLoaderImp.TAG, "image get run: Disk " + mRequest.url + " " + result);
                }
            }
        }
        if (result == null) {
            try {
                byte[] bytes = getFromDownload(mRequest.url);
                if (bytes != null && bytes.length > 0) {
                    result = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    putToMemory(key, result);
                    putToDisk(key, bytes);
                    if (ImageLoaderImp.isDebug) {
                        Log.i(ImageLoaderImp.TAG, "image get run: Download " + mRequest.url + " " + FileUtils.formatFileSize((bytes == null ? 0 : bytes.length)));
                    }
                }
            } catch (Exception e) {
                if (ImageLoaderImp.isDebug) {
                    Log.i(ImageLoaderImp.TAG, "download error " + mRequest.url + " " + e.toString());
                }
                mImageDispatch.notifyFail(key, e.toString());
            }
        }
        if (result != null) {
//            if (ImageLoaderImp.isDebug) {
//                Log.i(ImageLoaderImp.TAG, "image get run: ok " + mRequest.url + " " + result);
//            }
            mImageDispatch.notifySuccess(key, result);
        }
    }

    private Bitmap getFromMemory(String key) {
        return mMemoryCache.get(key);
    }

    private void putToMemory(String key, Bitmap bytes) {
        mMemoryCache.put(key, bytes);
    }

    private Bitmap getFromDisk(String key) {
        return mDiskCache.get(key);
    }

    private void putToDisk(String key, byte[] obj) {
        mDiskCache.put(key, obj);
    }

    public byte[] getFromDownload(String urlStr) throws Exception {
        HttpURLConnection connection = null;
        InputStream in = null;
        ByteArrayOutputStream os = null;
        try {
            if (ImageLoaderImp.isDebug) {
                Log.i(ImageLoaderImp.TAG, "download request: " + urlStr);
            }
            URL url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(20000);
            connection.connect();

            in = connection.getInputStream();
            int bufferSize = FileUtils.DEFAULT_BUFFER_SIZE;
            int totalSize = connection.getContentLength();
            os = new ByteArrayOutputStream();
            byte[] data = new byte[bufferSize];
            int count = -1;
            int current = 0;
            while ((count = in.read(data, 0, bufferSize)) != -1) {
                current += count;
                os.write(data, 0, count);
//                if (ImageLoaderImp.isDebug) {
//                    Log.i(ImageLoaderImp.TAG, "download progress: " + urlStr + " " + totalSize + " " + current + " " + (current * 100 / totalSize));
//                }
            }
            return os.toByteArray();
        } catch (Exception e) {
            throw e;
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
