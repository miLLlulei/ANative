package com.mill.mnative.utils;

import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class FileUtils {
    private static final String TAG = "FileUtil";
    public static final int DEFAULT_BUFFER_SIZE = 8 * 1024;

    /**
     * 读取文件内容到字节数绿
     */
    public static byte[] readFileToBytes(File file) {
        byte[] bytes = null;
        if (file.exists()) {
            byte[] buffer;
            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;
            ByteArrayOutputStream baos = null;
            try {
                bis = new BufferedInputStream(new FileInputStream(file), DEFAULT_BUFFER_SIZE);
                baos = new ByteArrayOutputStream();
                bos = new BufferedOutputStream(baos, DEFAULT_BUFFER_SIZE);
                buffer = new byte[DEFAULT_BUFFER_SIZE];
                int len;
                while ((len = bis.read(buffer, 0, DEFAULT_BUFFER_SIZE)) != -1) {
                    bos.write(buffer, 0, len);
                }
                bos.flush();
                bytes = baos.toByteArray();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bos != null) {
                        bos.close();
                    }
                    if (baos != null) {
                        baos.close();
                    }
                    if (bis != null) {
                        bis.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bytes;
    }

    public static boolean writeBytesToFile(File file, byte[] bytes, long offset, int byteCount) {
        boolean isOk = false;
        if (!file.exists()) {
            try {
                isOk = file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (file.exists() && bytes != null && offset >= 0) {
            RandomAccessFile raf = null;
            try {
                raf = new RandomAccessFile(file, "rw");
                raf.seek(offset);
                raf.write(bytes, 0, byteCount);
                isOk = true;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (raf != null) {
                        raf.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return isOk;
    }


    public static boolean makeDir(String dir) {
        if (TextUtils.isEmpty(dir)) {
            return false;
        }

        File f = new File(dir);
        if (!f.exists()) {
            return f.mkdirs();
        }

        return true;
    }

    public static byte[] InputStreamTOByte(InputStream in) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[DEFAULT_BUFFER_SIZE];
        int count = -1;
        while ((count = in.read(data, 0, DEFAULT_BUFFER_SIZE)) != -1) {
            outStream.write(data, 0, count);
        }
        return outStream.toByteArray();
    }

    public static String formatFileSize(long fileS) {
        if (fileS < 0) {
            return "未知大小";
        }
        DecimalFormat df = new DecimalFormat("0.0");
        String fileSizeString;
        if (fileS < 1024) {
            fileSizeString = df.format(Rounding(fileS)) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format(Rounding((double) fileS / 1024)) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format(Rounding((double) fileS / 1048576)) + "M";
        } else {
            fileSizeString = df.format(Rounding((double) fileS / 1073741824)) + "G";
        }
        return fileSizeString;
    }

    private static double Rounding(double d) {
        BigDecimal bd = new BigDecimal(d);
        bd.setScale(1, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    // 获得一个目录下 所有文件的大小 包括子目录
    public static long getDirectorySize(String filePath) {
        if (filePath == null)
            return 0;

        File file = new File(filePath);
        if (!file.isDirectory())
            return 0;

        long size = 0;
        File list[] = file.listFiles();
        if (list != null) {
            for (int i = 0; i < list.length; i++) {
                if (list[i].isDirectory()) {
                    size += getDirectorySize(list[i].getAbsolutePath());
                } else {
                    size += list[i].length();
                }
            }
        }

        return size;
    }
}