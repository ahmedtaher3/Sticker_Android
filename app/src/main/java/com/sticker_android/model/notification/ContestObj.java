package com.sticker_android.model.notification;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by user on 17/4/18.
 */

public class ContestObj implements Parcelable {
    @SerializedName("status")
    public int status;
    @SerializedName("contest_id")
    public int contestId;
    @SerializedName("msg")
    public String msg;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.status);
        dest.writeInt(this.contestId);
        dest.writeString(this.msg);
    }

    public ContestObj() {
    }

    protected ContestObj(Parcel in) {
        this.status = in.readInt();
        this.contestId = in.readInt();
        this.msg = in.readString();
    }

    public static final Parcelable.Creator<ContestObj> CREATOR = new Parcelable.Creator<ContestObj>() {
        @Override
        public ContestObj createFromParcel(Parcel source) {
            return new ContestObj(source);
        }

        @Override
        public ContestObj[] newArray(int size) {
            return new ContestObj[size];
        }
    };
}
