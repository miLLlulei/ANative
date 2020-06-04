package com.mill.mnative.utils;

import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

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
        makeDir(file.getParent());
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

    /**
     * 根据路径删除指定的目录或文件，无论存在与吿
     *
     * @param sPath 要删除的目录或文仿
     * @return 删除成功返回 true，否则返囿false〿
     */
    public static boolean deleteFileOrDirectory(String sPath) {
        File file = new File(sPath);
        // 判断目录或文件是否存圿
        if (!file.exists()) { // 不存在返囿false
            return false;
        } else {
            // 判断是否为文仿
            if (file.isFile()) { // 为文件时调用删除文件方法
                return deleteFile(sPath);
            } else { // 为目录时调用删除目录方法
                return deleteDirectory(sPath);
            }
        }
    }

    /**
     * 删除单个文件
     *
     * @param sPath 被删除文件的文件吿
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String sPath) {
        if (TextUtils.isEmpty(sPath)) {
            return false;
        }
        boolean flag = false;
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            flag = file.delete();
        }
        return flag;
    }

    /**
     * 删除目录（文件夹）以及目录下的文件
     *
     * @param sPath 被删除目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String sPath) {
        return deleteDirectoryImp(sPath, null);
    }

    public interface IDeleteFileFilter {
        boolean isDelete(File file);
    }

    public static boolean deleteDirectoryImp(String sPath, IDeleteFileFilter deleteFileFilter) {
        // 如果sPath不以文件分隔符结尾，自动添加文件分隔笿
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        // 如果dir对应的文件不存在，或者不是一个目录，则ꀥǿ
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        // 删除文件夹下的所有文仿包括子目彿
        File[] files = dirFile.listFiles();
        if (files == null) {
            return false;
        }
        for (int i = 0; i < files.length; i++) {
            // 删除子文仿
            if (files[i].isFile()) {
                if (deleteFileFilter == null || deleteFileFilter.isDelete(files[i])) {
                    flag = deleteFile(files[i].getAbsolutePath());
                    if (!flag) {
                        break;
                    }
                }
            } // 删除子目彿
            else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
        }
        if (!flag) {
            return false;
        }
        // 删除当前目录
        return dirFile.delete();
    }

    public static long getFileLen(String file) {
        if (file == null)
            return 0;
        File f = new File(file);
        return f.length();
    }

    public static boolean IsFileExist(String file) {
        if (TextUtils.isEmpty(file)) {
            return false;
        }
        File f = new File(file);
        return f.exists();
    }

    public static boolean mergeTempFile(List<String> tempPaths, String savePath, boolean deleteTemp) {
        if (tempPaths != null && !tempPaths.isEmpty()) {
            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;
            try {
                bos = new BufferedOutputStream(new FileOutputStream(savePath), DEFAULT_BUFFER_SIZE);
                for (String temp : tempPaths) {
                    try {
                        bis = new BufferedInputStream(new FileInputStream(temp), DEFAULT_BUFFER_SIZE);
                        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
                        int len;
                        while ((len = bis.read(buffer, 0, DEFAULT_BUFFER_SIZE)) != -1) {
                            bos.write(buffer, 0, len);
                        }
                        bos.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (bis != null) {
                                bis.close();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (deleteTemp) {
                        deleteFile(temp);
                    }
                }
                bos.flush();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bos != null) {
                        bos.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}