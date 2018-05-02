package com.sticker_android.model.filter;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by user on 1/5/18.
 */

public class FanFilter implements Parcelable {

    public int dummyId;
    @SerializedName("filter_id")
    public long filterId;
    @SerializedName("image_url")
    public String imageUrl;
    @SerializedName("filter_name")
    public String filterName;
    @SerializedName("status")
    public int status;
    @SerializedName("type")
    public String type;

    public FanFilter(){}

    protected FanFilter(Parcel in) {
        dummyId = in.readInt();
        filterId = in.readLong();
        imageUrl = in.readString();
        filterName = in.readString();
        status = in.readInt();
        type = in.readString();
    }

    public static final Creator<FanFilter> CREATOR = new Creator<FanFilter>() {
        @Override
        public FanFilter createFromParcel(Parcel in) {
            return new FanFilter(in);
        }

        @Override
        public FanFilter[] newArray(int size) {
            return new FanFilter[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(dummyId);
        dest.writeLong(filterId);
        dest.writeString(imageUrl);
        dest.writeString(filterName);
        dest.writeInt(status);
        dest.writeString(type);
    }
}
