package com.sticker_android.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NewPaylpad {

    @SerializedName("data")
    @Expose
    private List<Votes> data = null;

    public List<Votes> getData() {
        return data;
    }

    public void setData(List<Votes> data) {
        this.data = data;
    }
}
