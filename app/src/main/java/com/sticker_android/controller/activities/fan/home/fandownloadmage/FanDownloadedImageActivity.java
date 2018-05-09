package com.sticker_android.controller.activities.fan.home.fandownloadmage;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.sticker_android.R;
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.model.User;
import com.sticker_android.model.fandownload.Download;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.sharedpref.AppPref;

import retrofit2.Call;

public class FanDownloadedImageActivity extends AppBaseActivity {

    private ImageView imvProductImage;
    private Toolbar toolbar;
    private ProgressBar pgrImage;
    private ImageView imvDelete;
    private AppPref appPref;
    private User user;
    private Download downloadImageObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fan_downloaded_image);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        init();
        setViewReferences();
        setViewListeners();
        setToolBarTitle();
        setSupportActionBar(toolbar);
        //backgroundChange();
        changeStatusBarColor(getResources().getColor(R.color.colorstatusBarFan));
        toolbar.setBackground(getResources().getDrawable(R.drawable.fan_header_xhdpi));
        toolbar.setNavigationIcon(R.drawable.back_arrow_small);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        // getWindow().setLayout(LinearLayoutCompat.LayoutParams.FILL_PARENT, LinearLayoutCompat.LayoutParams.FILL_PARENT);
        if (getIntent().getExtras() != null) {
            downloadImageObj = getIntent().getExtras().getParcelable("image");
            if(downloadImageObj!=null)
            setImage(downloadImageObj.imageUrl);
        }
    }

    private void init() {
        appPref = new AppPref(this);
        user = appPref.getUserInfo();
    }

    private void setImage(String imagePath) {
        pgrImage.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(imagePath)
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
                .into(imvProductImage);
    }


    private void setToolBarTitle() {
        TextView textView = (TextView) toolbar.findViewById(R.id.tvToolbar);
        textView.setText(R.string.txt_my_customization);
        toolbar.setTitle("");
    }


    private void measureImageWidthHeight() {

        ViewTreeObserver vto = imvProductImage.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                imvProductImage.getViewTreeObserver().removeOnPreDrawListener(this);
                int finalWidth = imvProductImage.getMeasuredWidth();
                int height = finalWidth * 3 / 5;
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imvProductImage.getLayoutParams();
                //    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imvProductImage.getLayoutParams();
                layoutParams.height = height;
                imvProductImage.setLayoutParams(layoutParams);
                return true;
            }
        });
    }

    @Override
    protected void setViewListeners() {
        imvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog();
            }
        });
    }

    private void showDeleteDialog() {

        Utils.deleteDialog(getString(R.string.txt_are_you_sure), getActivity(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteProductApi();
            }
        });
    }

    private void deleteProductApi() {
        if (downloadImageObj != null) {
            Call<ApiResponse> apiResponseCall = RestClient.getService().deleteCustomizeImage(user.getLanguageId(), user.getAuthrizedKey(), user.getId(), "" + downloadImageObj.user_my_id);

            apiResponseCall.enqueue(new ApiCall(getActivity()) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {

                    if (apiResponse.status) {
                        Utils.showToast(getActivity(), "Deleted successfully");
                        setResult(RESULT_OK);
                        onBackPressed();

                    }
                }

                @Override
                public void onFail(Call<ApiResponse> call, Throwable t) {

                }
            });

        }
    }

    @Override
    protected void setViewReferences() {
        imvProductImage = (ImageView) findViewById(R.id.image);
        pgrImage = (ProgressBar) findViewById(R.id.pgrImage);
        imvDelete = (ImageView) findViewById(R.id.imvDelete);

    }

    @Override
    protected boolean isValidData() {
        return false;
    }
}
