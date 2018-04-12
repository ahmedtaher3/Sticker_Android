package com.sticker_android.model.enums;

/**
 * Created by satyendra on 4/12/18.
 */

public enum ProductStatus {

    PENDING("pending"),
    APPROVED("approved"),
    REJECTED("rejected"),
    EXPIRED("expired");

    private String mStatus;

    ProductStatus(String status){
        this.mStatus = status;
    }

    public String getStatus(){
        return mStatus;
    }
}
