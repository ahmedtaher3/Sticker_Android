package com.sticker_android.model.contest;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by user on 17/4/18.
 */

public class ContestInfo implements Parcelable {
    @SerializedName("contest_id")
    public int contestId;
    @SerializedName("contest_name")
    public String contestName;
    @SerializedName("expiry_date")
    public String expireDate;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.contestId);
        dest.writeString(this.contestName);
        dest.writeString(this.expireDate);
    }

    public ContestInfo() {
    }

    protected ContestInfo(Parcel in) {
        this.contestId = in.readInt();
        this.contestName = in.readString();
        this.expireDate = in.readString();
    }

    public static final Parcelable.Creator<ContestInfo> CREATOR = new Parcelable.Creator<ContestInfo>() {
        @Override
        public ContestInfo createFromParcel(Parcel source) {
            return new ContestInfo(source);
        }

        @Override
        public ContestInfo[] newArray(int size) {
            return new ContestInfo[size];
        }
    };
}
