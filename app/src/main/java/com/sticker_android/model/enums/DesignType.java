package com.sticker_android.model.enums;

/**
 * Created by satyendra on 4/10/18.
 */

public enum DesignType {

    stickers("Stickers"),
    gif("GIF"),
    emoji("Emoji"),
    ads("Ads"),
    filter("Filter"),
    contest("Contest"),
    products("Product"),;

    private String mType;
    DesignType(String type){
        this.mType = type;
    }

    public String getType(){
        return mType;
    }
}
