package com.sticker_android.model.payload;

import com.google.gson.annotations.SerializedName;
import com.sticker_android.model.User;

/**
 * Created by user on 26/3/18.
 */

public class Payload {

    @SerializedName("data")
    private User data;

    public User getData() {
        return data;
    }

    public void setData(User data) {
        this.data = data;
    }

}
