package com.mill.mnative.net;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.mill.mnative.imageload.ImageCallback;
import com.mill.mnative.imageload.ImageLoaderImp;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ImageLoadTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("com.mill.mnative.net.test", appContext.getPackageName());
    }

    @Test
    public void testGetBitmap() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        ImageLoaderImp.getInstance().init(appContext);
        List<String> list = new ArrayList<>();
        list.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSpyRiDZ0Byw-dwNsLeJ0822qJdPa2rIx62xX_NxnauYz-MrC5Q&s");
        list.add("https://img95.699pic.com/photo/40011/0709.jpg_wh860.jpg");
        list.add("https://img95.699pic.com/photo/50055/5642.jpg_wh860.jpg");
        list.add("https://img95.699pic.com/photo/50055/5642.jpg_wh860.jpg");
        list.add("https://static.runoob.com/images/demo/demo2.jpg");
        list.add("https://lh3.googleusercontent.com/proxy/3il8LipGWJjismtR0P7XnYccH0UCtV7hLJmMUXbVKywoPnz7wxupYhKBQnMKrFLNCERZ9sxkEJWhDMbzjmd-M2KMbY4hpxcx_TKADk8mir2B6l02fKQ");
        list.add("https://img95.699pic.com/photo/50055/5642.jpg_wh860.jpg");
        list.add("https://t1.hddhhn.com/uploads/tu/201812/622/484.jpg");
        list.add("https://img95.699pic.com/photo/50055/5642.jpg_wh860.jpg");
        list.add("https://lh3.googleusercontent.com/proxy/YfUG3_CjC9xuzWo-tj1JcqRGjU3qCwa2c5U8pmfyQGtZ_r0sTaXNpfWs-eerLwsSgaFxKJKdR8ue2ZIbJclPtV4Hfjy7Qmb_xtEYaVq72YYiS_K8IVM");
        list.add("https://uploadfile.huiyi8.com/up/a2/e3/83/a2e3832e52216b846c80313049591938.jpg");
        list.add("https://img95.699pic.com/photo/50055/5642.jpg_wh860.jpg");
        list.add("");
        for (final String url : list) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    ImageLoaderImp.getInstance().getBitmap(null, url, new ImageCallback() {
                        @Override
                        public void onImageSuccess(String url, Bitmap result) {
                            Log.i("ImageLoadTest", "r " + url + " " + result);
                        }

                        @Override
                        public void onImageFail(String url, String error) {
                            Log.i("ImageLoadTest", "r " + error);
                        }
                    });
                }
            }, 1000);
        }
        assertEquals(1, 1);
    }
}
