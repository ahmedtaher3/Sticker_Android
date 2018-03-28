package com.sticker_android.controller.listeners;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

/**
 * Created by user on 26/7/17.
 */


public class TabListeners implements TabLayout.OnTabSelectedListener {

    private ViewPager viewPager;
    public TabListeners(ViewPager viewPager){
        this.viewPager=viewPager;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}