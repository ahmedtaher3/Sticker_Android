package com.sticker_android.controller.activities.fan.home.details;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
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
import com.sticker_android.controller.activities.designer.addnew.AddNewDesignActivity;
import com.sticker_android.controller.activities.designer.addnew.DesignDetailActivity;
import com.sticker_android.controller.activities.designer.home.DesignerHomeActivity;
import com.sticker_android.controller.adaptors.FanListAdaptor;
import com.sticker_android.model.User;
import com.sticker_android.model.corporateproduct.Product;
import com.sticker_android.model.enums.DesignType;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.AppLogger;
import com.sticker_android.utils.ProgressDialogHandler;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.helper.TimeUtility;
import com.sticker_android.utils.sharedpref.AppPref;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fan_details);
        init();
        getuserInfo();
        setViewReferences();
        setViewListeners();
        getIntentValues();
        setToolbar();
        changeStatusBarColor(getResources().getColor(R.color.colorstatusBarFan));

        toolbar.setNavigationIcon(R.drawable.back_arrow_small);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        setImageHeight();

        if (mProduct != null) {
            setProductDetail();
        }
    }

    private void getIntentValues() {
        Intent intent = getIntent();
        if (intent != null) {
            mProduct = intent.getParcelableExtra(AppConstant.PRODUCT);
        }
    }

    /**
     * will set the product detail
     */
    private void setProductDetail() {

        if (mProduct != null) {
            if (mProduct.getType().equals(mProduct.getType().equalsIgnoreCase(DesignType.stickers.getType().toLowerCase()))
                    || mProduct.getType().equalsIgnoreCase(DesignType.gif.getType()) || mProduct.getType().equalsIgnoreCase(DesignType.emoji.getType())) {
                tvDownloads.setVisibility(View.VISIBLE);
                tvDescription.setVisibility(View.GONE);

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

            checkboxLike.setText(Utils.format(mProduct.statics.likeCount));
            checkboxShare.setText(Utils.format(mProduct.statics.shareCount));
            tvDownloads.setText(Utils.format(mProduct.statics.downloadCount));
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
                textView.setText("Sticker" + " " + getString(R.string.detail));
            } else if (mProduct.getType().equalsIgnoreCase(DesignType.gif.getType())) {
                textView.setText("GIF" + " " + getString(R.string.detail));
            } else if (mProduct.getType().equalsIgnoreCase(DesignType.emoji.getType())) {
                textView.setText("Emoji" + " " + getString(R.string.detail));
            } else if (mProduct.getType().equalsIgnoreCase(DesignType.products.getType())) {
                textView.setText("Products" + " " + getString(R.string.detail));
            } else if (mProduct.getType().equalsIgnoreCase(DesignType.ads.getType())) {
                textView.setText("Ads" + " " + getString(R.string.detail));
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
                    } else {
                        likeApi(1);
                    }
              /*  if (product.isLike==1) {
                    likeApi(product, 0, position);
                } else if(product.isLike==0){
                    likeApi(product, 1, position);

                }*/

            }
        });
    }

    private void likeApi(final int i) {

        Call<ApiResponse> apiResponseCall = RestClient.getService().apiSaveProductLike(userdata.getLanguageId(), userdata.getAuthrizedKey(), userdata.getId()
                , "", mProduct.getProductid(), "" + i, "statics", "like_count");
        apiResponseCall.enqueue(new ApiCall(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.status) {
                    mProduct.isLike = i;
                    mProduct.statics.likeCount = apiResponse.paylpad.statics.likeCount;
                    checkboxLike.setText(mProduct.statics.likeCount);
                }
            }

            @Override
            public void onFail(Call<ApiResponse> call, Throwable t) {

            }
        });


    }

    private void downloadListener() {

        tvDownloads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadApi(1);
            }
        });

    }


    private void downloadApi(int i) {

        Call<ApiResponse> apiResponseCall = RestClient.getService().apiSaveProductLike(userdata.getLanguageId(), userdata.getAuthrizedKey(), userdata.getId()
                , "", mProduct.getProductid(), "" + i, "", "download_count");
        apiResponseCall.enqueue(new ApiCall(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.status) {
                    mProduct.statics.downloadCount++;
                    checkboxLike.setText(mProduct.statics.downloadCount);
                }
            }

            @Override
            public void onFail(Call<ApiResponse> call, Throwable t) {

            }
        });


    }

}
