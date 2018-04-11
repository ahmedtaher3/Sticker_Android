package com.sticker_android.utils;

import android.util.Log;

/**
 * Created by satyendra on 11/3/15.
 */
public class AppLogger {

    public static void debug(String tag, Object message){

        if(AppConstants.DEBUG_MODE) Log.d(tag, String.valueOf(message));
    }

    public static void error(String tag, Object message){

        if(AppConstants.DEBUG_MODE) Log.e(tag, String.valueOf(message));
    }

    public static void info(String tag, Object message){

        if(AppConstants.DEBUG_MODE) Log.i(tag, String.valueOf(message));
    }
}