package com.mill.mnative.net;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class HttpClientImp {
    public static final String TAG = "HttpClientImp";

    public static volatile HttpClientImp sInstance;
    public static boolean isDebug = BuildConfig.DEBUG;
    public ConnectDispatch mDispatch;

    public static HttpClientImp getInstance() {
        if (sInstance == null) {
            synchronized (HttpClientImp.class) {
                if (sInstance == null) {
                    sInstance = new HttpClientImp();
                }
            }
        }
        return sInstance;
    }

    private HttpClientImp() {
        ExecutorService mExecutor = Executors.newFixedThreadPool(2, new ThreadFactory() {
            private final AtomicInteger mThreadId = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName(String.format("Mn_net_%d", this.mThreadId.getAndIncrement()));
                t.setPriority(Thread.NORM_PRIORITY);
                return t;
            }
        });
        mDispatch = new ConnectDispatch(mExecutor);
    }

    public void cancel(Object tag) {
        mDispatch.cancel(tag);
    }

    public void cancel(Request request) {
        mDispatch.cancel(request, true);
    }

    public Request getAsync(Object tag, String url, Map<String, String> params, NetCallback callback) {
        Request request = new Request();
        request.tag = tag;
        request.url = url;
        request.method = Request.METHOD_GET;
        request.strParams = params;
        request.callback = callback;
        return mDispatch.netAsync(request);
    }

    public Request postAsync(Object tag, String url, Map<String, String> params, NetCallback callback) {
        Request request = new Request();
        request.tag = tag;
        request.url = url;
        request.method = Request.METHOD_POST;
        request.strParams = params;
        request.callback = callback;
        return mDispatch.netAsync(request);
    }


}
