package com.sticker_android.model.payload;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.sticker_android.constant.AppConstant;
import com.sticker_android.model.User;
import com.sticker_android.model.contest.ContestCompleted;
import com.sticker_android.model.contest.FanContest;
import com.sticker_android.model.contest.FanContestAll;
import com.sticker_android.model.contest.FanContestDownload;
import com.sticker_android.model.contest.OngoingContest;
import com.sticker_android.model.corporateproduct.Category;
import com.sticker_android.model.corporateproduct.Product;
import com.sticker_android.model.corporateproduct.Statics;
import com.sticker_android.model.filter.FanFilter;
import com.sticker_android.model.notification.NotificationApp;

import java.util.ArrayList;

/**
 * Created by user on 26/3/18.
 */

public class Payload implements Parcelable {

    @SerializedName("data")
    private User data;

    @SerializedName("product_list")
    public ArrayList<Product> productList;

    @SerializedName(AppConstant.PRODUCT)
    public Product product;

    public User getData() {
        return data;
    }

    public void setData(User data) {
        this.data = data;
    }

    @SerializedName("corporate_category")
    public ArrayList<Category> corporateCategories;

    @SerializedName("notification_list")
    public ArrayList<NotificationApp> notificationArrayList;

    @SerializedName("contest_list")
    public ArrayList<OngoingContest> ongoingContests;

    @SerializedName("completed_contest_list")
    public ArrayList<ContestCompleted> completedArrayList;


    @SerializedName("all_product_list")
    public ArrayList<Product> productListAll;

    @SerializedName("statics")
    public Statics statics;

    @SerializedName("fan_contest_list")
    public ArrayList<FanContest> fanContestList;

    @SerializedName("fan_contest_list_all")
    public ArrayList<FanContestAll> fanContestAllArrayList;


    @SerializedName("fan_download_list")
    public ArrayList<FanContestDownload> fanDownloadList;
    @SerializedName("filter_list")
    public ArrayList<FanFilter> fanFilterArrayList = new ArrayList<>();


    @SerializedName("msg")
    public String msg;


    @SerializedName("title")
    public String title;

    @SerializedName("info_text")
    public String infoText;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.data, flags);
        dest.writeTypedList(this.productList);
        dest.writeParcelable(this.product, flags);
        dest.writeTypedList(this.corporateCategories);
        dest.writeTypedList(this.notificationArrayList);
        dest.writeList(this.ongoingContests);
        dest.writeList(this.completedArrayList);
        dest.writeTypedList(this.productListAll);
        dest.writeParcelable(this.statics, flags);
        dest.writeTypedList(this.fanContestList);
        dest.writeList(this.fanContestAllArrayList);
        dest.writeList(this.fanDownloadList);
        dest.writeTypedList(this.fanFilterArrayList);
        dest.writeString(this.msg);
        dest.writeString(this.title);
        dest.writeString(this.infoText);
    }

    public Payload() {
    }

    protected Payload(Parcel in) {
        this.data = in.readParcelable(User.class.getClassLoader());
        this.productList = in.createTypedArrayList(Product.CREATOR);
        this.product = in.readParcelable(Product.class.getClassLoader());
        this.corporateCategories = in.createTypedArrayList(Category.CREATOR);
        this.notificationArrayList = in.createTypedArrayList(NotificationApp.CREATOR);
        this.ongoingContests = new ArrayList<OngoingContest>();
        in.readList(this.ongoingContests, OngoingContest.class.getClassLoader());
        this.completedArrayList = new ArrayList<ContestCompleted>();
        in.readList(this.completedArrayList, ContestCompleted.class.getClassLoader());
        this.productListAll = in.createTypedArrayList(Product.CREATOR);
        this.statics = in.readParcelable(Statics.class.getClassLoader());
        this.fanContestList = in.createTypedArrayList(FanContest.CREATOR);
        this.fanContestAllArrayList = new ArrayList<FanContestAll>();
        in.readList(this.fanContestAllArrayList, FanContestAll.class.getClassLoader());
        this.fanDownloadList = new ArrayList<FanContestDownload>();
        in.readList(this.fanDownloadList, FanContestDownload.class.getClassLoader());
        this.fanFilterArrayList = in.createTypedArrayList(FanFilter.CREATOR);
        this.msg = in.readString();
        this.title = in.readString();
        this.infoText = in.readString();
    }

    public static final Parcelable.Creator<Payload> CREATOR = new Parcelable.Creator<Payload>() {
        @Override
        public Payload createFromParcel(Parcel source) {
            return new Payload(source);
        }

        @Override
        public Payload[] newArray(int size) {
            return new Payload[size];
        }
    };
}


