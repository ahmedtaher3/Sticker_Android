package com.sticker_android.model.notification;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.sticker_android.model.payload.Payload;

/**
 * Created by user on 3/5/18.
 */

public class AppNotification implements Parcelable {

    @SerializedName("image")
    public String image;
    @SerializedName("is_background")

    public Boolean isBackground;
    @SerializedName("payload")
    public Payload payload;
    @SerializedName("title")
    public String title;
    @SerializedName("message")
    public String message;
    @SerializedName("timestamp")
    public String timestamp;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.image);
        dest.writeValue(this.isBackground);
        dest.writeParcelable(this.payload, flags);
        dest.writeString(this.title);
        dest.writeString(this.message);
        dest.writeString(this.timestamp);
    }

    public AppNotification() {
    }

    protected AppNotification(Parcel in) {
        this.image = in.readString();
        this.isBackground = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.payload = in.readParcelable(Payload.class.getClassLoader());
        this.title = in.readString();
        this.message = in.readString();
        this.timestamp = in.readString();
    }

    public static final Parcelable.Creator<AppNotification> CREATOR = new Parcelable.Creator<AppNotification>() {
        @Override
        public AppNotification createFromParcel(Parcel source) {
            return new AppNotification(source);
        }

        @Override
        public AppNotification[] newArray(int size) {
            return new AppNotification[size];
        }
    };
}
