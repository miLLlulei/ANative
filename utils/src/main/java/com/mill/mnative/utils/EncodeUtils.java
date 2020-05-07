package com.mill.mnative.utils;

import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class EncodeUtils {

    public static String getDefaultCharset() {
        return "UTF-8";
    }

    public static String getAsciiString(byte[] value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return new String(value, StandardCharsets.US_ASCII);
        } else {
            return new String(value, Charset.forName("US-ASCII"));
        }
    }

    public static byte[] getAsciiBytes(String value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return value.getBytes(StandardCharsets.US_ASCII);
        } else {
            return value.getBytes(Charset.forName("US-ASCII"));
        }
    }

    public static String encodeAsUtf8(String val) {
        String utf8 = "";
        if (!TextUtils.isEmpty(val)) {
            try {
                utf8 = URLEncoder.encode(val, "UTF-8");
            } catch (UnsupportedEncodingException e) {
            }
        }
        return utf8;
    }

    // 使用Base64.NO_WRAP 没有换行。
    // Base64算法加密，当字符串过长（一般超过76）时会自动在中间加一个换行符，。用android.util.Base64.encodeToString加密时，多一android.util.Base64.NO_WRAP即可解决问题
    public static byte[] base64Dec(String str) {
        return Base64.decode(str, Base64.NO_WRAP);
    }

    public static String base64Enc(byte[] enBytes) {
        return Base64.encodeToString(enBytes, Base64.NO_WRAP);
    }


    public static String bytesToHex(byte[] bytes) {
        if (bytes != null) {
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < bytes.length; i++) {
                int v = bytes[i] & 0xFF;
                String hv = Integer.toHexString(v);
                if (hv.length() < 2) {
                    stringBuffer.append(0);
                }
                stringBuffer.append(hv);
            }
            return stringBuffer.toString();
        }
        return null;
    }
}
