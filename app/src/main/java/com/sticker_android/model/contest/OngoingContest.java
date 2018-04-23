package com.sticker_android.model.contest;

import com.google.gson.annotations.SerializedName;
import com.sticker_android.model.corporateproduct.Product;

/**
 * Created by user on 17/4/18.
 */

public class OngoingContest {
    @SerializedName("user_contest_id")
    public int userContestId;
    @SerializedName("user_id")
    public int userId;
    @SerializedName("contest_id")
    public int contestId;
    @SerializedName("product_id")
    public int productId;
    @SerializedName("contest_info")
    public ContestInfo contestInfo;

    @SerializedName("product_info")
    public Product productList;
}
