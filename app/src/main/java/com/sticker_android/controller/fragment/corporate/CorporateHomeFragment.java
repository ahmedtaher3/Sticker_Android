package com.sticker_android.controller.fragment.corporate;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sticker_android.R;
import com.sticker_android.controller.activities.corporate.addnew.AddNewCorporateActivity;
import com.sticker_android.controller.adaptors.ViewPagerAdapter;
import com.sticker_android.controller.fragment.AboutUsFragment;
import com.sticker_android.controller.fragment.AccountSettingFragment;
import com.sticker_android.controller.fragment.ChangePasswordFragment;
import com.sticker_android.controller.fragment.ContactUsFragment;
import com.sticker_android.controller.fragment.TermsAndConditionFragment;
import com.sticker_android.controller.fragment.base.BaseFragment;
import com.sticker_android.controller.fragment.corporate.ad.AdsFragment;
import com.sticker_android.controller.fragment.corporate.product.ProductsFragment;
import com.sticker_android.model.User;
import com.sticker_android.utils.UserTypeEnum;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.sharedpref.AppPref;

/**
 * A simple {@link Fragment} subclass.
 */
public class CorporateHomeFragment extends BaseFragment implements View.OnClickListener {

    private FloatingActionButton fabAddNew;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ViewPagerAdapter adapter;
    private AppPref appPref;
    private User userdata;

    public CorporateHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_corporate_home, container, false);
        init();
        getuserInfo();
        setViewReferences(view);
        setViewListeners();
        setupViewPager();
        addFragmentToTab();
        setSelectedTabColor();
        setBackground();
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
    return view;
    }

    private void init() {

        appPref   =   new AppPref(getActivity());
    }

    private void getuserInfo() {
        userdata  =   appPref.getUserInfo();
    }

    @Override
    protected void setViewListeners() {

        fabAddNew.setOnClickListener(this);

        tabLayout.addOnTabSelectedListener(new TabListeners(viewPager));

    }

    @Override
    protected void setViewReferences(View view) {
        fabAddNew   =      (FloatingActionButton)view.findViewById(R.id.fabAddNew);
        viewPager   =      (ViewPager)view. findViewById(R.id.view_pager);
        tabLayout   =      (TabLayout)view. findViewById(R.id.act_landing_tab);

    }

    private void setSelectedTabColor() {
        tabLayout.setTabTextColors(Color.parseColor("#AAFFFFFF"), Color.WHITE);
    }

    private void setupViewPager() {
        tabLayout.setupWithViewPager(viewPager);
    }
    @Override
    protected boolean isValidData() {
        return false;
    }

    @Override
    public void onClick(View v) {

       switch (v.getId()){

           case R.id.fabAddNew:

               startActivity(new Intent(getActivity(), AddNewCorporateActivity.class));

           break;
       }
    }
    private void addFragmentToTab() {
        adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new AdsFragment(), getString(R.string.txt_ads_frag));
        adapter.addFragment(new ProductsFragment(),  getString(R.string.txt_products_frag));
        viewPager.setAdapter(adapter);

    }

    private void setBackground() {
        tabLayout.setBackground(getResources().getDrawable(R.drawable.side_nav_corporate));
    }

    public class TabListeners implements TabLayout.OnTabSelectedListener {

        private ViewPager viewPager;

        public TabListeners(ViewPager viewPager) {
            this.viewPager = viewPager;
        }

        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            Utils.hideKeyboard(getActivity());
            viewPager.setCurrentItem(tab.getPosition());
            Fragment fragment = adapter.getItem(tab.getPosition());
            if (fragment instanceof ChangePasswordFragment) {
                ((ChangePasswordFragment) fragment).clearField();
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    }
}
