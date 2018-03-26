package com.sticker_android.network;

import android.content.Context;

import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class ApiError {

    private String ERROR_NO_INTERNET = "Please check your internet connection.";
    private String ERROR_TIMEOUT = "Server Timeout";
    private String ERROR_COMMON = "Server Unreachable";
    private String ERROR_PARSING = "Parsing error";

    public ApiError(Context mContext, Throwable t) {
        t.printStackTrace();
        LogUtils.printLog(LogUtils.ERROR, getClass().getSimpleName() , "Error: Exception");

        if(t instanceof ConnectException) {
            ErrorUtils.showMessageDialog(mContext, "Error", ERROR_NO_INTERNET);
        } else if(t instanceof SocketTimeoutException) {
            ErrorUtils.showMessageDialog(mContext, "Error", ERROR_TIMEOUT);
        } else if (t instanceof MalformedJsonException) {
            ErrorUtils.showMessageDialog(mContext, "Error", ERROR_COMMON);
        } else if (t instanceof SocketException) {
            ErrorUtils.showMessageDialog(mContext, "Error", ERROR_TIMEOUT);
        } else if (t instanceof IOException) {
            ErrorUtils.showMessageDialog(mContext, "Error", ERROR_NO_INTERNET);
        } else if (t instanceof JsonSyntaxException){
            ErrorUtils.showMessageDialog(mContext, "Error", ERROR_COMMON);
        }
    }
}
