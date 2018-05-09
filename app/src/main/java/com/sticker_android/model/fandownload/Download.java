package com.sticker_android.model.fandownload;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by user on 8/5/18.
 */

public class Download implements Parcelable {

    @SerializedName("user_id")
    public long userId;
    @SerializedName("image_url")
    public String imageUrl;
    @SerializedName("user_my_id")
    public long user_my_id;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.userId);
        dest.writeString(this.imageUrl);
        dest.writeLong(this.user_my_id);
    }

    public Download() {
    }

    protected Download(Parcel in) {
        this.userId = in.readLong();
        this.imageUrl = in.readString();
        this.user_my_id = in.readLong();
    }

    public static final Parcelable.Creator<Download> CREATOR = new Parcelable.Creator<Download>() {
        @Override
        public Download createFromParcel(Parcel source) {
            return new Download(source);
        }

        @Override
        public Download[] newArray(int size) {
            return new Download[size];
        }
    };
}
