package com.sticker_android.model.corporateproduct;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * This is the category list of item that is provided by admin
 * @author satyendra
 */

public class Category implements Parcelable{

    public boolean isChecked;
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
        dest.writeByte(this.isChecked ? (byte) 1 : (byte) 0);
        dest.writeInt(this.categoryId);
        dest.writeString(this.categoryName);
    }

    protected Category(Parcel in) {
        this.isChecked = in.readByte() != 0;
        this.categoryId = in.readInt();
        this.categoryName = in.readString();
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel source) {
            return new Category(source);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };
}
