package com.shaubert.liftago.util;

import android.util.Log;
import com.shaubert.liftago.BuildConfig;

public class Logger {

    public static void network(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message);
        }
    }

    public static void persistence(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message);
        }
    }

    public static void debug(String tag, String message, Throwable t) {
        if (BuildConfig.LOG_LEVEL <= Log.DEBUG) {
            Log.d(tag, message, t);
        }
    }

    public static void debug(String tag, String message) {
        if (BuildConfig.LOG_LEVEL <= Log.DEBUG) {
            Log.d(tag, message);
        }
    }

    public static void warn(String tag, String message) {
        if (BuildConfig.LOG_LEVEL <= Log.WARN) {
            Log.w(tag, message);
        }
    }

    public static void warn(String tag, String message, Throwable t) {
        if (BuildConfig.LOG_LEVEL <= Log.WARN) {
            Log.w(tag, message, t);
        }
    }

    public static void error(String tag, String message, Throwable t) {
        if (BuildConfig.LOG_LEVEL <= Log.ERROR) {
            Log.e(tag, message, t);
        }
    }

}
