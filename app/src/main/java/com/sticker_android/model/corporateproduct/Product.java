package com.sticker_android.model.corporateproduct;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.sticker_android.model.enums.ProductStatus;

/**
 * Created by user on 4/4/18.
 */

public class Product implements Parcelable {

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

    @SerializedName("status")
    public int productStatus;

    @Override
    public boolean equals(Object obj) {
        return productid == ((Product)obj).productid;
    }

    @Override
    public int hashCode() {
        return String.valueOf(productid).hashCode();
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

    public Product() {}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(productid);
        dest.writeString(productname);
        dest.writeString(type);
        dest.writeString(description);
        dest.writeString(expireDate);
        dest.writeString(imagePath);
        dest.writeInt(isExpired);
        dest.writeString(createdTime);
        dest.writeInt(categoryId);
        dest.writeInt(productStatus);
    }

    protected Product(Parcel in) {
        productid = in.readInt();
        productname = in.readString();
        type = in.readString();
        description = in.readString();
        expireDate = in.readString();
        imagePath = in.readString();
        isExpired = in.readInt();
        createdTime = in.readString();
        categoryId = in.readInt();
        productStatus = in.readInt();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}
