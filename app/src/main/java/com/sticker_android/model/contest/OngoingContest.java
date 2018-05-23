package com.sticker_android.model.contest;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.sticker_android.model.corporateproduct.Product;

/**
 * Created by user on 17/4/18.
 */

public class OngoingContest implements Parcelable {
    @SerializedName("user_contest_id")
    public int userContestId;
    @SerializedName("user_id")
    public int userId;
    @SerializedName("contest_id")
    public int contestId;
    @SerializedName("product_id")
    public int productId;
    @SerializedName("contest_info")
    public ContestInfo contestInfo;

    @SerializedName("product_info")
    public Product productList;
    public int dummyId;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.userContestId);
        dest.writeInt(this.userId);
        dest.writeInt(this.contestId);
        dest.writeInt(this.productId);
        dest.writeParcelable(this.contestInfo, flags);
        dest.writeParcelable(this.productList, flags);
        dest.writeInt(this.dummyId);
    }

    public OngoingContest() {
    }

    protected OngoingContest(Parcel in) {
        this.userContestId = in.readInt();
        this.userId = in.readInt();
        this.contestId = in.readInt();
        this.productId = in.readInt();
        this.contestInfo = in.readParcelable(ContestInfo.class.getClassLoader());
        this.productList = in.readParcelable(Product.class.getClassLoader());
        this.dummyId = in.readInt();
    }

    public static final Parcelable.Creator<OngoingContest> CREATOR = new Parcelable.Creator<OngoingContest>() {
        @Override
        public OngoingContest createFromParcel(Parcel source) {
            return new OngoingContest(source);
        }

        @Override
        public OngoingContest[] newArray(int size) {
            return new OngoingContest[size];
        }
    };
}
