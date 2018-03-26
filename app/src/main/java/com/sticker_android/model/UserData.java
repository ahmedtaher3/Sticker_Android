package com.sticker_android.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by user on 26/3/18.
 */

public class UserData {

    @SerializedName("id")
    private String id;
    @SerializedName("first_name")

    private String firstName;
    @SerializedName("email")

    private String email;
    @SerializedName("pid")

    private String pid;
    @SerializedName("device_type")

    private String deviceType;
    @SerializedName("language_id")

    private String languageId;
    @SerializedName("last_name")

    private String lastName;
    @SerializedName("mobile")

    private String mobile;
    @SerializedName("image_url")

    private String imageUrl;
    @SerializedName("user_type")

    private String userType;
    @SerializedName("company_address")

    private String companyAddress;
    @SerializedName("company_name")

    private String companyName;
    @SerializedName("company_logo")

    private String companyLogo;
    @SerializedName("authrized_key")

    private String authrizedKey;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getLanguageId() {
        return languageId;
    }

    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyLogo() {
        return companyLogo;
    }

    public void setCompanyLogo(String companyLogo) {
        this.companyLogo = companyLogo;
    }

    public String getAuthrizedKey() {
        return authrizedKey;
    }

    public void setAuthrizedKey(String authrizedKey) {
        this.authrizedKey = authrizedKey;
    }

}
