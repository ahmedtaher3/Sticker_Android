package com.sticker_android.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by user on 26/3/18.
 */

public class User {

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

    public String getPasssword() {
        return passsword;
    }

    public void setPasssword(String passsword) {
        this.passsword = passsword;
    }

    @SerializedName("password")
    private String passsword;

    @SerializedName("product_id")
    private int productid;

    @SerializedName("product_name")
    private String productname;
    @SerializedName("type")
    private String type;

    @SerializedName("description")
    private String description;

    @SerializedName("expiry_date")
    private String expireDate;
    @SerializedName("image_path")
    private String imagePath;

    @SerializedName("title")
    private String title;
    @SerializedName("info_text")
    private String infoText;

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getProductid() {
        return productid;
    }

    public void setProductid(int productid) {
        this.productid = productid;
    }

    public String getProductname() {
        return productname;
    }

    public void setProductname(String productname) {
        this.productname = productname;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInfoText() {
        return infoText;
    }

    public void setInfoText(String infoText) {
        this.infoText = infoText;
    }


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
