package com.damianmichalak.shopping_list.helper;

import android.util.Log;

public class Logger {

    public static void log(String tag, String message) {
        Log.d(tag, message);
    }

    public static void log(String message) {
        Log.d("LOGGER", message);
    }

}
