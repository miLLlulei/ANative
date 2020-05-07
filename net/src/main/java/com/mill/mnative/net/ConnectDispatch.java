package com.mill.mnative.net;


import android.graphics.Bitmap;
import android.text.TextUtils;

import com.mill.mnative.utils.ThreadUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class ConnectDispatch {
    public List<Request> mRequestList = new ArrayList<>();
    private ExecutorService mExecutor;

    public ConnectDispatch(ExecutorService executor) {
        this.mExecutor = executor;
    }

    public void notifySuccess(final Request request, final String response) {
        if (request.callback != null) {
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (request.callback != null) {
                        request.callback.onNetSuccess(response);
                    }
                }
            });
        }
    }

    public void notifyFail(final Request request, final String error) {
        if (request.callback != null) {
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (request.callback != null) {
                        request.callback.onNetFail(error);
                    }
                }
            });
        }
    }

    public void addRequest(Request request) {
        if (!mRequestList.contains(request)) {
            mRequestList.add(request);
        }
    }

    public void removeRequest(Request request) {
        mRequestList.remove(request);
    }

    public void cancel(Object tag) {
        if (tag != null) {
            if (!mRequestList.isEmpty()) {
                Iterator<Request> it = mRequestList.iterator();
                while (it.hasNext()) {
                    Request request = it.next();
                    if (request.tag == tag) {
                        cancel(request, false);
                        it.remove();
                    }
                }
            }
        }
    }

    public void cancel(Request request, boolean removeFromList) {
        if (request.future != null) {
            try {
                request.future.cancel(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        request.tag = null;
        request.url = null;
        request.method = null;
        request.strParams = null;
        request.callback = null;
        request.future = null;
        if (removeFromList) {
            mRequestList.remove(request);
        }
    }

    public Request netAsync(Request request) {
        Future future = mExecutor.submit(new ConnectRunnable(request, this));
        request.future = future;
        addRequest(request);
        return request;
    }
}
