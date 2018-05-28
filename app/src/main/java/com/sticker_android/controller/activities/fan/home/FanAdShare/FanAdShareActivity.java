package com.sticker_android.controller.activities.fan.home.FanAdShare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
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
import com.sticker_android.controller.activities.fan.home.details.FanDetailsActivity;
import com.sticker_android.model.User;
import com.sticker_android.model.corporateproduct.Product;
import com.sticker_android.utils.AppLogger;
import com.sticker_android.utils.sharedpref.AppPref;

public class FanAdShareActivity extends AppBaseActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private AppPref appPref;
    private User userdata;
    private ImageView imvOfCustomization, imvProductImage, imvShare, imvOfAds;
    private ProgressBar pgrImage1, pgrImage;
    private TextView tvTitle;
    private TextView tvname, tvDescription;
    private Product mProduct;
    private String link = null;
    private Button btnVisit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fan_ad_share);
        init();
        getuserInfo();
        getAdsInfo();
        setViewReferences();
        setViewListeners();
        setToolbar();
        getData();
        getProductInfo();
    }

    private void getData() {

        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getString("link") != null) {
                link = getIntent().getExtras().getString("link");
                AppLogger.debug(FanAdShareActivity.class.getSimpleName(), "link" + link);
            }
        }
    }

    private void getAdsInfo() {
        mProduct = appPref.getAds();
    }

    private void getProductInfo() {
        if (mProduct != null)
            Glide.with(this)
                    .load(mProduct.getImagePath())
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            pgrImage.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            pgrImage.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(imvOfAds);

        if (link != null) {
            Glide.with(this)
                    .load(link)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            pgrImage1.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            pgrImage1.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(imvOfCustomization);
        }
    }

    /**
     * Method is used to set the toolbar
     */
    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setToolbarBackground();
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.back_arrow_small);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                // AppLogger.debug(FanDetailsActivity.class.getSimpleName(),"on Backpress called.");
            }
        });
    }

    /**
     * Method is used to set the toolbar background
     */
    private void setToolbarBackground() {
        toolbar.setBackground(getResources().getDrawable(R.drawable.fan_header_hdpi));

        TextView textView = (TextView) toolbar.findViewById(R.id.tvToolbar);
        textView.setText("Ads");
        toolbar.setTitle(" ");
    }


    private void init() {
        appPref = new AppPref(getActivity());
    }

    private void getuserInfo() {
        userdata = appPref.getUserInfo();
    }


    @Override
    protected void setViewListeners() {
        btnVisit.setOnClickListener(this);
    }

    @Override
    protected void setViewReferences() {
        imvOfCustomization = findViewById(R.id.imvOfCustomization);
        imvProductImage = findViewById(R.id.imvProductImage);
        pgrImage1 = findViewById(R.id.pgrImage1);
        imvShare = findViewById(R.id.imvShare);
        imvOfAds = findViewById(R.id.imvOfAds);
        pgrImage = findViewById(R.id.pgrImage);
        tvTitle = findViewById(R.id.tv_add_product_title);
        tvname = findViewById(R.id.tv_name);
        tvDescription = findViewById(R.id.tv_add_product_item_description);
        btnVisit = findViewById(R.id.act_select_visit_ad);
    }

    @Override
    protected boolean isValidData() {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.act_select_visit_ad:
                Intent intent = new Intent(getActivity(), FanDetailsActivity.class);
                intent.putExtra(AppConstant.PRODUCT, mProduct);
                getActivity().startActivityForResult(intent, 333);
                getActivity().overridePendingTransition(R.anim.activity_animation_enter,
                        R.anim.activity_animation_exit);

                break;
        }
    }
}
