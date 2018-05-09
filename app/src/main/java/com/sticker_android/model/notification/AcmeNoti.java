package com.sticker_android.model.notification;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by user on 9/5/18.
 */

public class AcmeNoti implements Parcelable {

    @SerializedName("status")
    public int status;
    @SerializedName("contest_id")
    public int contestId;
    @SerializedName("msg")
    public String msg;
    @SerializedName("notification_id")
    public long notificationId;


    public AcmeNoti() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.status);
        dest.writeInt(this.contestId);
        dest.writeString(this.msg);
        dest.writeLong(this.notificationId);
    }

    protected AcmeNoti(Parcel in) {
        this.status = in.readInt();
        this.contestId = in.readInt();
        this.msg = in.readString();
        this.notificationId = in.readLong();
    }

    public static final Creator<AcmeNoti> CREATOR = new Creator<AcmeNoti>() {
        @Override
        public AcmeNoti createFromParcel(Parcel source) {
            return new AcmeNoti(source);
        }

        @Override
        public AcmeNoti[] newArray(int size) {
            return new AcmeNoti[size];
        }
    };
}
