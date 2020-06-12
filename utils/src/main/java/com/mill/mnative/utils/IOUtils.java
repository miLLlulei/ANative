package com.mill.mnative.utils;

import android.content.ClipData;
import android.content.Context;
import android.text.TextUtils;

import java.io.Closeable;
import java.io.IOException;

/**
 */
public class IOUtils {

    public static void close(Closeable closeable) {
        if (closeable == null) return;
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
            //close error
        }
    }

    public static boolean copyToClipboard(String content) {
        if (TextUtils.isEmpty(content)) {
            return false;
        }
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) ContextUtils.getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", content);
        clipboard.setPrimaryClip(clip);
        return true;
    }
}
