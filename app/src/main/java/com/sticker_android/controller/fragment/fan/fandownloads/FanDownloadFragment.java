package com.sticker_android.controller.fragment.fan.fandownloads;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.sticker_android.R;
import com.sticker_android.controller.fragment.base.BaseFragment;
import com.sticker_android.controller.fragment.fan.fanhome.FanHomeAdsFragment;
import com.sticker_android.controller.fragment.fan.fanhome.FanHomeEmojiFragment;
import com.sticker_android.controller.fragment.fan.fanhome.FanHomeProductsFragment;
import com.sticker_android.controller.fragment.fan.fanhome.FanHomeStickerFragment;
import com.sticker_android.model.User;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.sharedpref.AppPref;

import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class FanDownloadFragment extends BaseFragment {

    private TabLayout tabLayout;
    private AppPref appPref;
    private User mUserdata;
    private RelativeLayout rlFragmentContainer;
    private int index;
    private Call<ApiResponse> apiResponseCall;
    private SearchView searchView;
    private MenuItem item;


    public FanDownloadFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fan_home, container, false);

        init();
        getuserInfo();
        setViewReferences(view);
        setViewListeners();
        addTabsDynamically();
        setSelectedTabColor();
        setBackground();
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setSelectedTabIndicatorColor(Color.TRANSPARENT);
        setHasOptionsMenu(true);
        replaceFragment(new FanHomeStickerFragment());
        return view;
    }

    private void init() {
        appPref = new AppPref(getActivity());
    }

    private void getuserInfo() {
        mUserdata = appPref.getUserInfo();
    }


    @Override
    protected void setViewListeners() {

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        replaceFragment(new FanHomeStickerFragment());
                        break;
                    case 1:
                        replaceFragment(new FanHomeEmojiFragment());
                        break;
                    case 2:
                        replaceFragment(new FanHomeEmojiFragment());
                        break;
                    case 3:
                        replaceFragment(new FanHomeAdsFragment());
                        break;
                    case 4:
                        replaceFragment(new FanHomeProductsFragment());
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
    protected void setViewReferences(View view) {
        tabLayout = (TabLayout) view.findViewById(R.id.act_landing_tab);
        rlFragmentContainer = (RelativeLayout) view.findViewById(R.id.rlFragmentContainer);

    }

    @Override
    protected boolean isValidData() {
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.corporate_menu, menu);
    }

    private void setSelectedTabColor() {
        tabLayout.setTabTextColors(Color.parseColor("#77FFFFFF"), Color.WHITE);
    }

    private void setBackground() {
        tabLayout.setBackground(getResources().getDrawable(R.drawable.side_nav_fan));
    }

    public void addTabsDynamically() {

        TabLayout.Tab stickerTab = tabLayout.newTab();
        stickerTab.setText(getString(R.string.stickers)); // set the Text for the first Tab
        tabLayout.addTab(stickerTab);

        TabLayout.Tab gifTab = tabLayout.newTab();
        gifTab.setText(getString(R.string.gif)); // set the Text for the first Tab
        tabLayout.addTab(gifTab);

        TabLayout.Tab emojiTab = tabLayout.newTab();
        emojiTab.setText(getString(R.string.emoji)); // set the Text for the first Tab
        tabLayout.addTab(emojiTab);

        TabLayout.Tab AdsTab = tabLayout.newTab();
        AdsTab.setText(getString(R.string.txt_ads_frag)); // set the Text for the first Tab
        tabLayout.addTab(AdsTab);

        TabLayout.Tab productsTab = tabLayout.newTab();
        productsTab.setText(getString(R.string.txt_products_frag)); // set the Text for the first Tab
        tabLayout.addTab(productsTab);

        Utils.setTabLayoutDivider(tabLayout, getActivity());
    }

    /**
     * replace existing fragment of container
     *
     * @param fragment
     */
    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction =
                getChildFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container_fan_home,
                fragment);
        fragmentTransaction.commit();
    }


}
