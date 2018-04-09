package com.sticker_android.model.corporateproduct;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by user on 4/4/18.
 */

public class ProductList implements Parcelable {

    @SerializedName("product_id")
    private int productid;


    @SerializedName("product_name")
    private String productname;
    @SerializedName("type")
    private String type="load";

    @SerializedName("description")
    private String description;

    @SerializedName("expiry_date")
    private String expireDate;
    @SerializedName("image_path")
    private String imagePath;

    public int getIsExpired() {
        return isExpired;
    }

    public void setIsExpired(int isExpired) {
        this.isExpired = isExpired;
    }

    @SerializedName("is_expired")
    private int isExpired;

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    @SerializedName("created_time")
    private String createdTime;

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    @SerializedName("category_id")
    private int categoryId;

    public ProductList() {
    }

    public int getProductid() {
        return productid;
    }

    public void setProductid(int productid) {
        this.productid = productid;
    }

    public String getProductname() {
        return productname;
    }

    public void setProductname(String productname) {
        this.productname = productname;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.productid);
        dest.writeString(this.productname);
        dest.writeString(this.type);
        dest.writeString(this.description);
        dest.writeString(this.expireDate);
        dest.writeString(this.imagePath);
        dest.writeInt(this.isExpired);
        dest.writeString(this.createdTime);
        dest.writeInt(this.categoryId);
    }

    protected ProductList(Parcel in) {
        this.productid = in.readInt();
        this.productname = in.readString();
        this.type = in.readString();
        this.description = in.readString();
        this.expireDate = in.readString();
        this.imagePath = in.readString();
        this.isExpired = in.readInt();
        this.createdTime = in.readString();
        this.categoryId = in.readInt();
    }

    public static final Creator<ProductList> CREATOR = new Creator<ProductList>() {
        @Override
        public ProductList createFromParcel(Parcel source) {
            return new ProductList(source);
        }

        @Override
        public ProductList[] newArray(int size) {
            return new ProductList[size];
        }
    };
}
