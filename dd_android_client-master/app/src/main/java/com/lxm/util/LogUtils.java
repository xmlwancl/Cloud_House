package com.lxm.util;

import android.util.Log;

import com.lxm.BuildConfig;

public class LogUtils {

    private static final String TAG = "LogUtils";

    public static void logInfoInDebugMode(String msg) {
        logInfoInDebugMode(TAG, msg);
    }

    public static void logInfoInDebugMode(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, msg);
        }
    }

    public static void logErrorInDebugMode(String msg) {
        logErrorInDebugMode(TAG, msg);
    }

    public static void logErrorInDebugMode(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, msg);
        }
    }
}
