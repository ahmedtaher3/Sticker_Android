package com.sticker_android.model.corporateproduct;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by user on 4/4/18.
 */

public class Product implements Parcelable {

    public boolean isSelected;
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

    @SerializedName("is_like")
    public int isLike;
    @SerializedName("user_name")
    public String userName;

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

    @SerializedName("statics")
    public Statics statics;


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
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
        dest.writeInt(this.productid);
        dest.writeString(this.productname);
        dest.writeString(this.type);
        dest.writeString(this.description);
        dest.writeString(this.expireDate);
        dest.writeString(this.imagePath);
        dest.writeInt(this.isLike);
        dest.writeString(this.userName);
        dest.writeInt(this.isExpired);
        dest.writeString(this.createdTime);
        dest.writeInt(this.categoryId);
        dest.writeInt(this.productStatus);
        dest.writeParcelable(this.statics, flags);
    }

    protected Product(Parcel in) {
        this.isSelected = in.readByte() != 0;
        this.productid = in.readInt();
        this.productname = in.readString();
        this.type = in.readString();
        this.description = in.readString();
        this.expireDate = in.readString();
        this.imagePath = in.readString();
        this.isLike = in.readInt();
        this.userName = in.readString();
        this.isExpired = in.readInt();
        this.createdTime = in.readString();
        this.categoryId = in.readInt();
        this.productStatus = in.readInt();
        this.statics = in.readParcelable(Statics.class.getClassLoader());
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel source) {
            return new Product(source);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}
