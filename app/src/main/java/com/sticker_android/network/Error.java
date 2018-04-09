package com.sticker_android.network;

import com.google.gson.annotations.SerializedName;

/**
 * Created by user on 26/3/18.
 */

public class Error {
    @SerializedName("error")
    public String error;
    @SerializedName("message")
    public String message;
}

