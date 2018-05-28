package com.sticker_android.controller.activities.fan.home.details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.sticker_android.R;
import com.sticker_android.constant.AppConstant;
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.controller.activities.fan.home.fandownloadmage.FanDownloadedImageActivity;
import com.sticker_android.model.User;
import com.sticker_android.model.corporateproduct.Product;
import com.sticker_android.model.enums.DesignType;
import com.sticker_android.model.fandownload.Download;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.helper.TimeUtility;
import com.sticker_android.utils.sharedpref.AppPref;

import org.json.JSONObject;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.SharingHelper;
import io.branch.referral.util.LinkProperties;
import io.branch.referral.util.ShareSheetStyle;
import retrofit2.Call;

public class FanDetailsActivity extends AppBaseActivity {

    private final String TAG = FanDetailsActivity.class.getSimpleName();
    private Context mContext = this;
    private Toolbar toolbar;
    private AppPref appPref;
    private User userdata;

    public ImageView imvProductImage;
    public TextView tvProductTitle, tvStatus, tvTime, tvDownloads;
    public CheckBox checkboxLike, checkboxShare;
    public TextView tvName;
    public CardView cardItem;
    public ProgressBar pbLoader;

    private Product mProduct;
    private TimeUtility timeUtility = new TimeUtility();
    private TextView tvDescription;
    private TextView tvFeatured;

