package com.sticker_android.model.notification;

import com.google.gson.annotations.SerializedName;
import com.sticker_android.model.payload.Payload;

/**
 * Created by user on 3/5/18.
 */

public class AppNotification {

    @SerializedName("image")
    public String image;
    @SerializedName("is_background")

    public Boolean isBackground;
    @SerializedName("payload")
    public Payload payload;
    @SerializedName("title")
    public String title;
    @SerializedName("message")
    public String message;
    @SerializedName("timestamp")
    public String timestamp;

}
