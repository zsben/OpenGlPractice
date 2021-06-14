package com.example.openglpractice.util;

import android.util.Log;

public class LogUtils {
    public static boolean ON = true;

    public static void d(String TAG, String msg) {
        if (ON) {
            Log.d(TAG, "d: " + msg);
        }
    }

    public static void e(String TAG, String msg) {
        if (ON) {
            Log.e(TAG, "e: " + msg);
        }
    }
}
