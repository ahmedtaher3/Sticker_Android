package com.sticker_android.controller.activities.fan.home.FanAdShare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.google.gson.Gson;
import com.sticker_android.R;
import com.sticker_android.constant.AppConstant;
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.controller.activities.fan.home.details.FanDetailsActivity;
import com.sticker_android.model.User;
import com.sticker_android.model.corporateproduct.Product;
import com.sticker_android.model.fandownload.Download;
import com.sticker_android.utils.AppLogger;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.sharedpref.AppPref;

import java.io.File;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.SharingHelper;
import io.branch.referral.util.LinkProperties;
import io.branch.referral.util.ShareSheetStyle;

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
    private String localFilePath = "";
    private Button btnVisit;

    private Context mContext = this;

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
                localFilePath = getIntent().getExtras().getString("local_file_path");
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

        if (localFilePath != null) {
            Glide.with(this)
                    .load(Uri.fromFile(new File(localFilePath)))
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

        toolbar.setNavigationIcon(R.drawable.close_search);

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
        textView.setText("");
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
        imvShare.setOnClickListener(this);
    }

    @Override
    protected void setViewReferences() {
        imvOfCustomization = (ImageView) findViewById(R.id.imvOfCustomization);
        imvProductImage = (ImageView) findViewById(R.id.imvProductImage);
        pgrImage1 = (ProgressBar) findViewById(R.id.pgrImage1);
        imvShare = (ImageView) findViewById(R.id.imvShare);
        imvOfAds = (ImageView) findViewById(R.id.imvOfAds);
        pgrImage = (ProgressBar) findViewById(R.id.pgrImage);
        tvTitle = (TextView) findViewById(R.id.tv_add_product_title);
        tvname = (TextView) findViewById(R.id.tv_name);
        tvDescription = (TextView) findViewById(R.id.tv_add_product_item_description);
        btnVisit = (Button) findViewById(R.id.act_select_visit_ad);
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
            case R.id.imvShare:
                /*createDeepLink(link);*/
                Utils.shareImageOnSocialMedia(this, localFilePath, userdata.getEmail());
                break;
        }
    }

    private void createDeepLink(final String customizedImageLink){

        Gson gson = new Gson();
        Download download = new Download();
        download.imageUrl = link;
        download.userId = Long.parseLong(userdata.getId());
        download.user_my_id = Long.parseLong(userdata.getId() + 5);

        BranchUniversalObject branchUniversalObject = new BranchUniversalObject()
                .setCanonicalIdentifier("item/" + download.user_my_id)
                .setTitle(mContext.getResources().getString(R.string.app_name))
                .setContentDescription("")
                .setContentImageUrl(customizedImageLink)
                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .addContentMetadata("property2", gson.toJson(download));

        LinkProperties linkProperties = new LinkProperties()
                .setChannel("facebook")
                .setFeature("sharing");

        ShareSheetStyle shareSheetStyle = new ShareSheetStyle(mContext, "Check this out!", "")
                .setCopyUrlStyle(mContext.getResources().getDrawable(android.R.drawable.ic_menu_send), "Copy", "Added to clipboard")
                .setMoreOptionStyle(mContext.getResources().getDrawable(android.R.drawable.ic_menu_search), "Show more")
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.FACEBOOK)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.TWITTER)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.EMAIL)
                .setAsFullWidthStyle(true)
                .setSharingTitle(mContext.getResources().getString(R.string.txt_share));

        branchUniversalObject.showShareSheet((Activity) mContext,
                linkProperties,
                shareSheetStyle,
                new Branch.BranchLinkShareListener() {
                    @Override
                    public void onShareLinkDialogLaunched() {
                    }
                    @Override
                    public void onShareLinkDialogDismissed() {
                    }
                    @Override
                    public void onLinkShareResponse(String sharedLink, String sharedChannel, BranchError error) {

                    }
                    @Override
                    public void onChannelSelected(String channelName) {
                    }
                });

        branchUniversalObject.generateShortUrl(mContext, linkProperties, new Branch.BranchLinkCreateListener() {
            @Override
            public void onLinkCreate(String url, BranchError error) {
                if (error == null) {
                }
            }
        });
    }
}
