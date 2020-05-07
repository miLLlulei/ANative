package com.mill.mnative.net;

public interface NetCallback {
    void onNetSuccess(String response);
    void onNetFail(String error);
}
