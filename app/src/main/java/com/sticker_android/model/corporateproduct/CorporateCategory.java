package com.sticker_android.model.corporateproduct;

import com.google.gson.annotations.SerializedName;

/**
 * Created by user on 6/4/18.
 */

public class CorporateCategory {

    @SerializedName("category_id")
   public int  categoryId;

    @SerializedName("category_name")
     public   String categoryName;


    @Override
    public String toString() {
        return categoryName;
    }
}
