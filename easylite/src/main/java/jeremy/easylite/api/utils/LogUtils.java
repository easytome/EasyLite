package jeremy.easylite.api.utils;

import android.text.TextUtils;
import android.util.Log;

/**
 * Created by JIANGJIAN650 on 2018/5/22.
 */

public class LogUtils {
    private final static String TAG = "EasyLiteLogUtils";
    private static int LOG_MAXLENGTH = 2000;

    private static boolean isDebug = false;

    public static void setDebug(boolean isDebug) {
        LogUtils.isDebug = isDebug;
    }

    public static void v(String msg) {
        v(TAG, msg);
    }

    public static void v(String tagName, String msg) {
        print(tagName, msg, new OnLogListener() {
            @Override
            public void println(String tag, String msg) {
                Log.v(tag, msg);
            }
        });
    }

    public static void d(String msg) {
        d(TAG, msg);
    }

    public static void d(String tagName, String msg) {
        print(tagName, msg, new OnLogListener() {
            @Override
            public void println(String tag, String msg) {
                Log.d(tag, msg);
            }
        });
    }

    public static void i(String msg) {
        i(TAG, msg);
    }

    public static void i(String tagName, String msg) {
        print(tagName, msg, new OnLogListener() {
            @Override
            public void println(String tag, String msg) {
                Log.i(tag, msg);
            }
        });
    }

    public static void w(String msg) {
        w(TAG, msg);
    }

    public static void w(String tagName, String msg) {
        print(tagName, msg, new OnLogListener() {
            @Override
            public void println(String tag, String msg) {
                Log.w(tag, msg);
            }
        });
    }

    public static void e(String msg) {
        e(TAG, msg);
    }

    public static void e(String tagName, String msg) {
        print(tagName, msg, new OnLogListener() {
            @Override
            public void println(String tag, String msg) {
                Log.e(tag, msg);
            }
        });
    }

    private static void print(String tagName, String msg, OnLogListener onLogListener) {
        if (!isDebug)
            return;
        if (onLogListener == null)
            return;
        if (TextUtils.isEmpty(msg)) {
            Log.e(TAG, "msg is null!");
            return;
        }
        if (isMoreThanMax(msg)) {
            int strLength = msg.length();
            int start = 0;
            int end = LOG_MAXLENGTH;
            for (int i = 0; i < 100; i++) {
                if (strLength > end) {
                    onLogListener.println(tagName + i, msg.substring(start, end));
                    start = end;
                    end = end + LOG_MAXLENGTH;
                } else {
                    onLogListener.println(tagName + i, msg.substring(start, strLength));
                    break;
                }
            }
        } else {
            onLogListener.println(tagName, msg);
        }
        onLogListener = null;
    }

    private static boolean isMoreThanMax(String msg) {
        if (TextUtils.isEmpty(msg))
            return false;
        return msg.length() >= LOG_MAXLENGTH;
    }

    interface OnLogListener {
        void println(String tag, String msg);
    }
}
