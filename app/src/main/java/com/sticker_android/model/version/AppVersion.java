package com.sticker_android.model.version;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by user on 31/5/18.
 */

public class AppVersion implements Parcelable {
@SerializedName("id")
    public String id;
    @SerializedName("android_version")
    public String appVersion;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.appVersion);
    }

    public AppVersion() {
    }

    protected AppVersion(Parcel in) {
        this.id = in.readString();
        this.appVersion = in.readString();
    }

    public static final Parcelable.Creator<AppVersion> CREATOR = new Parcelable.Creator<AppVersion>() {
        @Override
        public AppVersion createFromParcel(Parcel source) {
            return new AppVersion(source);
        }

        @Override
        public AppVersion[] newArray(int size) {
            return new AppVersion[size];
        }
    };
}
