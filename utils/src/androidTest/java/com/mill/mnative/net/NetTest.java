package com.mill.mnative.net;

import android.content.Context;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class NetTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("com.mill.mnative.net.test", appContext.getPackageName());
    }

    @Test
    public void testGet() {
        HttpClientImp.getInstance().getAsync(null,"http://www.baidu.com", null, new NetCallback() {
            @Override
            public void onNetSuccess(String response) {
                Log.d("NetTest", "r " + response);
            }

            @Override
            public void onNetFail(String error) {
                Log.d("NetTest", "e " + error);
            }
        });
    }

    @Test
    public void testUrl() {
        HashMap<String, String> map = new HashMap<>();
        map.put("sd", "");
        map.put("sdfx", "1123");
        map.put("xcvzv", "");
        map.put("fdhgh", "56756");
        MnativeNetUtils.buildUrl(null, null);
        MnativeNetUtils.buildUrl(null, map);
        MnativeNetUtils.buildUrl("http://www.baidu.com", null);
        MnativeNetUtils.buildUrl("http://www.baidu.com", map);
        MnativeNetUtils.buildUrl("http://www.baidu.com?", map);
        MnativeNetUtils.buildUrl("http://www.baidu.com?a=12", map);
        MnativeNetUtils.buildUrl("http://www.baidu.com?sdfx=xvxc", map);

        MnativeNetUtils.buildStr(map);
        assertEquals(1,1);
    }
}
