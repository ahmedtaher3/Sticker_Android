package com.sticker_android.controller.activities.fan.home.fandownloadmage;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.sticker_android.utils.helper.PermissionManager;
import com.sticker_android.utils.sharedpref.AppPref;

import java.io.File;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.SharingHelper;
import io.branch.referral.util.LinkProperties;
import io.branch.referral.util.ShareSheetStyle;
import retrofit2.Call;

import static com.sticker_android.utils.helper.PermissionManager.Constant.WRITE_STORAGE_ACCESS_RQ;

public class FanDownloadedImageActivity extends AppBaseActivity implements View.OnClickListener {

    private ImageView imvProductImage;
    private Toolbar toolbar;
    private ProgressBar pgrImage;
    private AppPref appPref;
    private User user;
    private Download downloadImageObj;
    private Button btnDelete, btnSave;
    private RelativeLayout rlDelete;
    private Context mContext = this;

    private final String TAG = FanDownloadedImageActivity.class.getSimpleName();

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
            boolean noDeleteButton = getIntent().getExtras().getBoolean("no_delete_btn");

            if(noDeleteButton){
                rlDelete.setVisibility(View.GONE);

                TextView textView = (TextView) toolbar.findViewById(R.id.tvToolbar);
                textView.setText("");
                toolbar.setTitle("");
            }

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

        final ProgressDialogHandler progressDialogHandler=new ProgressDialogHandler(this);
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
        rlDelete = (RelativeLayout) findViewById(R.id.rlDelete);
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
                /*shareItem();*/
                //  showDeleteDialog();
                /*createDeepLink(downloadImageObj);*/
                shareItem();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void shareItem() {

        final ProgressDialogHandler progressDialogHandler = new ProgressDialogHandler(this);
        progressDialogHandler.show();
        new AsyncTask<Void, Void, File>(){

            @Override
            protected File doInBackground(Void... params) {
                try{
                    Bitmap bitmap = Utils.getBitmapFromView(imvProductImage);
                    if(bitmap != null){
                        File file = Utils.getFileFromBitmap(FanDownloadedImageActivity.this, bitmap);
                        return file;
                    }
                    else{
                        return null;
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(File file) {
                super.onPostExecute(file);
                if(progressDialogHandler != null){
                    progressDialogHandler.hide();
                }

                if(file != null){
                    Utils.shareImageOnSocialMedia(FanDownloadedImageActivity.this, file.getAbsolutePath(), user.getEmail());
                }
            }
        }.execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnDelete:
                showDeleteDialog();
                break;
            case R.id.btnSave:
                if (Utils.isConnectedToInternet(getActivity())) {
                    if(PermissionManager.checkWriteStoragePermission(this, PermissionManager.Constant.WRITE_STORAGE_ACCESS_RQ)){
                        downloadImageIntoLocalStorage();
                    }
                }
                else{
                    Utils.showToastMessage(getActivity(), getString(R.string.pls_check_ur_internet_connection));
                }
                break;
        }
    }

    private void downloadImageIntoLocalStorage(){

        final ProgressDialogHandler progressDialogHandler=new ProgressDialogHandler(this);
        progressDialogHandler.show();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case WRITE_STORAGE_ACCESS_RQ:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    downloadImageIntoLocalStorage();

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    boolean isDenied = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                    if (!isDenied) {
                        //If the user turned down the permission request in the past and chose the Don't ask again option in the permission request system dialog

                        PermissionManager.showCustomPermissionDialog(this, getString(R.string.external_storage_permission_msg), new PermissionManager.CustomPermissionDialogCallback() {
                            @Override
                            public void onCancelClick() {

                            }

                            @Override
                            public void onOpenSettingClick() {

                            }
                        });
                    }
                }
                break;
        }
    }

    private void createDeepLink(final Download downloadImageObj){

        Gson gson = new Gson();

        BranchUniversalObject branchUniversalObject = new BranchUniversalObject()
                .setCanonicalIdentifier("item/" + downloadImageObj.user_my_id)
                .setTitle(mContext.getResources().getString(R.string.app_name))
                .setContentDescription("")
                .setContentImageUrl(downloadImageObj.imageUrl)
                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .addContentMetadata("property2", gson.toJson(downloadImageObj));

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
                        Log.e(TAG, "Shared link => " + sharedLink);
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
