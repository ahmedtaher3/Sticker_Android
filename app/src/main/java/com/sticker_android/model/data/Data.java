package com.sticker_android.model.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by user on 27/3/18.
 */

public class Data {

    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("info_text")
    private String infoText;

    @SerializedName("email")
    private String email;

    @SerializedName("mobile")
    private String mobile;
}
