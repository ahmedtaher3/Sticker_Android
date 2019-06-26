package com.sticker_android.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Ads {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("path")
    @Expose
    private String path;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("place")
    @Expose
    private String place;
    @SerializedName("placeid")
    @Expose
    private String placeid;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getPlaceid() {
        return placeid;
    }

    public void setPlaceid(String placeid) {
        this.placeid = placeid;
    }

    public Ads(Integer id, String path, String description, String place, String placeid, String createdAt, String updatedAt) {
        this.id = id;
        this.path = path;
        this.description = description;
        this.place = place;
        this.placeid = placeid;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

}
