package com.sticker_android.model.corporateproduct;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by user on 23/4/18.
 */

public class Statics implements Parcelable {


    @SerializedName("product_id")
    public int productId;
    @SerializedName("like_count")
    public int likeCount;
    @SerializedName("share_count")
    public int shareCount;
    @SerializedName("download_count")
    public int downloadCount;

    public Statics() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.productId);
        dest.writeInt(this.likeCount);
        dest.writeInt(this.shareCount);
        dest.writeInt(this.downloadCount);
    }

    protected Statics(Parcel in) {
        this.productId = in.readInt();
        this.likeCount = in.readInt();
        this.shareCount = in.readInt();
        this.downloadCount = in.readInt();
    }

    public static final Creator<Statics> CREATOR = new Creator<Statics>() {
        @Override
        public Statics createFromParcel(Parcel source) {
            return new Statics(source);
        }

        @Override
        public Statics[] newArray(int size) {
            return new Statics[size];
        }
    };
}
