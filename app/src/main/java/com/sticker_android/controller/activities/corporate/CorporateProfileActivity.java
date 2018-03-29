
package com.sticker_android.controller.activities.corporate;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sticker_android.R;
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.controller.activities.common.signin.SigninActivity;
import com.sticker_android.controller.activities.designer.home.CorporateHomeActivity;
import com.sticker_android.model.UserData;
import com.sticker_android.model.interfaces.ImagePickerListener;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiConstant;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.ProgressDialogHandler;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.helper.PermissionManager;
import com.sticker_android.utils.sharedpref.AppPref;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

import static com.sticker_android.utils.helper.PermissionManager.Constant.READ_STORAGE_ACCESS_RQ;
import static com.sticker_android.utils.helper.PermissionManager.Constant.WRITE_STORAGE_ACCESS_RQ;

/*
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
*/

public class CorporateProfileActivity extends AppBaseActivity implements View.OnClickListener{

    private ImageView imgCompanyLogo;
    private EditText firstName,lastName;
    private RelativeLayout rlBgProfile;
    private LinearLayout llCorporate;
    private AppPref appPref;
    private EditText edtCompanyName,edtCompanyAddress,edtProfileFirstName;
    private EditText edtProfileLastName,edtProfileEmail;
    private Button btnSubmit;
    private UserData userData;
    private String mCapturedImageUrl;
    private android.app.AlertDialog mPermissionDialog;

