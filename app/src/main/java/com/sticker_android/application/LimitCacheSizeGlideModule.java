package com.sticker_android.application;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.module.GlideModule;

/**
 * Created by user on 30/5/18.
 */

public class LimitCacheSizeGlideModule implements GlideModule {

    private static final int PHOTOS_CACHE_SIZE = 5;	// Mbytes

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
        builder.setDiskCache(new DiskLruCacheFactory(Glide.getPhotoCacheDir(context).getAbsolutePath(), PHOTOS_CACHE_SIZE * 1024 * 1024));
    }

    @Override
    public void registerComponents(Context context, Glide glide) {

    }
}
