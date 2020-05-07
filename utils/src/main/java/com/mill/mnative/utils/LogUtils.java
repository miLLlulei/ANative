package com.mill.mnative.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Process;
import android.os.SystemClock;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.util.Pair;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

public class LogUtils {
    public static final String TAG = "LogUtils";

    private static final long LOGABLE_DELAY = 2000;

    public static File LogPath;
    private static final String FORMAT_STR = "%S[%s] %s\n";

    private static AtomicBoolean isDebug = new AtomicBoolean(false);
    private static AtomicBoolean isWriteLogFile = new AtomicBoolean(false);
    private static AtomicBoolean initilized = new AtomicBoolean(false);

    public static AtomicBoolean canLog = new AtomicBoolean(false);//是不是可以显示日志，如果不显示，就不要构造log的message了，浪费内存

    private static ThreadPoolExecutor mExecutor;

    public static long START_TIME = SystemClock.elapsedRealtime(); // 返回系统启动到现在的毫秒数，包含休眠时间

    public static boolean isWriteLogFile() {
        return isWriteLogFile.get();
    }

    public static String getLogPath() {
        Context context = ContextUtils.getApplicationContext();
        File dir = context.getExternalFilesDir("Log");
        if (dir != null) {
            return dir.getPath();
        } else {
            return context.getDir("Log", Context.MODE_PRIVATE).getPath();
        }
    }

    // debug 表示是否输出ddms日志。
    // loable 表示是否写日志。
    public static void initInAppProcess(boolean debug, boolean logable, String logFile) {
        isDebug.set(debug);
        canLog.set(logable);
        isWriteLogFile.set(false);
        initilized.set(true);
    }

    public static void init(final Context context) {
        isDebug.set(DeviceUtils.isDebuggable());
        isWriteLogFile.set(false);
        canLog.set(isWriteLogFile.get() || isDebug.get());
        initilized.set(true);
    }

    /**
     * 判断是否为debug包
     * @return
     */
    public static boolean getDebug(){
        return isDebug.get();
    }

    /**
     * 判断是否可以进行日志输出，debug包或者开了写日志开关都可以进行日志输出
     * @return
     */
    public static boolean isDebug() {
        return canLog.get();
    }

