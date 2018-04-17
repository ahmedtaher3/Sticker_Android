package com.sticker_android.model.contest;

import com.google.gson.annotations.SerializedName;

/**
 * Created by user on 17/4/18.
 */

public class ContestInfo {
    @SerializedName("contest_id")
    public int contestId;
    @SerializedName("contest_name")
    public String contestName;
    @SerializedName("expiry_date")
    public String expireDate;
}
