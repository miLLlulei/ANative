package com.mill.mnative;

import android.app.Activity;
import android.os.Bundle;

import com.mill.mnative.imageload.ImageLoaderImp;
import com.mill.mnative.net.HttpClientImp;
import com.mill.mnative.utils.ContextUtils;
import com.mill.mnative.utils.SPUtils;

public class BaseActivity extends Activity {
    public static final String SP_KEY_S_A_C = "S_A_C";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        if (!(this instanceof SplashActivity)) {
            SPUtils.setString(null, ContextUtils.getApplicationContext(), SP_KEY_S_A_C, this.getClass().getName());
        }
        ImageLoaderImp.getInstance().cancel(this);
        HttpClientImp.getInstance().cancel(this);
        super.onDestroy();
    }
}
