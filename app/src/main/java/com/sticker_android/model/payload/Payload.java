package com.sticker_android.model.payload;

import com.google.gson.annotations.SerializedName;
import com.sticker_android.model.UserData;

/**
 * Created by user on 26/3/18.
 */

public class Payload {

    @SerializedName("data")
    private UserData data;

    public UserData getData() {
        return data;
    }

    public void setData(UserData data) {
        this.data = data;
    }

}
