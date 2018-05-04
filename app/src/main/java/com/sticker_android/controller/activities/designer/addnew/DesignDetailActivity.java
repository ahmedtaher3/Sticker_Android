package com.sticker_android.controller.activities.designer.addnew;

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
import com.sticker_android.controller.activities.designer.home.DesignerHomeActivity;
import com.sticker_android.model.User;
import com.sticker_android.model.corporateproduct.Product;
import com.sticker_android.model.enums.DesignType;
import com.sticker_android.model.enums.ProductStatus;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.AppLogger;
import com.sticker_android.utils.ProgressDialogHandler;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.helper.TimeUtility;
import com.sticker_android.utils.sharedpref.AppPref;

import retrofit2.Call;

public class DesignDetailActivity extends AppBaseActivity implements View.OnClickListener {

    private final String TAG = DesignDetailActivity.class.getSimpleName();
    private Context mContext = this;
    private Toolbar toolbar;
    private AppPref appPref;
    private User userdata;

    public ImageView imvProductImage;
    public TextView tvProductTitle, tvStatus, tvTime, tvDownloads;
    public CheckBox checkboxLike, checkboxShare;
    public ImageButton imvBtnEditRemove;
    public CardView cardItem;
    public ProgressBar pbLoader;

    private Product mProduct;
    private TimeUtility timeUtility = new TimeUtility();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_design_detail);
        init();
        getuserInfo();
        setViewReferences();
        setViewListeners();

        getIntentValues();

        setToolbar();
        toolbar.setNavigationIcon(R.drawable.back_arrow_small);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        changeStatusBarColor(getResources().getColor(R.color.colorstatusBarDesigner));

        setImageHeight();

        if(mProduct != null){
            setProductDetail();
        }
    }

    private void getIntentValues(){
        Intent intent = getIntent();
        if(intent != null){
            mProduct = intent.getParcelableExtra(AppConstant.PRODUCT);
        }
    }

    /**
     * will set the product detail
     */
    private void setProductDetail(){

        if(mProduct != null){

            checkboxLike.setText(Utils.format(mProduct.statics.likeCount));
          //  checkboxShare.setText(Utils.format(0));
            tvDownloads.setText(Utils.format(mProduct.statics.downloadCount));
            imvBtnEditRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopup(v, -1, "pending", mProduct);
                }
            });
            tvProductTitle.setText(Utils.capitlizeText(mProduct.getProductname()));
            tvTime.setText(timeUtility.covertTimeToText(Utils.convertToCurrentTimeZone(mProduct.getCreatedTime()), mContext).replaceAll("about", "").trim());


            int status = mProduct.productStatus;
            AppLogger.error(TAG, "Status => " + status);
            if (status == ProductStatus.REJECTED.getStatus()) {
                tvStatus.setTextColor(Color.RED);
                tvStatus.setText(R.string.rejected);
            }
            else if(status == ProductStatus.EXPIRED.getStatus()){
                tvStatus.setTextColor(Color.RED);
                tvStatus.setText(R.string.expired);
            }else if(status == ProductStatus.APPROVED.getStatus()){
                tvStatus.setTextColor(ContextCompat.getColor(this, R.color.colorHomeGreen));
                tvStatus.setText(R.string.approved);
            }
            else{
                tvStatus.setTextColor(Color.parseColor("#1D93FB"));
                tvStatus.setText(R.string.pending);
            }

            if(mProduct.statics.likeCount>0){
                checkboxLike.setChecked(true);
                checkboxLike.setButtonDrawable(getResources().getDrawable(R.drawable.ic_hand));
            } else {
                checkboxLike.setChecked(false);
                checkboxLike.setButtonDrawable(getResources().getDrawable(R.drawable.ic_like));

            }
            /*String status = "pending";
            if (status.equalsIgnoreCase("rejected")) {
                tvStatus.setTextColor(Color.RED);
                tvStatus.setText("Rejected");
            } else if(status.equalsIgnoreCase("approved")){
                tvStatus.setTextColor(ContextCompat.getColor(mContext, R.color.colorHomeGreen));
                tvStatus.setText("Approved");
            }
            else{
                tvStatus.setTextColor(Color.parseColor("#1D93FB"));
                tvStatus.setText("Pending");
            }*/

            if(mProduct.getImagePath() != null && !mProduct.getImagePath().isEmpty()){
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
            }
            else{
                imvProductImage.setBackgroundColor(ContextCompat.getColor(mContext, R.color.image_background_color));
            }
        }
    }

    private void setImageHeight(){
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
        if(mProduct != null){
            if(mProduct.getType().equalsIgnoreCase(DesignType.stickers.getType())){
                textView.setText("Sticker" + " " + getString(R.string.detail));
            }
            else if(mProduct.getType().equalsIgnoreCase(DesignType.gif.getType())){
                textView.setText("GIF" + " " + getString(R.string.detail));
            }
            else if(mProduct.getType().equalsIgnoreCase(DesignType.emoji.getType())){
                textView.setText("Emoji" + " " + getString(R.string.detail));
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
        toolbar.setBackground(getResources().getDrawable(R.drawable.designer_header_hdpi));
    }

    @Override
    protected void setViewListeners() {

    }

    @Override
    protected void setViewReferences() {
        imvProductImage = (ImageView) findViewById(R.id.imvProductImage);
        tvProductTitle = (TextView) findViewById(R.id.tv_add_product_title);
        tvStatus = (TextView) findViewById(R.id.tv_add_product_status);
        checkboxLike = (CheckBox) findViewById(R.id.checkboxLike);
        checkboxShare = (CheckBox) findViewById(R.id.checkboxShare);
        imvBtnEditRemove = (ImageButton) findViewById(R.id.imvBtnEditRemove);
        tvTime = (TextView) findViewById(R.id.tvTime);
        tvDownloads = (TextView) findViewById(R.id.tvDownloads);
        cardItem = (CardView) findViewById(R.id.card_view);
        pbLoader = (ProgressBar) findViewById(R.id.pgrImage);
    }

    @Override
    protected boolean isValidData() {
        return false;
    }

    @Override
    public void onClick(View v) {    }

    /**
     * Method is used to show the popup with edit and delete option     *
     * @param v view on which click is perfomed
     * @param position position of item
     * @param product
     */
    public void showPopup(View v, final int position, String status, final Product product) {
        final int editId = 1;
        final int removeId = 2;
        final int reSubmitId = 3;
        PopupMenu popup = new PopupMenu(mContext, v);
     //   popup.getMenu().add(1, editId, editId, R.string.edit);
        if(product.productStatus==3){
            popup.getMenu().add(1, reSubmitId, reSubmitId, R.string.txt_resubmit);
            popup.getMenu().add(1, removeId, removeId, R.string.remove);
        }
        else{
            popup.getMenu().add(1, editId, editId, R.string.edit);
            popup.getMenu().add(1, removeId, removeId, R.string.remove);
        }
        /* if(product.productStatus==3){
            popup.getMenu().add(1, removeId, removeId, R.string.remove);
         //   popup.getMenu().add(1, reSubmitId, reSubmitId, R.string.resubmit_with_justification);
        }
        else{
            popup.getMenu().add(1, removeId, removeId, R.string.remove);
        }*/
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case editId:
                        AppLogger.error(TAG, "Edit item");
                        Intent intent = new Intent(mContext, AddNewDesignActivity.class);
                        intent.putExtra(AppConstant.DATA_REFRESH_NEEDED, true);
                        intent.putExtra(AppConstant.PRODUCT, product);
                        startActivity(intent);
                        break;
                    case removeId:
                        AppLogger.error(TAG, "Remove item");
                        if(Utils.isConnectedToInternet(mContext)){
                            Utils.deleteDialog(mContext.getString(R.string.txt_are_you_sure), (Activity) mContext, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    removeProductApi(product);
                                }
                            });
                        }
                        else{
                            Utils.showToastMessage(mContext, mContext.getString(R.string.pls_check_ur_internet_connection));
                        }
                        break;
                    case reSubmitId:
                        AppLogger.error(TAG, "Re submit item");
                        Intent resubmitIntent = new Intent(mContext, AddNewDesignActivity.class);
                        resubmitIntent.putExtra(AppConstant.DATA_REFRESH_NEEDED, true);
                        resubmitIntent.putExtra(AppConstant.PRODUCT, product);
                        startActivity(resubmitIntent);
                        break;
                }
                return false;
            }
        });
    }

    private void removeProductApi(final Product product) {
        AppPref appPref = new AppPref(mContext);
        User mUserdata = appPref.getUserInfo();
        final ProgressDialogHandler progressDialogHandler = new ProgressDialogHandler(this);
        progressDialogHandler.show();

        Call<ApiResponse> apiResponseCall = RestClient.getService().apiDeleteProduct(mUserdata.getLanguageId(), mUserdata.getAuthrizedKey(), mUserdata.getId(),
                String.valueOf(product.getProductid()));

        apiResponseCall.enqueue(new ApiCall((Activity) mContext) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                progressDialogHandler.hide();
                if (apiResponse.status) {
                    Utils.showToast(mContext, mContext.getString(R.string.deleted_successfully));
                    Intent intent = new Intent(mContext, DesignerHomeActivity.class);
                    intent.putExtra(AppConstant.DATA_REFRESH_NEEDED, true);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }

            @Override
            public void onFail(Call<ApiResponse> call, Throwable t) {
                progressDialogHandler.hide();
            }
        });
    }
}
