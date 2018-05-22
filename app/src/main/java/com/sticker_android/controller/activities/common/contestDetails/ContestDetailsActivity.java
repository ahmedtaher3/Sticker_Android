package com.sticker_android.controller.activities.common.contestDetails;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.sticker_android.R;
import com.sticker_android.constant.AppConstant;
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.controller.activities.common.contestlist.ContestAllItemListActivity;
import com.sticker_android.model.User;
import com.sticker_android.model.corporateproduct.Product;
import com.sticker_android.utils.AppLogger;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.sharedpref.AppPref;

public class ContestDetailsActivity extends AppBaseActivity implements View.OnClickListener {

    private FloatingActionButton fabShowAllItems;
    private Toolbar toolbar;
    private AppPref appPref;
    private User mUserData;
    private Product productObj;

    public ImageView imvSelected, imvOfContest, imvProductImage;
    public CardView cardItem;
    public ProgressBar pgrImage;
    public TextView tvEndDate;
    public CheckBox checkboxLike;
    public TextView tvFeatured;
    RelativeLayout rlcontestCompleted;
    private String userContestId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest_details);
        setViewReferences();
        setViewListeners();
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
        measureImageWidthHeight();
        changeStatusBarColor(getResources().getColor(R.color.colorstatusBarCorporate));

        setProductData();
    }

    private void setProductData() {
        tvEndDate.setText(Utils.dateModify(productObj.getExpireDate()));
        checkboxLike.setText(Utils.format(productObj.statics.likeCount));
        if (productObj.statics.likeCount > 0) {
            checkboxLike.setChecked(true);
            checkboxLike.setButtonDrawable(getResources().getDrawable(R.drawable.ic_hand));
        } else {
            checkboxLike.setChecked(false);
            checkboxLike.setButtonDrawable(getResources().getDrawable(R.drawable.ic_like));

        }
        checkboxLike.setText("" + productObj.statics.likeCount);
        if (productObj.getImagePath() != null && !productObj.getImagePath().isEmpty())
            Glide.with(this)
                    .load(productObj.getImagePath()).fitCenter()
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
        if (productObj.isFeatured > 0)
            tvFeatured.setVisibility(View.VISIBLE);
        else
            tvFeatured.setVisibility(View.GONE);


    }

    @Override
    protected void setViewListeners() {
        fabShowAllItems.setOnClickListener(this);
    }

    @Override
    protected void setViewReferences() {

        fabShowAllItems = (FloatingActionButton) findViewById(R.id.fabShowAllItems);
        cardItem = (CardView) findViewById(R.id.card_view);
        imvSelected = (ImageView) findViewById(R.id.imvSelected);
        imvOfContest = (ImageView) findViewById(R.id.imvOfContest);
        imvProductImage = (ImageView) findViewById(R.id.imvProductImage);
        pgrImage = (ProgressBar) findViewById(R.id.pgrImage);
        tvEndDate = (TextView) findViewById(R.id.tv_name);
        checkboxLike = (CheckBox) findViewById(R.id.checkboxLike);
        tvFeatured = (TextView) findViewById(R.id.tvFeatured);

    }

    @Override
    protected boolean isValidData() {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabShowAllItems:
                moveToDetails();
                break;
        }
    }


    private void measureImageWidthHeight() {

        ViewTreeObserver vto = imvOfContest.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                imvOfContest.getViewTreeObserver().removeOnPreDrawListener(this);
                int finalWidth = imvOfContest.getMeasuredWidth();
                int height = finalWidth * 3 / 5;
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imvOfContest.getLayoutParams();

                //      LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imvOfAds.getLayoutParams();
                layoutParams.height = height;
                imvOfContest.setLayoutParams(layoutParams);
                return true;
            }
        });
    }

    /**
     * Method is used to set the toolbar background
     */
    private void setToolbarBackground() {
        toolbar.setBackground(getResources().getDrawable(R.drawable.corporate_header_hdpi));
    }


    private void init() {
        appPref = new AppPref(this);
        mUserData = appPref.getUserInfo();
    }

    private void setToolBarTitle(String type) {
        TextView textView = (TextView) toolbar.findViewById(R.id.tvToolbar);
        textView.setText(R.string.txt_ongoing_contest);

        toolbar.setTitle("");
    }


    private void getProductData() {

        if (getIntent().getExtras() != null) {

            productObj = getIntent().getExtras().getParcelable(AppConstant.PRODUCT_OBJ_KEY);
            if (productObj != null)
                setToolBarTitle(productObj.getType());
            userContestId = getIntent().getExtras().getString("userContestId");
            AppLogger.debug(ContestDetailsActivity.class.getSimpleName(),"userContestId"+userContestId);
        }
    }


    private void moveToDetails() {
        Bundle bundle = new Bundle();

        bundle.putParcelable(AppConstant.PRODUCT_OBJ_KEY, productObj);

        Intent intent = new Intent(this, ContestAllItemListActivity.class);

        intent.putExtras(bundle);
        intent.putExtra("userContestId", userContestId);
        AppLogger.debug(ContestDetailsActivity.class.getSimpleName(),"userContestId"+userContestId);
        startActivityForResult(intent, AppConstant.INTENT_PRODUCT_DETAILS);

        overridePendingTransition(R.anim.activity_animation_enter,
                R.anim.activity_animation_exit);
    }

}
