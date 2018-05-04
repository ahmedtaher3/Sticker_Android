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

import com.sticker_android.R;
import com.sticker_android.constant.AppConstant;
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.controller.fragment.designer.contest.DesignerContestEmojiFragment;
import com.sticker_android.controller.fragment.designer.contest.DesignerContestGifFragment;
import com.sticker_android.controller.fragment.designer.contest.DesignerContestStickerFragment;
import com.sticker_android.model.User;
import com.sticker_android.model.corporateproduct.Product;
import com.sticker_android.model.notification.NotificationApp;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.sharedpref.AppPref;

import java.util.ArrayList;

import retrofit2.Call;

public class ApplyDesignerContestActivity extends AppBaseActivity implements View.OnClickListener {
    ArrayList<String> strings = new ArrayList<>();
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
        setContentView(R.layout.activity_apply_designer_contest);
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
        replaceFragment( new DesignerContestStickerFragment());
        setBackground();
        getNotificationData();
        changeStatusBarColor(getResources().getColor(R.color.colorstatusBarDesigner));

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
        toolbar.setBackground(getResources().getDrawable(R.drawable.designer_header_hdpi));
    }

    private void getNotificationData() {
        if (getIntent().getExtras() != null) {

            notificationObj = getIntent().getExtras().getParcelable(AppConstant.NOTIFICATION_OBJ);
        }
    }



    public void addTabsDynamically() {

        TabLayout.Tab stickerTab = tabLayout.newTab();
        stickerTab.setText(getString(R.string.stickers)); // set the Text for the first Tab
        tabLayout.addTab(stickerTab);

        TabLayout.Tab gifTab = tabLayout.newTab();
        gifTab.setText(getString(R.string.gif)); // set the Text for the Second Tab
        tabLayout.addTab(gifTab);
        TabLayout.Tab emojiTab = tabLayout.newTab();
        emojiTab.setText(getString(R.string.emoji)); // set the Text for the Second Tab
        tabLayout.addTab(emojiTab);

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

        tabLayout               =     (TabLayout) findViewById(R.id.act_landing_tab);
        rlTabLayoutContainer    =     (RelativeLayout) findViewById(R.id.rlTabLayoutContainer);
        contarinerContest       =     (FrameLayout) findViewById(R.id.container_contest);
        btnPostContest          =     (Button)findViewById(R.id.btnPostContest);
        progressBarSave         =     (ProgressBar) findViewById(R.id.progressBarSave);
    }

    @Override
    protected boolean isValidData() {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnPostContest:
                if (selectedProduct != null) {
                    saveContestApiCall();
                }else {
                    Utils.showToast(this,getString(R.string.txt_apply_for_context));
                }
                break;

        }
    }


    public class TabListeners implements TabLayout.OnTabSelectedListener {

        public TabListeners() {

        }

        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            selectedProduct=null;
            switch (tab.getPosition()) {
                case 0:
                    replaceFragment( new DesignerContestStickerFragment());
                    break;
                case 1:
                    replaceFragment( new DesignerContestGifFragment());
                    break;
                case 2:
                    replaceFragment( new DesignerContestEmojiFragment());
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

    public void disablePost(boolean isVisible) {
        if (isVisible)
            btnPostContest.setVisibility(View.VISIBLE);
        else
            btnPostContest.setVisibility(View.GONE);
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



    public void saveContest(Product product) {

        selectedProduct = product;
    }

    private void saveContestApiCall() {
if(progressBarSave!=null)
        progressBarSave.setVisibility(View.VISIBLE);

        if (notificationObj != null) {

            Call<ApiResponse> apiResponseCall = RestClient.getService().saveUserContest(userdata.getLanguageId(), userdata.getAuthrizedKey(), userdata.getId(), selectedProduct.getProductid(), notificationObj.acme.contestObj.contestId, "",notificationObj.notificatinId);
            apiResponseCall.enqueue(new ApiCall(this) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    if(progressBarSave!=null)
                    progressBarSave.setVisibility(View.GONE);
                    if (apiResponse.status) {
                        Utils.showToast(ApplyDesignerContestActivity.this, getString(R.string.txt_successfully_applied_for_contest));
                       setResult(RESULT_OK);
                        onBackPressed();
                    }
                }

                @Override
                public void onFail(Call<ApiResponse> call, Throwable t) {
                    if(progressBarSave!=null)
                    progressBarSave.setVisibility(View.GONE);
                }
            });
        }
    }
}