    private final int PROFILE_CAMERA_IMAGE = 0;
    private final int PROFILE_GALLERY_IMAGE = 1;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_profile);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
       init();
        setToolBarTitle();
        setSupportActionBar(toolbar);
        setViewReferences();
        setViewListeners();
       //  backgroundChange();
        changeStatusBarColor(getResources().getColor(R.color.colorstatusBarCorporate));
        changeBtnBackground();
    setImageData();
    }

    private void setImageData() {
        imageLoader.displayImage(ApiConstant.IMAGE_URl+userData.getCompanyLogo(),imgCompanyLogo);
    }

    private void changeBtnBackground() {
        btnSubmit.setBackgroundDrawable(getResources().getDrawable(R.drawable.corporate_btn_background));
    }

    private void init() {
        appPref=new AppPref(this);
        userData=appPref.getUserInfo();
    }

    private void setToolBarTitle() {
        TextView textView= (TextView) toolbar.findViewById(R.id.tvToolbar);
        textView.setText("Company Profile");
        toolbar.setTitle("");
    }


    @Override
    protected void setViewListeners() {
        btnSubmit.setOnClickListener(this);
        imgCompanyLogo.setOnClickListener(this);
    }

    @Override
    protected void setViewReferences() {
        imgCompanyLogo = (ImageView) findViewById(R.id.imgCompanyLogo);
        rlBgProfile= (RelativeLayout) findViewById(R.id.bgProfile);
        llCorporate= (LinearLayout) findViewById(R.id.llCorporate);
        edtCompanyName= (EditText) findViewById(R.id.act_profile_edt_company_name);
        edtCompanyAddress= (EditText) findViewById(R.id.act_profile_edt_company_address);
        btnSubmit= (Button) findViewById(R.id.act_corporate_profile_btn_register);
        imgCompanyLogo= (ImageView) findViewById(R.id.imgCompanyLogo);
    }

    @Override
    protected boolean isValidData()
    {
        if(edtCompanyName.getText().toString().trim().isEmpty())
    {
        Utils.showToast(CorporateProfileActivity.this,"Please enter company name.");

        //   CommonSnackBar.show(edtCompanyName, "Company name cannot be empty", Snackbar.LENGTH_SHORT);
        this.edtCompanyName.requestFocus();
        return false;
    }else if(edtCompanyAddress.getText().toString().trim().isEmpty()){
        Utils.showToast(CorporateProfileActivity.this, "Please enter company address.");

     //   CommonSnackBar.show(edtCompanyAddress, "Company address cannot be empty", Snackbar.LENGTH_SHORT);
        this.edtCompanyAddress.requestFocus();

        return false;
    }

        return true;
    }


   /* private void backgroundChange() {

        if(userData.getUserType()!=null)
            switch (userData.getUserType()){
                case "fan":
                    rlBgProfile.setBackground(getResources().getDrawable(R.drawable.gradient_bg_fan_hdpi));
                    llCorporate.setVisibility(View.GONE);
                    break;
                case "designer":
                    rlBgProfile.setBackground(getResources().getDrawable(R.drawable.gradient_bg_des_hdpi));
                    llCorporate.setVisibility(View.GONE);
                    break;
                case "corporate":
                    rlBgProfile.setBackground(getResources().getDrawable(R.drawable.profile_hdpi));
                    llCorporate.setVisibility(View.VISIBLE);
                    break;
            }
    }*/

    @Override
    public void onClick(View v) {
        Utils.hideKeyboard(this);
        switch (v.getId()){
            case R.id.act_corporate_profile_btn_register:
                if(isValidData()){
                    updateProfileApi();
                }
                break;
            case R.id.imgCompanyLogo:
                Utils.showAlertDialogToGetPic(this, new ImagePickerListener() {
                    @Override
                    public void pickFromGallery() {
                        pickGalleryImage();
                    }

                    @Override
                    public void captureFromCamera() {
                        captureImage();
                    }
                });
                break;
        }
    }
    private void captureImage() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = Utils.getCustomImagePath(this, System.currentTimeMillis() + "");
        mCapturedImageUrl = file.getAbsolutePath();
        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(takePicture, PROFILE_CAMERA_IMAGE);
    }

    private void pickGalleryImage() {
        Intent openGallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(openGallery, PROFILE_GALLERY_IMAGE);
    }


    private void updateProfileApi() {
        if (userData.getId() != null) {
            final ProgressDialogHandler progressDialogHandler=new ProgressDialogHandler(this);
            progressDialogHandler.show();
            Call<ApiResponse> apiResponseCall = RestClient.getService().updateProfile(userData.getId(), edtCompanyName.getText().toString(),
                    "", edtCompanyAddress.getText().toString(), userData.getFirstName(), userData.getLastName(),
                    userData.getEmail(), userData.getUserType());
            apiResponseCall.enqueue(new ApiCall(getActivity()) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                  progressDialogHandler.hide();
                    if (apiResponse.status) {
                        appPref.saveUserObject(apiResponse.paylpad.getData());
                        appPref.setLoginFlag(true);
                        startNewActivity(CorporateHomeActivity.class);
                        finish();
                    } else {
                        Utils.showToast(CorporateProfileActivity.this, apiResponse.error.message);

                        //    CommonSnackBar.show(edtCompanyAddress, apiResponse.error.message, Snackbar.LENGTH_SHORT);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case PROFILE_CAMERA_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    if (mCapturedImageUrl != null) {
                       // openCropActivity(mCapturedImageUrl);
                        uploadImage();
                    }
                }
                break;

           case PROFILE_GALLERY_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = data.getData();
                    String sourceUrl = Utils.getGalleryImagePath(this, selectedImage);
                    File file = Utils.getCustomImagePath(this, "temp");
                    mCapturedImageUrl = file.getAbsolutePath();
                    mCapturedImageUrl=sourceUrl;
                   // openCropActivity(sourceUrl);
                    uploadImage();
                }
                break;
           /*
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    imageLoader.displayImage(resultUri.toString(), imgCompanyLogo);
                    mCapturedImageUrl = resultUri.getPath();
                    uploadImage();
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    error.printStackTrace();
                }*/
        }
    }

   /* private void openCropActivity(String url) {
        CropImage.activity(Uri.fromFile(new File(url)))
                .setGuidelines(CropImageView.Guidelines.OFF)
                .setFixAspectRatio(true)
                .setAspectRatio(1, 1)
                .setAutoZoomEnabled(true)
                .start(this);
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case WRITE_STORAGE_ACCESS_RQ:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    captureImage();

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    boolean isDenied = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                    if (!isDenied) {
                        //If the user turned down the permission request in the past and chose the Don't ask again option in the permission request system dialog

                        mPermissionDialog = PermissionManager.showCustomPermissionDialog(this, getString(R.string.external_storage_permission_msg), new PermissionManager.CustomPermissionDialogCallback() {
                            @Override
                            public void onCancelClick() {

                            }

                            @Override
                            public void onOpenSettingClick() {

                            }
                        });
                        //Toast.makeText(mContext, getResources().getString(R.string.storage_permission_msg), Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case READ_STORAGE_ACCESS_RQ:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    pickGalleryImage();

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    boolean isDenied = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE);

                    if (!isDenied) {
                        //If the user turned down the permission request in the past and chose the Don't ask again option in the permission request system dialog
                        mPermissionDialog = PermissionManager.showCustomPermissionDialog(this, getString(R.string.external_storage_permission_msg), new PermissionManager.CustomPermissionDialogCallback() {
                            @Override
                            public void onCancelClick() {

                            }

                            @Override
                            public void onOpenSettingClick() {

                            }
                        });
                    }
                    break;
                }
        }
    }


    public void uploadImage(){
        final ProgressDialogHandler progressDialogHandler=new ProgressDialogHandler(this);
        progressDialogHandler.show();
        File file = new File(mCapturedImageUrl);
        MultipartBody.Part body = MultipartBody.Part.createFormData("company_logo", file.getName(),
                RequestBody.create(MediaType.parse("multipart/form-data"), file));

        RequestBody userId = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(userData.getId()));
        RequestBody languageId = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(userData.getLanguageId()));
        RequestBody authKey = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(""));

         Call<ApiResponse> apiResponseCall=  RestClient.getService().profileImage(userId,languageId,authKey,body);
        apiResponseCall.enqueue(new ApiCall(this) {
    @Override
    public void onSuccess(ApiResponse apiResponse) {
     progressDialogHandler.hide();
        if(apiResponse.status){
         userData.setImageUrl(apiResponse.paylpad.getData().getCompanyLogo());
            imageLoader.displayImage("file://" + mCapturedImageUrl,imgCompanyLogo);
            UserData userDataNew=new UserData();
            userDataNew=userData;
            userDataNew.setCompanyLogo(apiResponse.paylpad.getData().getCompanyLogo());
            userData.setCompanyLogo(apiResponse.paylpad.getData().getCompanyLogo());
            appPref.saveUserObject(userDataNew);

        }
    }

    @Override
    public void onFail(Call<ApiResponse> call, Throwable t) {
        progressDialogHandler.hide();
    }
     });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            appPref.setLoginFlag(false);
            Intent intent=new Intent(this,SigninActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.activity_animation_enter,
                    R.anim.activity_animation_exit);
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }


    public void updateData(){}
}