    public static int v(String tag, String msgString) {
        try {
            String msg = null;
            if (isWriteLogFile.get()) {
                LogData data = writeToFile(tag, msgString, Log.INFO, null);
                if (data.hasLogout) {
                    return data.logResult;
                } else {
                    msg = data.msg;
                }
            }

            if (isDebug.get()) {
                if (TextUtils.isEmpty(msg)) {
                    msg = buildMessage(msgString);
                }
                return Log.v(tag, "" + msg);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int v(String tag, String msgString, Throwable tr) {
        try {
            String msg = null;
            if (isWriteLogFile.get()) {
                LogData data = writeToFile(tag, msgString, Log.INFO, tr);
                if (data.hasLogout) {
                    return data.logResult;
                } else {
                    msg = data.msg;
                }
            }
            if (isDebug.get()) {
                if (TextUtils.isEmpty(msg)) {
                    msg = buildMessage(msgString);
                }
                return Log.v(tag, "" + msg, tr);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int d(String tag, String msgString) {
        try {
            String msg = null;
            if (isWriteLogFile.get()) {
                LogData data = writeToFile(tag, msgString, Log.INFO, null);
                if (data.hasLogout) {
                    return data.logResult;
                } else {
                    msg = data.msg;
                }
            }

            if (isDebug.get()) {
                if (TextUtils.isEmpty(msg)) {
                    msg = buildMessage(msgString);
                }
                return Log.d(tag, "" + msg);
            }


        } catch (Exception e) {
        }
        return 0;
    }

    public static int d(String tag, String msgString, Throwable tr) {
        try {
            String msg = null;
            if (isWriteLogFile.get()) {
                LogData data = writeToFile(tag, msgString, Log.INFO, tr);
                if (data.hasLogout) {
                    return data.logResult;
                } else {
                    msg = data.msg;
                }
            }
            if (isDebug.get()) {
                if (TextUtils.isEmpty(msg)) {
                    msg = buildMessage(msgString);
                }
                return Log.d(tag, "" + msg, tr);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int i(String tag, String msgString) {
        try {
            String msg = null;
            if (isWriteLogFile.get()) {
                LogData data = writeToFile(tag, msgString, Log.INFO, null);
                if (data.hasLogout) {
                    return data.logResult;
                } else {
                    msg = data.msg;
                }
            }

            if (isDebug.get()) {
                if (TextUtils.isEmpty(msg)) {
                    msg = buildMessage(msgString);
                }
                return Log.i(tag, msg);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int i(String tag, String msgString, Throwable tr) {
        try {
            String msg = null;
            if (isWriteLogFile.get()) {
                LogData data = writeToFile(tag, msgString, Log.INFO, tr);
                if (data.hasLogout) {
                    return data.logResult;
                } else {
                    msg = data.msg;
                }
            }

            if (isDebug.get()) {
                if (TextUtils.isEmpty(msg)) {
                    msg = buildMessage(msgString);
                }
                return Log.i(tag, "" + msg, tr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int w(String tag, String msgString) {

        try {
            String msg = null;
            if (isWriteLogFile.get()) {
                LogData data = writeToFile(tag, msgString, Log.INFO, null);
                if (data.hasLogout) {
                    return data.logResult;
                } else {
                    msg = data.msg;
                }
            }

            if (isDebug.get()) {
                if (TextUtils.isEmpty(msg)) {
                    msg = buildMessage(msgString);
                }
                return Log.w(tag, "" + msg);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int w(String tag, String msgString, Throwable tr) {

        try {
            String msg = null;
            if (isWriteLogFile.get()) {
                LogData data = writeToFile(tag, msgString, Log.INFO, tr);
                if (data.hasLogout) {
                    return data.logResult;
                } else {
                    msg = data.msg;
                }
            }

            if (isDebug.get()) {
                if (TextUtils.isEmpty(msg)) {
                    msg = buildMessage(msgString);
                }
                return Log.w(tag, "" + msg, tr);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int e(String tag, String msgString) {
        try {
            String msg = null;
            if (isWriteLogFile.get()) {
                LogData data = writeToFile(tag, msgString, Log.INFO, null);
                if (data.hasLogout) {
                    return data.logResult;
                } else {
                    msg = data.msg;
                }
            }

            if (isDebug.get()) {
                if (TextUtils.isEmpty(msg)) {
                    msg = buildMessage(msgString);
                }
                return Log.e(tag, "" + msg);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int e(String tag, String msgString, Throwable tr) {
        try {
            String msg = null;
            if (isWriteLogFile.get()) {
                LogData data = writeToFile(tag, msgString, Log.INFO, tr);
                if (data.hasLogout) {
                    return data.logResult;
                } else {
                    msg = data.msg;
                }
            }

            if (isDebug.get()) {
                if (TextUtils.isEmpty(msg)) {
                    msg = buildMessage(msgString);
                }
                return Log.e(tag, "" + msg, tr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static LogData writeToFile(String tag, String msgString, int priority, Throwable tr) {
        String msg;
        LogData data = new LogData();
        if (initilized.get()) {
            if (isWriteLogFile.get()) {
                msg = buildMessage(msgString);
                if (tr != null) {
                    writeFile(tag, msg, tr);
                    data.logResult = Log.v(tag, "" + msg, tr);
                } else {
                    writeFile(tag, msg);
                    data.logResult = Log.v(tag, "" + msg);
                }
                data.hasLogout = true;
                data.msg = msg;
            }
        } else {
            msg = buildMessage(msgString);
            data.msg = msg;
        }
        return data;
    }

    public static void writeFile(final String tag, final String msg) {
        writeFile(LogPath, tag, msg, null);
    }

    private static void writeFile(final String tag, final String msg, final Throwable tr) {
        writeFile(LogPath, tag, msg, tr);
    }

    public synchronized static void writeFile(final File file, final String tag, final String msg, final Throwable tr) {
//        if (mExecutor == null) {
//            synchronized (TAG) {
//                if (mExecutor == null) {
//                    mExecutor = new ThreadPoolExecutor(1, 1,
//                            5 * 1000L, TimeUnit.MILLISECONDS,
//                            new LinkedBlockingQueue<Runnable>(),
//                            new PriorityThreadFactory(TAG, Process.THREAD_PRIORITY_LOWEST));
//                    mExecutor.allowCoreThreadTimeOut(true);
//                }
//            }
//        }
//        mExecutor.execute(new Runnable() {
//            @Override
//            public void run() {
//                writeFileSync(file, tag, msg, tr);
//            }
//        });
    }

    private static void writeFileSync(File file, String tag, String msg, Throwable tr) {
//        PrintStream outputStream = null;
//        FileUtils.makeDir(file.getParentFile().getPath());
//        if (!file.exists()) {
//            try {
//                file.createNewFile();
//            } catch (IOException ignore) {
//            }
//        }
//        if (file.exists()) {
//            try {
//                outputStream = new PrintStream(new FileOutputStream(file, true));
//                outputStream.printf(FORMAT_STR, getSystemTime(), tag, msg);
//                if (tr != null) {
//                    tr.printStackTrace(outputStream);
//                }
//            } catch (Exception e1) {
//                if (isDebug()) {
//                    e1.printStackTrace();
//                }
//            } finally {
//                if (outputStream != null) {
//                    outputStream.close();
//                }
//            }
//        }
    }

    /**
     * Formats the caller's provided message and prepends useful info like
     * calling thread ID and method name.
     */
    private static String buildMessage(String msg) {
        return Process.myPid() + " " + Thread.currentThread().getId() + ": " + msg;
    }

    public static String getCaller(){
        StackTraceElement[] trace = new Throwable().fillInStackTrace().getStackTrace();
        String caller = "";
        for (int i = 0; i < trace.length; i++) {
            caller = caller +  trace[i].toString() + "\n";
        }
        return caller;
    }

    static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.CHINA);

    private static String getSystemTime() {
        String str = null;
        try {

            Date curDate = new Date(System.currentTimeMillis());
            str = formatter.format(curDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 显示 2016-11-30T19:15:38.000+08:00 格式的时间
     *
     * @param time
     * @return
     */
    public static String timeToString(long time) {
        Time result = new Time();
        result.set(time);
        return result.format3339(false);
    }

    /**
     * 显示 {xxx, xxx} 格式的Pair
     *
     * @param pair
     * @return
     */
    public static String pairToString(Pair<?, ?> pair) {
        StringBuilder sb = new StringBuilder();
        sb.append("{")
            .append(pair != null ? pair.first : null)
            .append(", ").append(pair != null ? pair.second : null)
            .append("}");
        return sb.toString();
    }

    /**
     * 显示Intent中的所有数据
     */
    public static String intentToString(Intent intent) {
        if (intent != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Intent { ");
            String action = intent.getAction();
            if (action != null) {
                sb.append("act = ").append(action).append(",");
            }
            Set<String> categories = intent.getCategories();
            if (categories != null) {
                sb.append(" cat = [");
                Iterator<String> i = categories.iterator();
                boolean done = false;
                while (i.hasNext()) {
                    if (done) {
                        sb.append(", ");
                    }
                    done = true;
                    sb.append(i.next());
                }
                sb.append("]");
            }
            Uri uri = intent.getData();
            if (uri != null) {
                sb.append(" dat = ").append(uri).append(",");
            }
            String type = intent.getType();
            if (type != null) {
                sb.append(" typ = ").append(type).append(",");
            }
            int flags = intent.getFlags();
            if (flags != 0) {
                sb.append(" flg = 0x").append(Integer.toHexString(flags)).append(",");
            }
            String packageStr = intent.getPackage();
            if (packageStr != null) {
                sb.append(" pkg = ").append(packageStr).append(",");
            }
            ComponentName component = intent.getComponent();
            if (component != null) {
                sb.append(" cmp = ").append(component.flattenToShortString()).append(",");
            }
            Rect rect = intent.getSourceBounds();
            if (rect != null) {
                sb.append(" bnds = ").append(rect.toShortString()).append(",");
            }
            String bundleToString = bundleToString("extras", intent.getExtras());
            if (!TextUtils.isEmpty(bundleToString)) {
                sb.append(bundleToString);
            }
            sb.append(" }");
            return sb.toString();
        }
        return null;
    }

    public static String bundleToString(Bundle bundle) {
        return bundleToString("Bundle", bundle);
    }

    private static String bundleToString(String tag, Bundle bundle) {
        if (bundle != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(" ").append(tag).append(" = [");
            int i = 0;
            try {
                for (String key : bundle.keySet()) {
                    sb.append(key).append(" = ");
                    Object obj = bundle.get(key);
                    if (obj != null && obj instanceof Bundle) {
                        sb.append(" [").append(tag).append("2 = [");
                        int j = 0;
                        Bundle extras2 = (Bundle) obj;
                        for (String key2 : extras2.keySet()) {
                            Object obj2 = extras2.get(key2);
                            sb.append(key2).append(" = ").append(obj2 instanceof byte[] ? new String((byte[]) obj2) : obj2);
                            if (++j <= extras2.size() - 1) {
                                sb.append(", ");
                            }
                        }
                        sb.append("] ]");
                    } else {
                        sb.append(obj instanceof byte[] ? new String((byte[]) obj) : obj);
                    }
                    if (++i <= bundle.size() - 1) {
                        sb.append(", ");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            sb.append("]");
            return sb.toString();
        }
        return null;
    }

    private static class LogData {
        boolean hasLogout;
        int logResult;
        String msg;
    }

}
