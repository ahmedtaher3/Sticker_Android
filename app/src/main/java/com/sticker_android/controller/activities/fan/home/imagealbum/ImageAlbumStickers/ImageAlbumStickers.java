package com.sticker_android.controller.activities.fan.home.imagealbum.ImageAlbumStickers;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.sticker_android.R;
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.controller.fragment.fan.fanhome.fanfilter.FanFilterAdminStickerFragment;
import com.sticker_android.controller.fragment.fan.fanhome.fanfilter.FanFilterDesignerStickerFragment;
import com.sticker_android.utils.Utils;

public class ImageAlbumStickers extends AppBaseActivity {

    private TabLayout tabLayout;
    private Toolbar toolbar;
    public static final String FILTER_IMAGE_TYPE = "filter_image_type";
    public static final String SELECTED_FILTER = "selected_filter";
    private String mFilterImageType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_album_stickers);
        setViewReferences();
        setViewListeners();
        getIntentData();
        setToolbar();
        addTabsDynamically();
        replaceFragment(FanFilterAdminStickerFragment.newInstance(mFilterImageType));
        changeStatusBarColor(getResources().getColor(R.color.colorstatusBarFan));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setSelectedTabIndicatorColor(Color.TRANSPARENT);
        toolbar.setNavigationIcon(R.drawable.back_arrow_small);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        setBackground();
        setSelectedTabColor();
    }

    private void getIntentData() {
        if (getIntent() != null) {
            mFilterImageType = getIntent().getStringExtra(FILTER_IMAGE_TYPE);
        }
    }

    private void setBackground() {
        tabLayout.setBackground(getResources().getDrawable(R.drawable.side_nav_fan));
    }

    private void setSelectedTabColor() {
        tabLayout.setTabTextColors(Color.parseColor("#77FFFFFF"), Color.WHITE);
    }

    public void addTabsDynamically() {

        if (mFilterImageType != null) {
            if (mFilterImageType.equalsIgnoreCase("stickers")) {
                TabLayout.Tab stickerTab = tabLayout.newTab();
                stickerTab.setText(getString(R.string.stickers)); // set the Text for the first Tab
                tabLayout.addTab(stickerTab);

                TabLayout.Tab designerStickerTab = tabLayout.newTab();
                designerStickerTab.setText(R.string.txt_designer_stickers); // set the Text for the first Tab
                tabLayout.addTab(designerStickerTab);

            } else {
                TabLayout.Tab stickerTab = tabLayout.newTab();
                stickerTab.setText(getString(R.string.emoji)); // set the Text for the first Tab
                tabLayout.addTab(stickerTab);

                TabLayout.Tab designerStickerTab = tabLayout.newTab();
                designerStickerTab.setText(R.string.txt_designer_emoji); // set the Text for the first Tab
                tabLayout.addTab(designerStickerTab);

            }
        }

        Utils.setTabLayoutDivider(tabLayout, getActivity());
    }

    /**
     * Method is used to set the toolbar
     */
    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setToolbarBackground();
        setToolBarTitle();
        setSupportActionBar(toolbar);
    }

    private void setToolBarTitle() {
        TextView textView = (TextView) toolbar.findViewById(R.id.tvToolbar);
        if (mFilterImageType != null)
            if (mFilterImageType.equalsIgnoreCase("stickers")) {
                textView.setText(getResources().getString(R.string.stickers));
            } else {

                textView.setText(getResources().getString(R.string.emojis));
            }
        toolbar.setTitle(" ");
    }

    /**
     * Method is used to set the toolbar background
     */
    private void setToolbarBackground() {
        toolbar.setBackground(getResources().getDrawable(R.drawable.fan_header_hdpi));
    }


    /**
     * replace existing fragment of container
     *
     * @param fragment
     */
    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container_home_sticker,
                fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void setViewListeners() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {

                    case 0:
                        replaceFragment(FanFilterAdminStickerFragment.newInstance(mFilterImageType));
                        break;
                    case 1:
                        replaceFragment(FanFilterDesignerStickerFragment.newInstance(mFilterImageType));
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected void setViewReferences() { tabLayout = (TabLayout) findViewById(R.id.act_landing_tab);


    }

    @Override
    protected boolean isValidData() {
        return false;
    }
}
