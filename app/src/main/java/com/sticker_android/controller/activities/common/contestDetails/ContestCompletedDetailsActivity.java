package com.sticker_android.controller.activities.common.contestDetails;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.sticker_android.R;
import com.sticker_android.constant.AppConstant;
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.controller.activities.common.contestlist.ContestCompletedAllUserActivity;
import com.sticker_android.model.User;
import com.sticker_android.model.contest.ContestCompleted;
import com.sticker_android.utils.AppLogger;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.sharedpref.AppPref;

public class ContestCompletedDetailsActivity extends AppBaseActivity  implements View.OnClickListener{

    ImageView imvSelected, imvOfContest, imvProductImage;
    CardView cardItem;
    ProgressBar pgrImage;
    TextView totalNumberOfCount, tvContestStatus;
    TextView tvFeatured;
    private Toolbar toolbar;
    private AppPref appPref;
    private User mUserData;
    private ContestCompleted productObj;
    private String userContestId;

    FloatingActionButton fabShowAllItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest_completed_details);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        init();
        getProductData();
        setSupportActionBar(toolbar);
        setToolbarBackground();
        setViewReferences();
        setViewListeners();
        toolbar.setNavigationIcon(R.drawable.back_arrow_small);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setProductInfo();
        changeStatusBarColor(getResources().getColor(R.color.colorstatusBarCorporate));

    }

    private void setProductInfo() {

        if (productObj.isWinner > 0) {
            tvContestStatus.setText(R.string.txt_winner);
            tvContestStatus.setTextColor(getResources().getColor(R.color.colorHomeGreen));
        } else {
            tvContestStatus.setTextColor(Color.RED);
            tvContestStatus.setText(R.string.txt_looser);
        }
        totalNumberOfCount.setText(Utils.format(productObj.totalLike));
        if (productObj.productList.getImagePath() != null && !productObj.productList.getImagePath().isEmpty())
            Glide.with(this)
                    .load(productObj.productList.getImagePath()).fitCenter()
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            pgrImage.setVisibility(View.GONE);
                            imvProductImage.setVisibility(View.GONE);

                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            pgrImage.setVisibility(View.GONE);
                            imvProductImage.setVisibility(View.GONE);

                            return false;
                        }
                    })
                    .into(imvOfContest);
        if (productObj.productList.isFeatured > 0)
            tvFeatured.setVisibility(View.VISIBLE);
        else
            tvFeatured.setVisibility(View.GONE);

        cardItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }



    @Override
    protected void setViewListeners() {
        fabShowAllItems.setOnClickListener(this);
    }
    /**
     * Method is used to set the toolbar background
     */
    private void setToolbarBackground() {
        toolbar.setBackground(getResources().getDrawable(R.drawable.corporate_header_hdpi));
    }


    @Override
    protected void setViewReferences() {

        cardItem = (CardView) findViewById(R.id.card_view);
        imvSelected = (ImageView) findViewById(R.id.imvSelected);
        imvOfContest = (ImageView) findViewById(R.id.imvOfContest);
        imvProductImage = (ImageView) findViewById(R.id.imvProductImage);
        pgrImage = (ProgressBar) findViewById(R.id.pgrImage);
        totalNumberOfCount = (TextView) findViewById(R.id.tv_total_count_number);
        tvContestStatus = (TextView) findViewById(R.id.tv_contest_status);
        tvFeatured = (TextView) findViewById(R.id.tvFeatured);
        fabShowAllItems =(FloatingActionButton)findViewById(R.id.fabShowAllItems);

    }

    private void init() {
        appPref = new AppPref(this);
        mUserData = appPref.getUserInfo();
    }

    private void setToolBarTitle(String type) {
        TextView textView = (TextView) toolbar.findViewById(R.id.tvToolbar);
        textView.setText(R.string.txt_completed_contest);

        toolbar.setTitle("");
    }


    private void getProductData() {

        if (getIntent().getExtras() != null) {

            productObj = getIntent().getExtras().getParcelable(AppConstant.PRODUCT_OBJ_KEY);
            if (productObj != null)
                setToolBarTitle(getResources().getString(R.string.txt_completed_contest));
        }
        userContestId = getIntent().getExtras().getString("userContestId");
    }


    @Override
    protected boolean isValidData() {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fabShowAllItems:
                moveToDetails();
                break;
        }
    }


    private void moveToDetails() {
        Bundle bundle = new Bundle();

        bundle.putParcelable(AppConstant.PRODUCT_OBJ_KEY, productObj.productList);

        Intent intent = new Intent(this, ContestCompletedAllUserActivity.class);

        intent.putExtras(bundle);
        intent.putExtra("userContestId", userContestId);
        AppLogger.debug(ContestDetailsActivity.class.getSimpleName(),"userContestId"+userContestId);
        startActivityForResult(intent, AppConstant.INTENT_PRODUCT_DETAILS);

        overridePendingTransition(R.anim.activity_animation_enter,
                R.anim.activity_animation_exit);
    }

}
