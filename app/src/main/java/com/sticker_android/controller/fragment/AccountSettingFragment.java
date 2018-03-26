package com.sticker_android.controller.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sticker_android.R;
import com.sticker_android.controller.adaptors.ViewPagerAdapter;
import com.sticker_android.controller.fragment.base.BaseFragment;
import com.sticker_android.controller.listeners.TabListeners;


public class AccountSettingFragment extends BaseFragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ViewPagerAdapter adapter;
    public AccountSettingFragment() {
        // Required empty public constructor
    }

      public static AccountSettingFragment newInstance(String param1, String param2) {
        AccountSettingFragment fragment = new AccountSettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_account_setting, container, false);
        setViewReferences(view);
        setViewListeners();
        setupViewPager();
        setSelectedTabColor();
        setupViewPager();
        addFragmentToTab();
        return view;
    }

    private void addFragmentToTab() {
        adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new ChangePasswordFragment(), "Change Password");
        adapter.addFragment(new ContactUsFragment(), "Contact Us ");
        adapter.addFragment(new ContactUsFragment(), "Terms & Conditions");
        adapter.addFragment(new AboutUsFragment(), "About Us ");
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);
    }

    private void setSelectedTabColor() {
        tabLayout.setTabTextColors(R.color.colorTabUnselected, Color.WHITE);
    }

    private void setupViewPager() {
        tabLayout.setupWithViewPager(viewPager);
    }


    @Override
    public void onDetach() {
        super.onDetach();

    }


    @Override
    protected void setViewListeners() {
        tabLayout.addOnTabSelectedListener(new TabListeners(viewPager));
    }

    @Override
    protected void setViewReferences(View view) {
        viewPager = (ViewPager)view. findViewById(R.id.view_pager);
        tabLayout = (TabLayout)view. findViewById(R.id.act_landing_tab);
    }

    @Override
    protected boolean isValidData() {
        return false;
    }
}
