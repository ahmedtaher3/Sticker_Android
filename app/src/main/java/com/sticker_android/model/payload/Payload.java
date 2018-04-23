package com.sticker_android.model.payload;

import com.google.gson.annotations.SerializedName;
import com.sticker_android.constant.AppConstant;
import com.sticker_android.model.User;
import com.sticker_android.model.contest.ContestCompleted;
import com.sticker_android.model.contest.OngoingContest;
import com.sticker_android.model.corporateproduct.Category;
import com.sticker_android.model.corporateproduct.Product;
import com.sticker_android.model.corporateproduct.Statics;
import com.sticker_android.model.notification.NotificationApp;

import java.util.ArrayList;

/**
 * Created by user on 26/3/18.
 */

public class Payload {

    @SerializedName("data")
    private User data;

    @SerializedName("product_list")
    public ArrayList<Product> productList;

    @SerializedName(AppConstant.PRODUCT)
    public Product product;

    public User getData() {
        return data;
    }

    public void setData(User data) {
        this.data = data;
    }

    @SerializedName("corporate_category")
    public ArrayList<Category> corporateCategories;

    @SerializedName("notification_list")
    public ArrayList<NotificationApp> notificationArrayList;

    @SerializedName("contest_list")
    public ArrayList<OngoingContest> ongoingContests;

    @SerializedName("completed_contest_list")
    public ArrayList<ContestCompleted> completedArrayList;


    @SerializedName("all_product_list")
    public ArrayList<Product> productListAll;

    @SerializedName("statics")
    public Statics statics;

}


