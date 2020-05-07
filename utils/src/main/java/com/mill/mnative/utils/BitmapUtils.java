package com.mill.mnative.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtils {

    public static int getBitmapSize(Bitmap bitmap) {
        if (bitmap != null) {
            try {
                if (Build.VERSION.SDK_INT >= 19) {
                    return bitmap.getAllocationByteCount();
                } else if (Build.VERSION.SDK_INT >= 12) {
                    return bitmap.getByteCount();
                } else {
                    return bitmap.getRowBytes() * bitmap.getHeight();
                }
            } catch (Exception e) {
            }
        }
        return 0;
    }

    public static Bitmap getBitmapFromFile(File dst, int width, int height) {
        if (null != dst && dst.exists()) {
            BitmapFactory.Options opts = null;
            if (width > 0 && height > 0) {
                opts = new BitmapFactory.Options();
                opts.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(dst.getPath(), opts);
                final int minSideLength = Math.min(width, height);
                opts.inSampleSize = computeSampleSize(opts, minSideLength, width * height);
                opts.inJustDecodeBounds = false;
                opts.inInputShareable = true;
                opts.inPurgeable = true;
            }
            try {
                return BitmapFactory.decodeFile(dst.getPath(), opts);
            } catch (OutOfMemoryError e) {
                try {
                    return BitmapFactory.decodeFile(dst.getPath(), opts);
                } catch (OutOfMemoryError e2) {
                    e2.printStackTrace();
                }
            }
        }
        return null;
    }

    private static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = maxNumOfPixels == -1 ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = minSideLength == -1 ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            return lowerBound;
        }

        if (maxNumOfPixels == -1 && minSideLength == -1) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    public static boolean savePicToPath(Bitmap b, File path, int quality, Bitmap.CompressFormat format) {
        if (b == null || path == null) {
            return false;
        }
        FileOutputStream fos = null;
        try {
            FileUtils.makeDir(path.getParentFile().getPath());
            fos = new FileOutputStream(path);
            boolean success = b.compress(format, quality, fos);
            fos.flush();
            return success;
        } catch (Exception e) {
            return false;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e2) {
                    if (LogUtils.isDebug()) {
                        e2.printStackTrace();
                    }
                }
            }
        }
    }
}
