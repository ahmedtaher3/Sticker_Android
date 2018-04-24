package com.sticker_android.model.contest;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by user on 24/4/18.
 */

public class FanContest implements Parcelable {

    @SerializedName("contest_id")
    public  long contestId;
    @SerializedName("contest_name")
    public String contestName;
    @SerializedName("expiry_date")
    public String expireDate;
    @SerializedName("total_item")
    public long tootleItem;
    @SerializedName("created_time")
    public String createdTime;
    @SerializedName("is_expired")
    public int isExpired;

    public FanContest() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.contestId);
        dest.writeString(this.contestName);
        dest.writeString(this.expireDate);
        dest.writeLong(this.tootleItem);
        dest.writeString(this.createdTime);
        dest.writeInt(this.isExpired);
    }

    protected FanContest(Parcel in) {
        this.contestId = in.readLong();
        this.contestName = in.readString();
        this.expireDate = in.readString();
        this.tootleItem = in.readLong();
        this.createdTime = in.readString();
        this.isExpired = in.readInt();
    }

    public static final Creator<FanContest> CREATOR = new Creator<FanContest>() {
        @Override
        public FanContest createFromParcel(Parcel source) {
            return new FanContest(source);
        }

        @Override
        public FanContest[] newArray(int size) {
            return new FanContest[size];
        }
    };
}
