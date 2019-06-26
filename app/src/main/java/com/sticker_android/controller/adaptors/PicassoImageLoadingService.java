package com.sticker_android.controller.adaptors;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import com.sticker_android.network.ApiConstant;

import ss.com.bannerslider.ImageLoadingService;

/**
 * Created by A.taher on 9/14/2018.
 */

public class PicassoImageLoadingService implements ImageLoadingService {
    public Context context;

    public PicassoImageLoadingService(Context context) {
        this.context = context;
    }

    @Override
    public void loadImage(String url, ImageView imageView) {

        Glide.with(context.getApplicationContext())
                .load(url)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })
                .placeholder(imageView.getDrawable())
                .into(imageView);


    }

    @Override
    public void loadImage(int resource, ImageView imageView) {

        Glide.with(context.getApplicationContext())
                .load(resource)
                .placeholder(imageView.getDrawable())
                .into(imageView);
    }

    @Override
    public void loadImage(String url, int placeHolder, int errorDrawable, ImageView imageView) {
        Glide.with(context.getApplicationContext())
                .load(url)
                .placeholder(imageView.getDrawable())
                .into(imageView);;
    }
}