    private boolean isSharedEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fan_details);
        init();
        getuserInfo();
        setViewReferences();
        setViewListeners();
        getIntentValues();
        setToolbarItem();

        setImageHeight();

        if (mProduct != null) {
            setProductDetail();
        }
        measureImageWidthHeight();
    }

    private void setToolbarItem(){

        setToolbar();
        changeStatusBarColor(getResources().getColor(R.color.colorstatusBarFan));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        this.setIntent(intent);
    }

    private void getIntentValues() {
        Intent intent = getIntent();
        if (intent != null) {
            mProduct = intent.getParcelableExtra(AppConstant.PRODUCT);
            viewCountApi();
        }
    }

    private void measureImageWidthHeight() {

        ViewTreeObserver vto = imvProductImage.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                imvProductImage.getViewTreeObserver().removeOnPreDrawListener(this);
                int finalWidth = imvProductImage.getMeasuredWidth();
                int height = finalWidth * 3 / 5;
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imvProductImage.getLayoutParams();

                //      LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imvOfAds.getLayoutParams();
                layoutParams.height = height;
                imvProductImage.setLayoutParams(layoutParams);
                return true;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Branch branch = Branch.getInstance();

        // Branch init
        branch.initSession(new Branch.BranchReferralInitListener() {
            @Override
            public void onInitFinished(JSONObject referringParams, BranchError error) {
                if (error == null) {
                    // params are the deep linked params associated with the link that the user clicked -> was re-directed to this app
                    // params will be empty if no data found
                    // ... insert custom logic here ...
                    Log.e("BRANCH SDK", referringParams.toString());

                    if(referringParams.has("property1")){
                        String property1 = referringParams.optString("property1", "");
                        if(property1 != null){
                            Gson gson = new Gson();
                            mProduct = gson.fromJson(property1, Product.class);
                            if(mProduct != null){
                                isSharedEnabled = false;
                                setToolbarItem();
                                setProductDetail();
                            }
                        }
                    }
                    else if(referringParams.has("property2")){
                        String property2 = referringParams.optString("property2", "");
                        if(property2 != null){
                            Gson gson = new Gson();
                            Download download = gson.fromJson(property2, Download.class);
                            if(download != null){
                                Intent intent = new Intent(mContext, FanDownloadedImageActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putParcelable("image", download);
                                bundle.putBoolean("no_delete_btn", true);
                                intent.putExtra("image", download.imageUrl);
                                intent.putExtras(bundle);
                                startActivityForResult(intent, 444);
                                getActivity().overridePendingTransition(R.anim.activity_animation_enter,
                                        R.anim.activity_animation_exit);
                                finish();
                            }
                        }
                    }
                } else {
                    Log.i("BRANCH SDK", error.getMessage());
                }
            }
        }, this.getIntent().getData(), this);
    }

    private void viewCountApi() {

        if(mProduct != null){
            Call<ApiResponse> apiResponseCall = RestClient.getService().apiSaveProductLike(userdata.getLanguageId(), userdata.getAuthrizedKey(), userdata.getId()
                    , "", mProduct.getProductid(), "1", "statics", "view_count");
            apiResponseCall.enqueue(new ApiCall(this) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    if (apiResponse.status) {
                    }
                }

                @Override
                public void onFail(Call<ApiResponse> call, Throwable t) {

                }
            });
        }
    }

    /**
     * will set the product detail
     */
    private void setProductDetail() {

        Gson gson = new Gson();
        Log.e(TAG, "Product => " + gson.toJson(mProduct));
        if (mProduct != null) {
            setToolbar();
            if (mProduct.getType().equalsIgnoreCase(DesignType.stickers.getType().toLowerCase()) || mProduct.getType().equalsIgnoreCase(DesignType.gif.getType()) || mProduct.getType().equalsIgnoreCase(DesignType.emoji.getType())) {
                tvDescription.setVisibility(View.GONE);
                tvDownloads.setVisibility(View.VISIBLE);
            } else {
                tvDownloads.setVisibility(View.GONE);
                tvDescription.setVisibility(View.VISIBLE);
                tvDescription.setText(mProduct.getDescription());

            }
            if (mProduct.isLike > 0) {
                checkboxLike.setChecked(true);
                checkboxLike.setButtonDrawable(getResources().getDrawable(R.drawable.ic_hand));
            } else {
                checkboxLike.setChecked(false);
                checkboxLike.setButtonDrawable(getResources().getDrawable(R.drawable.ic_like));

            }
            if (mProduct.isFeatured > 0) {
                tvFeatured.setVisibility(View.VISIBLE);
            } else
                tvFeatured.setVisibility(View.GONE);
            checkboxLike.setText(Utils.format(mProduct.statics.likeCount));
            tvDownloads.setText(Utils.format(mProduct.statics.downloadCount));
            checkboxShare.setText(Utils.format(mProduct.statics.shareCount));
            tvName.setText(mProduct.userName);
            tvProductTitle.setText(Utils.capitlizeText(mProduct.getProductname()));
            tvTime.setText(timeUtility.covertTimeToText(Utils.convertToCurrentTimeZone(mProduct.getCreatedTime()), mContext).replaceAll("about", "").trim());
            if (mProduct.getImagePath() != null && !mProduct.getImagePath().isEmpty()) {
                pbLoader.setVisibility(View.VISIBLE);
                Glide.with(mContext)
                        .load(mProduct.getImagePath())
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                pbLoader.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                pbLoader.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(imvProductImage);
            } else {
                imvProductImage.setBackgroundColor(ContextCompat.getColor(mContext, R.color.image_background_color));
            }
        }

    }

    private void setImageHeight() {
        ViewTreeObserver vto = imvProductImage.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                imvProductImage.getViewTreeObserver().removeOnPreDrawListener(this);
                int finalWidth = imvProductImage.getMeasuredWidth();
                int height = finalWidth * 3 / 5;
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imvProductImage.getLayoutParams();
                layoutParams.height = height;
                imvProductImage.setLayoutParams(layoutParams);
                return true;
            }
        });
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
        if (mProduct != null) {
            if (mProduct.getType().equalsIgnoreCase(DesignType.stickers.getType())) {
                textView.setText(getString(R.string.stickers) + " " + getString(R.string.detail));
            } else if (mProduct.getType().equalsIgnoreCase(DesignType.gif.getType())) {
                textView.setText(getString(R.string.gif) + " " + getString(R.string.detail));
            } else if (mProduct.getType().equalsIgnoreCase(DesignType.emoji.getType())) {
                textView.setText(getString(R.string.emoji) + " " + getString(R.string.detail));
            } else if (mProduct.getType().equalsIgnoreCase(DesignType.products.getType())) {
                textView.setText(getString(R.string.txt_products_frag) + " " + getString(R.string.detail));
            } else if (mProduct.getType().equalsIgnoreCase(DesignType.ads.getType())) {
                textView.setText(getString(R.string.txt_ads_frag) + " " + getString(R.string.detail));
            }
        }
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
    }

    @Override
    protected void setViewListeners() {
        likeListener();
        downloadListener();
    }

    private void shareListener() {

    }

    @Override
    protected void setViewReferences() {
        imvProductImage = (ImageView) findViewById(R.id.imvOfAds);
        tvProductTitle = (TextView) findViewById(R.id.tv_add_product_title);
        tvStatus = (TextView) findViewById(R.id.tv_add_product_status);
        checkboxLike = (CheckBox) findViewById(R.id.checkboxLike);
        checkboxShare = (CheckBox) findViewById(R.id.checkboxShare);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvTime = (TextView) findViewById(R.id.tvTime);
        tvDownloads = (TextView) findViewById(R.id.tvDownloads);
        cardItem = (CardView) findViewById(R.id.card_view);
        pbLoader = (ProgressBar) findViewById(R.id.pgrImage);
        tvDescription = (TextView) findViewById(R.id.tv_add_product_item_description);
        tvFeatured = (TextView) findViewById(R.id.tvFeatured);
    }

    @Override
    protected boolean isValidData() {
        return false;
    }

    private void likeListener() {

        checkboxLike.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean checked = isChecked;
                if (buttonView.isPressed())
                    if (mProduct.isLike > 0) {
                        likeApi(0);
                        checkboxLike.setEnabled(false);
                    } else {
                        likeApi(1);
                        viewCountApi();
                        checkboxLike.setEnabled(false);
                    }
              /*  if (product.isLike==1) {
                    likeApi(product, 0, position);
                } else if(product.isLike==0){
                    likeApi(product, 1, position);

                }*/

            }
        });
        checkboxShare.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isSharedEnabled){
                    share();
                }
            }
        });
    }

    private void share() {

        checkboxShare.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
              /*  Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Image url " + mProduct.getImagePath();
                String shareSub = "Share data";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, getString(R.string.txt_share) + " :" + userdata.getEmail()));
*/
                if(mProduct!=null) {
                    createDeepLink(mProduct);
                    shareApi(1);
                }
            }
        });
    }


    private void createDeepLink(final Product product){
        Gson gson = new Gson();

        BranchUniversalObject branchUniversalObject = new BranchUniversalObject()
                .setCanonicalIdentifier("item/" + product.getProductid())
                .setTitle(getString(R.string.app_name))
                .setContentDescription(product.getProductname())
                .setContentImageUrl(product.getImagePath())
                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .addContentMetadata("property1", gson.toJson(product));

        LinkProperties linkProperties = new LinkProperties()
                .setChannel("facebook")
                .setFeature("sharing");
                /*.addControlParameter("$desktop_url", "http://www.google.com")
                .addControlParameter("$ios_url", "http://example.com/ios");*/

        ShareSheetStyle shareSheetStyle = new ShareSheetStyle(this, "Check this out!", "")
                .setCopyUrlStyle(getResources().getDrawable(android.R.drawable.ic_menu_send), "Copy", "Added to clipboard")
                .setMoreOptionStyle(getResources().getDrawable(android.R.drawable.ic_menu_search), "Show more")
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.FACEBOOK)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.TWITTER)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.EMAIL)
                .setAsFullWidthStyle(true)
                .setSharingTitle(getResources().getString(R.string.txt_share));

        branchUniversalObject.showShareSheet(this,
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

                        Log.e(TAG, "Shared link => " + sharedLink);
                    }
                    @Override
                    public void onChannelSelected(String channelName) {
                    }
                });

        branchUniversalObject.generateShortUrl(this, linkProperties, new Branch.BranchLinkCreateListener() {
            @Override
            public void onLinkCreate(String url, BranchError error) {
                if (error == null) {
                    /*Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareBody = "Image url " + product.getImagePath();
                    String shareSub = "Share data";
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody + "\n" + url);
                    context.startActivity(Intent.createChooser(sharingIntent, context.getString(R.string.txt_share) + " :" + mUserdata.getEmail()));*/
                }
            }
        });


    }



    private void likeApi(final int i) {

        if(!isSharedEnabled){
            return;
        }

        Call<ApiResponse> apiResponseCall = RestClient.getService().apiSaveProductLike(userdata.getLanguageId(), userdata.getAuthrizedKey(), userdata.getId()
                , "", mProduct.getProductid(), "" + i, "statics", "like_count");
        apiResponseCall.enqueue(new ApiCall(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.status) {
                    checkboxLike.setEnabled(true);
                    mProduct.isLike = i;
                    mProduct.statics.likeCount = apiResponse.paylpad.statics.likeCount;
                    checkboxLike.setText("" + mProduct.statics.likeCount);

                    if (mProduct.isLike > 0) {
                        checkboxLike.setChecked(true);
                        checkboxLike.setButtonDrawable(getResources().getDrawable(R.drawable.ic_hand));
                    } else {
                        checkboxLike.setChecked(false);
                        checkboxLike.setButtonDrawable(getResources().getDrawable(R.drawable.ic_like));

                    }
                }
            }

            @Override
            public void onFail(Call<ApiResponse> call, Throwable t) {
                checkboxLike.setEnabled(true);
            }
        });


    }

    private void downloadListener() {

        tvDownloads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isSharedEnabled){
                    downloadApi(1);
                }
            }
        });

    }


    private void downloadApi(int i) {

        Call<ApiResponse> apiResponseCall = RestClient.getService().apiSaveProductLike(userdata.getLanguageId(), userdata.getAuthrizedKey(), userdata.getId()
                , "", mProduct.getProductid(), "" + i, "statics", "download_count");
        apiResponseCall.enqueue(new ApiCall(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.status) {
                    mProduct.statics.downloadCount = apiResponse.paylpad.statics.downloadCount;
                    //    mProduct.statics.downloadCount++;
                    tvDownloads.setText("" + mProduct.statics.downloadCount);
                }
            }

            @Override
            public void onFail(Call<ApiResponse> call, Throwable t) {

            }
        });


    }

    private void shareApi(int i) {

        Call<ApiResponse> apiResponseCall = RestClient.getService().apiSaveProductLike(userdata.getLanguageId(), userdata.getAuthrizedKey(), userdata.getId()
                , "", mProduct.getProductid(), "" + i, "statics", "share_count");
        apiResponseCall.enqueue(new ApiCall(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.status) {
                    mProduct.statics.shareCount = apiResponse.paylpad.statics.shareCount;
                    //    mProduct.statics.downloadCount++;
                    checkboxShare.setText("" + mProduct.statics.shareCount);
                }
            }

            @Override
            public void onFail(Call<ApiResponse> call, Throwable t) {

            }
        });


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_OK);

    }
}
