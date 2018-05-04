package com.sticker_android.controller.activities.common.contest;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sticker_android.R;
import com.sticker_android.constant.AppConstant;
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.controller.fragment.corporate.contest.CorporateContestAdsFragment;
import com.sticker_android.controller.fragment.corporate.contest.CorporateContestProductFragment;
import com.sticker_android.model.User;
import com.sticker_android.model.corporateproduct.Product;
import com.sticker_android.model.notification.NotificationApp;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.sharedpref.AppPref;

import retrofit2.Call;

/**
 * Class is used for the notification
 */
public class ApplyCorporateContestActivity extends AppBaseActivity implements View.OnClickListener {

    private AppPref appPref;
    private User userdata;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private RelativeLayout rlTabLayoutContainer;
    private String TAG = ApplyCorporateContestActivity.class.getSimpleName();

    private FragmentManager mFragmentManager;
    private FrameLayout contarinerContest;
    private Button btnPostContest;
    private Product selectedProduct;
    private NotificationApp notificationObj;
    private ProgressBar progressBarSave;

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
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setSelectedTabIndicatorColor(Color.TRANSPARENT);

        setSelectedTabColor();
        replaceFragment(new CorporateContestAdsFragment());
        setBackground();
        changeStatusBarColor(getResources().getColor(R.color.colorstatusBarCorporate));

        getNotificationData();
    }

    private void getNotificationData() {
        if (getIntent().getExtras() != null) {

            notificationObj = getIntent().getExtras().getParcelable(AppConstant.NOTIFICATION_OBJ);
        }
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
        tabLayout = (TabLayout) findViewById(R.id.act_landing_tab);
        rlTabLayoutContainer = (RelativeLayout) findViewById(R.id.rlTabLayoutContainer);
        contarinerContest = (FrameLayout) findViewById(R.id.container_contest);
        btnPostContest = (Button) findViewById(R.id.btnPostContest);
        progressBarSave = (ProgressBar) findViewById(R.id.progressBarSave);
    }

    @Override
    protected boolean isValidData() {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPostContest:
                if (selectedProduct != null) {
                    saveContestApiCall();
                } else {
                    Utils.showToast(this, getString(R.string.txt_apply_for_context));
                }
                break;
        }
    }

    private void saveContestApiCall() {
        if (progressBarSave != null)
            progressBarSave.setVisibility(View.VISIBLE);

        if (notificationObj != null) {

            Call<ApiResponse> apiResponseCall = RestClient.getService().saveUserContest(userdata.getLanguageId(), userdata.getAuthrizedKey(), userdata.getId(), selectedProduct.getProductid(), notificationObj.acme.contestObj.contestId, "",notificationObj.notificatinId);
            apiResponseCall.enqueue(new ApiCall(this) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    if (progressBarSave != null)
                        progressBarSave.setVisibility(View.GONE);
                    if (apiResponse.status) {
                        Utils.showToast(ApplyCorporateContestActivity.this, getString(R.string.txt_successfully_applied_for_contest));
                        setResult(RESULT_OK);
                        onBackPressed();
                    }else
                    {
                        Toast.makeText(getActivity(),""+apiResponse.error.message,Toast.LENGTH_LONG).show();

                    }
                }

                @Override
                public void onFail(Call<ApiResponse> call, Throwable t) {
                    if (progressBarSave != null)
                        progressBarSave.setVisibility(View.GONE);
                }
            });
        }
    }

    public void disablePost(boolean isVisible) {
        if (isVisible)
            btnPostContest.setVisibility(View.VISIBLE);
        else
            btnPostContest.setVisibility(View.GONE);
    }


    public class TabListeners implements TabLayout.OnTabSelectedListener {

        public TabListeners() {

        }

        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            selectedProduct = null;
            switch (tab.getPosition()) {
                case 0:
                    replaceFragment(new CorporateContestAdsFragment());
                    break;
                case 1:
                    replaceFragment(new CorporateContestProductFragment());
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
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container_contest,
                fragment);
        fragmentTransaction.commit();
    }


    public void saveContest(Product product) {

        selectedProduct = product;
    }


}
