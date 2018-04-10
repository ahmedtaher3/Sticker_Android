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

    public CorporateCategory(int categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    @Override
    public String toString() {
        return categoryName;
    }
}
