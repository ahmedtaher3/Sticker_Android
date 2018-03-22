package com.sticker_android.application;

import android.app.Application;
import android.support.multidex.MultiDex;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by user on 16/3/18.
 */

public class StickerApp extends Application {

    private static StickerApp mInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setFontAttrId(uk.co.chrisjenx.calligraphy.R.attr.fontPath).build());
        mInstance=this;

    }
    public static StickerApp getInstance() {
        return mInstance;
    }

}
