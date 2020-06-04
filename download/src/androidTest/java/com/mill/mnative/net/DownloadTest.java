package com.mill.mnative.net;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.mill.mnative.download.DownloadMgr;
import com.mill.mnative.utils.LogUtils;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class DownloadTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("com.mill.mnative.net.test", appContext.getPackageName());
    }

    @Test
    public void testDownload() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        LogUtils.initInAppProcess(true, true, null);
        DownloadMgr.getInstance().init(appContext, true);

        DownloadMgr.getInstance().startDownload("http://upload.test1.mobilem.360.cn/2020521/app05210942.apk");

        assertEquals(1,1);
    }
}
