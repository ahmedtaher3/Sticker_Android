package com.sticker_android.network;

import android.util.Log;

public class LogUtils {

    /**
     * Priority constant for the println method
     */
    private static final int VERBOSE = 1;
    public static final int DEBUG = 2;
    private static final int INFO = 3;
    private static final int WARN = 4;
    public static final int ERROR = 5;

    public static void printLog(int LOG_ID_MAIN, String TAG, String message)
    {
        boolean LOG_ON = true;
        if (LOG_ON) {

            switch (LOG_ID_MAIN) {
                case DEBUG :
                    Log.d(TAG, message);
                    break;

                case ERROR :
                    Log.e(TAG, message);
                    break;

                case INFO :
                    Log.d(TAG, message);
                    break;

                case WARN :
                    Log.w(TAG, message);
                    break;

                case VERBOSE :
                    Log.v(TAG, message);
                    break;
            }
        }
    }

}
