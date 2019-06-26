package com.sticker_android.controller.adaptors;

import android.content.Context;

import com.sticker_android.model.Ads;

import java.util.List;
import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

/**
 * Created by A.taher on 9/14/2018.
 */

public class MainSliderAdapter extends SliderAdapter {

    Context context;
    List<Ads> my_data;


    public MainSliderAdapter (Context context, List<Ads> my_data)
    {
        this.context = context;
        this.my_data = my_data;
    }

    @Override
    public int getItemCount() {
        return my_data.size();
    }

    @Override
    public void onBindImageSlide(final int position, ImageSlideViewHolder viewHolder) {



                viewHolder.bindImageSlide(my_data.get(position).getPath());


    }
}