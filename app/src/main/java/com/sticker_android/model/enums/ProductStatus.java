package com.sticker_android.model.enums;

/**
 * Created by satyendra on 4/12/18.
 */

public enum ProductStatus {

    PENDING("1"),
    APPROVED("2"),
    REJECTED("3"),
    EXPIRED("4");

    private String mStatus;

    ProductStatus(String status){
        this.mStatus = status;
    }

    public int getStatus(){
        return Integer.valueOf(mStatus).intValue();
    }
}
