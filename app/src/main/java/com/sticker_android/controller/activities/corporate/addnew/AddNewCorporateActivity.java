package com.sticker_android.controller.activities.corporate.addnew;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sticker_android.R;
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.controller.adaptors.ViewPagerAdapter;
import com.sticker_android.model.User;
import com.sticker_android.utils.sharedpref.AppPref;

public class AddNewCorporateActivity extends AppBaseActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private AppPref appPref;
    private User userdata;
    private ViewPagerAdapter adapter;
    private TabLayout tabLayout;
    private Button btnPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_corporate);
        init();
        getuserInfo();
        setToolbar();
        setViewReferences();
        setViewListeners();
        toolbar.setNavigationIcon(R.drawable.back_arrow_small);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        addTabsDynamically();
        setBackground();
        setSelectedTabColor();
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
    }

    private void init() {

        appPref = new AppPref(getActivity());
    }

    private void getuserInfo() {
        userdata = appPref.getUserInfo();
    }

    /**
     * Method is used to set the toolbar title
     */
    private void setToolBarTitle() {
        TextView textView = (TextView) toolbar.findViewById(R.id.tvToolbar);
        textView.setText(getResources().getString(R.string.act_corp_txt_add_new));
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

    @Override
    protected void setViewListeners() {
        tabLayout.addOnTabSelectedListener(new TabListeners());
        btnPost.setOnClickListener(this);
    }

    private void setSelectedTabColor() {
        tabLayout.setTabTextColors(Color.parseColor("#AAFFFFFF"), Color.WHITE);
    }

    @Override
    protected void setViewReferences() {
        tabLayout = (TabLayout) findViewById(R.id.act_landing_tab);
        btnPost = (Button) findViewById(R.id.act_corp_add_new_btn_post);
    }

    @Override
    protected boolean isValidData() {
        return false;
    }


    private void setBackground() {
        tabLayout.setBackground(getResources().getDrawable(R.drawable.side_nav_corporate));
    }

    public void addTabsDynamically() {

        TabLayout.Tab firstTab = tabLayout.newTab();
        firstTab.setText(getString(R.string.txt_add_new_ads_frag)); // set the Text for the first Tab
        tabLayout.addTab(firstTab);

        TabLayout.Tab sec = tabLayout.newTab();
        sec.setText(getString(R.string.txt__add_products_frag)); // set the Text for the first Tab
        tabLayout.addTab(sec);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.act_corp_add_new_btn_post:
            Toast.makeText(getApplicationContext(),""+ tabLayout.getSelectedTabPosition(),Toast.LENGTH_SHORT).show();
                break;
             default:
        }
    }


    public class TabListeners implements TabLayout.OnTabSelectedListener {


        public TabListeners() {

        }

        @Override
        public void onTabSelected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    }
}
