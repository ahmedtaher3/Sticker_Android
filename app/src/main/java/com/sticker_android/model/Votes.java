package com.sticker_android.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Votes {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("vote_desc")
    @Expose
    private String voteDesc;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("category_name")
    @Expose
    private String categoryName;
    @SerializedName("category_id")
    @Expose
    private String categoryId;
    @SerializedName("first_choice_img")
    @Expose
    private String firstChoiceImg;
    @SerializedName("first_choice_desc")
    @Expose
    private String firstChoiceDesc;
    @SerializedName("second_choice_img")
    @Expose
    private String secondChoiceImg;
    @SerializedName("second_choice_desc")
    @Expose
    private String secondChoiceDesc;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("image_url")
    @Expose
    private String imageUrl;
    @SerializedName("no_first_votes")
    @Expose
    private int noFirstVotes;
    @SerializedName("no_second_votes")
    @Expose
    private int noSecondVotes;
    @SerializedName("no_votes")
    @Expose
    private String noVotes;
    @SerializedName("did_user_voted")
    @Expose
    private boolean didUserVoted;
    @SerializedName("user_vote")
    @Expose
    private String userVote;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVoteDesc() {
        return voteDesc;
    }

    public void setVoteDesc(String voteDesc) {
        this.voteDesc = voteDesc;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getFirstChoiceImg() {
        return firstChoiceImg;
    }

    public void setFirstChoiceImg(String firstChoiceImg) {
        this.firstChoiceImg = firstChoiceImg;
    }

    public String getFirstChoiceDesc() {
        return firstChoiceDesc;
    }

    public void setFirstChoiceDesc(String firstChoiceDesc) {
        this.firstChoiceDesc = firstChoiceDesc;
    }

    public String getSecondChoiceImg() {
        return secondChoiceImg;
    }

    public void setSecondChoiceImg(String secondChoiceImg) {
        this.secondChoiceImg = secondChoiceImg;
    }

    public String getSecondChoiceDesc() {
        return secondChoiceDesc;
    }

    public void setSecondChoiceDesc(String secondChoiceDesc) {
        this.secondChoiceDesc = secondChoiceDesc;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getNoFirstVotes() {
        return noFirstVotes;
    }

    public void setNoFirstVotes(int noFirstVotes) {
        this.noFirstVotes = noFirstVotes;
    }

    public int getNoSecondVotes() {
        return noSecondVotes;
    }

    public void setNoSecondVotes(int noSecondVotes) {
        this.noSecondVotes = noSecondVotes;
    }

    public String getNoVotes() {
        return noVotes;
    }

    public void setNoVotes(String noVotes) {
        this.noVotes = noVotes;
    }

    public boolean getDidUserVoted() {
        return didUserVoted;
    }

    public void setDidUserVoted(boolean didUserVoted) {
        this.didUserVoted = didUserVoted;
    }

    public String getUserVote() {
        return userVote;
    }

    public void setUserVote(String userVote) {
        this.userVote = userVote;
    }

}
