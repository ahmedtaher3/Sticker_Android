package com.sticker_android.model.message;

import com.google.gson.annotations.SerializedName;

/**
 * Created by user on 26/3/18.
 */

public class Message {
    @SerializedName("title")
    public String title;
    @SerializedName("message")
    public String message;
}
