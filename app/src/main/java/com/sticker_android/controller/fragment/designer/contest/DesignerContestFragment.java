package com.sticker_android.controller.fragment.designer.contest;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sticker_android.R;
import com.sticker_android.controller.fragment.base.BaseFragment;
import com.sticker_android.model.User;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.fragmentinterface.UpdateToolbarTitle;
import com.sticker_android.utils.sharedpref.AppPref;

/**
 * Created by user on 29/3/18.
 */

public class DesignerContestFragment extends BaseFragment{

    private UpdateToolbarTitle mUpdateToolbarCallback;
    private AppPref appPref;
    private User userdata;

    private TabLayout tabLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.frag_designer_contest, container, false);
        init();
        getuserInfo();
        setViewReferences(view);
        setViewListeners();
        setBackground();
        addTabsDynamically();
        replaceFragment(new DesignerContestOngingFragment());
        // Inflate the layout for this fragment
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setSelectedTabIndicatorColor(Color.TRANSPARENT);
        setSelectedTabColor();
        return view;
    }
    private void setSelectedTabColor() {
        tabLayout.setTabTextColors(Color.parseColor("#AAFFFFFF"), Color.WHITE);
    }

    private void init() {
        appPref = new AppPref(getActivity());
    }

    private void setBackground() {
        tabLayout.setBackground(getResources().getDrawable(R.drawable.side_nav_designer));
    }

    private void getuserInfo() {
        userdata = appPref.getUserInfo();
    }

    public void addTabsDynamically() {

        TabLayout.Tab adsTab = tabLayout.newTab();
        adsTab.setText(getString(R.string.txt_onging)); // set the Text for the first Tab
        tabLayout.addTab(adsTab);

        TabLayout.Tab productTab = tabLayout.newTab();
        productTab.setText(getString(R.string.txt_completed)); // set the Text for the Second Tab
        tabLayout.addTab(productTab);
        Utils.setTabLayoutDivider(tabLayout, getActivity());
    }


    @Override
    protected void setViewListeners() {
        tabLayout.addOnTabSelectedListener(new TabListeners());
    }

    @Override
    protected void setViewReferences(View view) {
        tabLayout = (TabLayout) view.findViewById(R.id.act_landing_tab);
    }

    @Override
    protected boolean isValidData() {
        return false;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
          //  mUpdateToolbarCallback = (UpdateToolbarTitle) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    public class TabListeners implements TabLayout.OnTabSelectedListener {

        public TabListeners() {

        }

        @Override
        public void onTabSelected(TabLayout.Tab tab) {

            switch (tab.getPosition()) {
                case 0:
                    replaceFragment(new DesignerContestOngingFragment());
                    break;
                case 1:
                    replaceFragment(new DesignerContestCompletedFragment());
                    break;

            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    }

    /**
     * replace existing fragment of container
     *
     * @param fragment
     */
    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction =
                getChildFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container_contest,
                fragment);
        fragmentTransaction.commit();
    }
}
