package com.sticker_android.model.filter;

import com.google.gson.annotations.SerializedName;

/**
 * Created by user on 1/5/18.
 */

public class FanFilter {

    public int dummyId;
    @SerializedName("filter_id")
    public long filterId;
    @SerializedName("image_url")
    public String imageUrl;
    @SerializedName("filter_name")
    public String filterName;
    @SerializedName("status")
    public int status;
    @SerializedName("type")
    public String type;
}
