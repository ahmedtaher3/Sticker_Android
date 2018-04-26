package com.sticker_android.model.notification;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by user on 17/4/18.
 */

public class NotificationApp implements Parcelable {

    @SerializedName("notification_id")
    public int notificatinId;
    @SerializedName("user_id")
    public int userId;
    @SerializedName("text_json")
    public ContestObj contestObj;
    @SerializedName("created_date")
    public String cratedDate;

    public NotificationApp() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.notificatinId);
        dest.writeInt(this.userId);
        dest.writeParcelable(this.contestObj, flags);
        dest.writeString(this.cratedDate);
    }

    protected NotificationApp(Parcel in) {
        this.notificatinId = in.readInt();
        this.userId = in.readInt();
        this.contestObj = in.readParcelable(ContestObj.class.getClassLoader());
        this.cratedDate = in.readString();
    }

    public static final Creator<NotificationApp> CREATOR = new Creator<NotificationApp>() {
        @Override
        public NotificationApp createFromParcel(Parcel source) {
            return new NotificationApp(source);
        }

        @Override
        public NotificationApp[] newArray(int size) {
            return new NotificationApp[size];
        }
    };
}
