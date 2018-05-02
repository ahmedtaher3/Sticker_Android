package com.sticker_android.model.rejection;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by user on 2/5/18.
 */

public class Reject implements Parcelable {
    @SerializedName("product_id")

    public String productId;
    @SerializedName("description")

    public String description;
    @SerializedName("action_by")

    public String actionBy;
    @SerializedName("id")

    public String id;
    @SerializedName("created_time")

    public String createdTime;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.productId);
        dest.writeString(this.description);
        dest.writeString(this.actionBy);
        dest.writeString(this.id);
        dest.writeString(this.createdTime);
    }

    public Reject() {
    }

    protected Reject(Parcel in) {
        this.productId = in.readString();
        this.description = in.readString();
        this.actionBy = in.readString();
        this.id = in.readString();
        this.createdTime = in.readString();
    }

    public static final Parcelable.Creator<Reject> CREATOR = new Parcelable.Creator<Reject>() {
        @Override
        public Reject createFromParcel(Parcel source) {
            return new Reject(source);
        }

        @Override
        public Reject[] newArray(int size) {
            return new Reject[size];
        }
    };
}
