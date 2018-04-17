package com.sticker_android.controller.activities.common.contest;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sticker_android.R;
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.controller.adaptors.ContestAdaptor;
import com.sticker_android.controller.fragment.corporate.contest.CorporateContestAdsFragment;
import com.sticker_android.controller.fragment.corporate.contest.CorporateContestProductFragment;
import com.sticker_android.model.User;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.sharedpref.AppPref;

import java.util.ArrayList;

/**
 * Class is used for the notification
 */
public class ApplyContestActivity extends AppBaseActivity implements View.OnClickListener{
    private RecyclerView recNotification;
    ArrayList<String> strings = new ArrayList<>();
    private AppPref appPref;
    private User userdata;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private RelativeLayout rlTabLayoutContainer;
    private String TAG = ApplyContestActivity.class.getSimpleName();

    private FragmentManager mFragmentManager;
    private FrameLayout contarinerContest;
    private Button btnPostContest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest);
        init();
        getuserInfo();
        setToolbar();
        toolbar.setNavigationIcon(R.drawable.back_arrow_small);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setViewReferences();
        setViewListeners();
        mFragmentManager = getSupportFragmentManager();
        addTabsDynamically();
        strings.add("hello test");
        setAdaptor();
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setSelectedTabIndicatorColor(Color.TRANSPARENT);

        setSelectedTabColor();
        replaceFragment( new CorporateContestAdsFragment());
        setBackground();
    }

    private void setAdaptor() {
        ContestAdaptor contestAdaptor = new ContestAdaptor(this, strings);
        recNotification.setAdapter(contestAdaptor);
    }

    private void init() {
        appPref = new AppPref(getActivity());
    }

    private void setBackground() {
        tabLayout.setBackground(getResources().getDrawable(R.drawable.side_nav_corporate));
    }

    private void getuserInfo() {
        userdata = appPref.getUserInfo();
    }

    /**
     * Method is used to set the toolbar title
     */
    private void setToolBarTitle() {
        TextView textView = (TextView) toolbar.findViewById(R.id.tvToolbar);
        textView.setText(getResources().getString(R.string.txt_contest));
        toolbar.setTitle(" ");
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

    /**
     * Method is used to set the toolbar background
     */
    private void setToolbarBackground() {
        toolbar.setBackground(getResources().getDrawable(R.drawable.corporate_header_hdpi));
    }



    public void addTabsDynamically() {

        TabLayout.Tab adsTab = tabLayout.newTab();
        adsTab.setText(getString(R.string.txt_ads_frag)); // set the Text for the first Tab
        tabLayout.addTab(adsTab);

        TabLayout.Tab productTab = tabLayout.newTab();
        productTab.setText(getString(R.string.txt_products_frag)); // set the Text for the Second Tab
        tabLayout.addTab(productTab);
        Utils.setTabLayoutDivider(tabLayout, this);
    }

    private void setSelectedTabColor() {
        tabLayout.setTabTextColors(Color.parseColor("#AAFFFFFF"), Color.WHITE);
    }

    @Override
    protected void setViewListeners() {
        tabLayout.addOnTabSelectedListener(new TabListeners());
        btnPostContest.setOnClickListener(this);
    }

    @Override
    protected void setViewReferences() {
        recNotification = findViewById(R.id.recNotification);
        tabLayout = (TabLayout) findViewById(R.id.act_landing_tab);
        rlTabLayoutContainer = (RelativeLayout) findViewById(R.id.rlTabLayoutContainer);
        contarinerContest = (FrameLayout) findViewById(R.id.container_contest);
        btnPostContest=(Button)findViewById(R.id.btnPostContest);
    }

    @Override
    protected boolean isValidData() {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnPostContest:

                break;
        }
    }


    public class TabListeners implements TabLayout.OnTabSelectedListener {

        public TabListeners() {

        }

        @Override
        public void onTabSelected(TabLayout.Tab tab) {

            switch (tab.getPosition()) {
                case 0:
                    replaceFragment( new CorporateContestAdsFragment());
                    break;
                case 1:
                    replaceFragment( new CorporateContestProductFragment());
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
    private void replaceFragment( Fragment fragment) {
        FragmentTransaction fragmentTransaction   =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container_contest,
                fragment);
        fragmentTransaction.commit();
    }






}
