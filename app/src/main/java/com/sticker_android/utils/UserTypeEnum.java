package com.sticker_android.utils;

/**
 * Created by user on 30/3/18.
 */

public enum  UserTypeEnum {
    FAN,CORPORATE,DESIGNER;
    //fan,corporate,designer;
    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
