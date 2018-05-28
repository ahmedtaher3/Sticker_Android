package com.sticker_android.controller.activities.fan.home.fandownloadmage;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
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
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.model.User;
import com.sticker_android.model.corporateproduct.Product;
import com.sticker_android.model.fandownload.Download;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.AppLogger;
import com.sticker_android.utils.DownloadImage;
import com.sticker_android.utils.FileUtil;
import com.sticker_android.utils.ProgressDialogHandler;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.sharedpref.AppPref;

import java.io.File;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.SharingHelper;
import io.branch.referral.util.LinkProperties;
import io.branch.referral.util.ShareSheetStyle;
import retrofit2.Call;

public class FanDownloadedImageActivity extends AppBaseActivity implements View.OnClickListener {

    private ImageView imvProductImage;
    private Toolbar toolbar;
    private ProgressBar pgrImage;
    private AppPref appPref;
    private User user;
    private Download downloadImageObj;
    private Button btnDelete, btnSave;

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
            if (downloadImageObj != null)
                setImage(downloadImageObj.imageUrl);
        }

       // setImageHeight();
    }

    private void init() {
        appPref = new AppPref(this);
        user = appPref.getUserInfo();
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


    @Override
    protected void setViewListeners() {
        btnDelete.setOnClickListener(this);
        btnSave.setOnClickListener(this);
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

    final    ProgressDialogHandler progressDialogHandler=new ProgressDialogHandler(this);
        progressDialogHandler.show();
        if (downloadImageObj != null) {
            Call<ApiResponse> apiResponseCall = RestClient.getService().deleteCustomizeImage(user.getLanguageId(), user.getAuthrizedKey(), user.getId(), "" + downloadImageObj.user_my_id);

            apiResponseCall.enqueue(new ApiCall(getActivity()) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    progressDialogHandler.hide();
                    if (apiResponse.status) {
                        Utils.showToast(getActivity(), getResources().getString(R.string.deleted_successfully));
                        setResult(RESULT_OK);
                        onBackPressed();

                    }
                }

                @Override
                public void onFail(Call<ApiResponse> call, Throwable t) {
                    progressDialogHandler.hide();
                }
            });

        }
    }

    @Override
    protected void setViewReferences() {
        imvProductImage = (ImageView) findViewById(R.id.image);
        pgrImage = (ProgressBar) findViewById(R.id.pgrImage);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnSave = (Button) findViewById(R.id.btnSave);
    }

    @Override
    protected boolean isValidData() {
        return false;
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.share, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                shareItem();
                //  showDeleteDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void shareItem() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Image url " + downloadImageObj.imageUrl;
        String shareSub = "Share data";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.txt_share) + " :" + user.getEmail()));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnDelete:
                showDeleteDialog();
                break;
            case R.id.btnSave:
                final ProgressDialogHandler progressDialogHandler=new ProgressDialogHandler(this);
                progressDialogHandler.show();
                if (Utils.isConnectedToInternet(getActivity())) {
                    new DownloadImage(new DownloadImage.ISaveImageToLocal() {
                        @Override
                        public void imageResult(Bitmap result) {
                            progressDialogHandler.hide();
                            Uri tempUri = Utils.getImageUri(getApplicationContext(), result);

                            // CALL THIS METHOD TO GET THE ACTUAL PATH
                            File finalFile = new File(Utils.getRealPathFromURI(getApplicationContext(), tempUri));
                            if (finalFile != null) {
                                FileUtil.albumUpdate(getApplicationContext(), finalFile.getAbsolutePath());
                                MediaScannerConnection.scanFile(getActivity(), new String[] { finalFile.getPath() }, new String[] { "image/jpeg" }, null);
                            }
                                Utils.showToast(getActivity(), getString(R.string.txt_image_saved_successfully));
                            AppLogger.debug(FanDownloadedImageActivity.class.getSimpleName(), "called here" + finalFile);
                        }
                    }).execute(downloadImageObj.imageUrl);
                }
                break;
        }
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
                .setSharingTitle("Share With");

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

                      //  Log.e(TAG, "Shared link => " + sharedLink);
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




}
