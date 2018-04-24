package com.sticker_android.model.contest;

import com.google.gson.annotations.SerializedName;
import com.sticker_android.model.corporateproduct.Product;

/**
 * Created by user on 24/4/18.
 */

public class FanContestAll {

    public int dummyId;
    @SerializedName("user_contest_id")
    public long userContestId;

    @SerializedName("user_id")
    public long userId;


    @SerializedName("contest_id")
    public long contestId;

    @SerializedName("product_id")
    public Product product;

}
