package com.sticker_android.model.payload;

import com.google.gson.annotations.SerializedName;
import com.sticker_android.model.User;
import com.sticker_android.model.corporateproduct.CorporateCategory;
import com.sticker_android.model.corporateproduct.ProductList;

import java.util.ArrayList;

/**
 * Created by user on 26/3/18.
 */

public class Payload {

    @SerializedName("data")
    private User data;

    @SerializedName("product_list")
    public ArrayList<ProductList> productList;

    public User getData() {
        return data;
    }

    public void setData(User data) {
        this.data = data;
    }

    @SerializedName("corporate_category")
    public ArrayList<CorporateCategory> corporateCategories;

}
