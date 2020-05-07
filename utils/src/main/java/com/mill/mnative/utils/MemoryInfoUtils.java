package com.mill.mnative.utils;

import android.os.Build;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class MemoryInfoUtils {
    private static long sTotalMemory;

    /**
     * 获取设备当前可用内存大小，单位：b
     * 从Android 2.0_r1开始支持
     *
     * @return
     */
    public static long getFreeMemory() {

        Long freeMemory = (Long) ReflectUtils.invokeStaticMethod("android.os.Process", "getFreeMemory", null); // 单位：b
        if (freeMemory != null) {
            return freeMemory;
        }
        return -1;
    }

    /**
     * 获取设备总内存大小，单位：b
     *
     * @return
     */
    public static long getTotalMemory() {
        if (sTotalMemory <= 0) {
            long totalMemory = getTotalMemoryForProcess();
            if (totalMemory >= 0) {
                sTotalMemory = totalMemory;
            }
            if (sTotalMemory <= 0) {
                sTotalMemory = getTotalMemoryForFile() * 1024; // 将单位换算为Byte
            }
        }
        return sTotalMemory;
    }

    /**
     * 从"android.os.Process"对象读取总内存大小，单位：b；
     *
     * @return
     */
    private static long getTotalMemoryForProcess() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Long totalMemory = (Long) ReflectUtils.invokeStaticMethod("android.os.Process", "getTotalMemory", null); // 单位：b
            if (totalMemory != null) {
                return totalMemory;
            }
        }
        return -1;
    }


    /**
     * 从"/proc/meminfo"文件读取总内存大小，单位：kb；
     *
     * @return
     */
    private static long getTotalMemoryForFile() {
        // /proc/meminfo读出的内核信息进行解释
        String path = "/proc/meminfo";
        String content = null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(path), 8);
            String line;
            if ((line = br.readLine()) != null) {
                content = line;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (!TextUtils.isEmpty(content)) {
            int start = content.indexOf(':') + 1;
            int end = content.indexOf('k');
            if (start >= 0 && end >= 0) {
                content = content.substring(start, end).trim();
                return Long.parseLong(content);
            }
        }
        return -1;
    }
}