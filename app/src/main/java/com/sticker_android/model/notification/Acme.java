package com.sticker_android.model.notification;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by user on 3/5/18.
 */

public class Acme implements Parcelable {
    @SerializedName("acme1")
    public ContestObj contestObj;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.contestObj, flags);
    }

    public Acme() {
    }

    protected Acme(Parcel in) {
        this.contestObj = in.readParcelable(ContestObj.class.getClassLoader());
    }

    public static final Parcelable.Creator<Acme> CREATOR = new Parcelable.Creator<Acme>() {
        @Override
        public Acme createFromParcel(Parcel source) {
            return new Acme(source);
        }

        @Override
        public Acme[] newArray(int size) {
            return new Acme[size];
        }
    };
}
