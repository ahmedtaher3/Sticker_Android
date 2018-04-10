package com.sticker_android.model.corporateproduct;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * This is the category list of item that is provided by admin
 * @author satyendra
 */

public class Category implements Parcelable{

    @SerializedName("category_id")
    public int  categoryId;

    @SerializedName("category_name")
    public   String categoryName;

    @Override
    public boolean equals(Object obj) {
        return categoryId == ((Category)obj).categoryId;
    }

    @Override
    public int hashCode() {
        return String.valueOf(categoryId).hashCode();
    }

    public Category(){}

    public Category(int categoryId, String name){
        this.categoryId = categoryId;
        this.categoryName = name;
    }

    protected Category(Parcel in) {
        categoryId = in.readInt();
        categoryName = in.readString();
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    @Override
    public String toString() {
        return categoryName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(categoryId);
        dest.writeString(categoryName);
    }
}
