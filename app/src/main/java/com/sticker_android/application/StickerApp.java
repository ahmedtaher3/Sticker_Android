package com.sticker_android.application;

import android.app.Activity;
import android.app.Application;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatDelegate;


import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.sticker_android.controller.activities.base.AppBaseActivity;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by user on 16/3/18.
 */

public class StickerApp extends Application {
    private AppBaseActivity mCurrentActivity=null;
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    private static StickerApp mInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setFontAttrId(uk.co.chrisjenx.calligraphy.R.attr.fontPath).build());
        mInstance = this;

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this)
                .memoryCacheExtraOptions(480, 480)
                .threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY - 1)
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024).memoryCacheSizePercentage(13)
                .discCacheSize(50 * 1024 * 1024)
                .discCacheFileCount(100)
                .discCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .imageDownloader(new BaseImageDownloader(this))
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .writeDebugLogs().build();
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);

    }
    public static StickerApp getInstance() {
        return mInstance;
    }

    public AppBaseActivity getCurrentActivity()
    {

        return  this.mCurrentActivity;
    }

    public void setCurrentActivity(Activity currentActivity)
    {
        this.mCurrentActivity= (AppBaseActivity) currentActivity;
    }
}
