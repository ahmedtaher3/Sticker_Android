package com.sticker_android.controller.activities.corporate;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.sticker_android.R;
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.controller.activities.common.signin.SigninActivity;
import com.sticker_android.model.UserData;
import com.sticker_android.model.interfaces.ImagePickerListener;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.CommonSnackBar;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.commonprogressdialog.CommonProgressBar;
import com.sticker_android.utils.helper.PermissionManager;
import com.sticker_android.utils.sharedpref.AppPref;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;

import retrofit2.Call;

import static com.sticker_android.utils.helper.PermissionManager.Constant.READ_STORAGE_ACCESS_RQ;
import static com.sticker_android.utils.helper.PermissionManager.Constant.WRITE_STORAGE_ACCESS_RQ;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_profile);
        setViewReferences();
        setViewListeners();
        init();
    }

    @Override
    protected void setViewListeners() {
        btnSubmit.setOnClickListener(this);
    }

    @Override
    protected void setViewReferences() {
        imgCompanyLogo = (ImageView) findViewById(R.id.imgCompanyLogo);
        rlBgProfile= (RelativeLayout) findViewById(R.id.bgProfile);
        llCorporate= (LinearLayout) findViewById(R.id.llCorporate);
        edtCompanyName= (EditText) findViewById(R.id.act_profile_edt_company_name);
        edtCompanyAddress= (EditText) findViewById(R.id.act_profile_edt_company_address);
        edtProfileFirstName= (EditText) findViewById(R.id.act_profile_edt_first_name);
        edtProfileLastName= (EditText) findViewById(R.id.act_profile_edt_last_name);
        edtProfileEmail= (EditText) findViewById(R.id.act_profile_edt_email);
        btnSubmit= (Button) findViewById(R.id.act_profile_btn_register);
    }

    @Override
    protected boolean isValidData()
    {
        String firstName = this.edtProfileFirstName.getText().toString().trim();
        String lastName = this.edtProfileLastName.getText().toString().trim();
        if (firstName.isEmpty()) {
            CommonSnackBar.show(edtProfileFirstName,getString(R.string.first_name_cannot_be_empty), Snackbar.LENGTH_SHORT);
            this.edtProfileFirstName.requestFocus();
            return false;
        }else if (lastName.isEmpty()) {
            CommonSnackBar.show(edtProfileLastName,getString(R.string.last_name_cannot_be_empty),Snackbar.LENGTH_SHORT);
            this.edtProfileLastName.requestFocus();
            return false;
        }
        String email = this.edtProfileEmail.getText().toString().trim();
        if (email.isEmpty()) {
            CommonSnackBar.show(edtProfileEmail,getString(R.string.msg_email_cannot_be_empty),Snackbar.LENGTH_SHORT);
            this.edtProfileEmail.requestFocus();
            return false;
        }else if (Patterns.EMAIL_ADDRESS.matcher(email).matches())  {
            CommonSnackBar.show(edtProfileEmail,getString(R.string.msg_email_not_valid),Snackbar.LENGTH_SHORT);
            this.edtProfileEmail.requestFocus();
            return false;
        }

        return true;
    }


    private void init() {
        userData=appPref.getUserInfo();
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
                    rlBgProfile.setBackground(getResources().getDrawable(R.drawable.corporate_hdpi));
                    llCorporate.setVisibility(View.VISIBLE);
                    break;
            }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.act_profile_btn_register:
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
            final CommonProgressBar commonProgressBar = new CommonProgressBar(getActivity());
            commonProgressBar.show();
            Call<ApiResponse> apiResponseCall = RestClient.getService().updateProfile(userData.getId(), edtCompanyName.getText().toString(),
                    "", edtCompanyAddress.getText().toString(), edtProfileFirstName.getText().toString(), edtProfileLastName.getText().toString(),
                    userData.getEmail(), userData.getUserType());
            apiResponseCall.enqueue(new ApiCall(getActivity()) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    commonProgressBar.hide();
                    if (apiResponse.status) {
                        appPref.saveUserObject(apiResponse.paylpad.getData());
                        appPref.setLoginFlag(true);
                    } else {
                        CommonSnackBar.show(edtCompanyAddress, apiResponse.error.message, Snackbar.LENGTH_SHORT);
                    }
                }

                @Override
                public void onFail(Call<ApiResponse> call, Throwable t) {
                    commonProgressBar.hide();
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
                        openCropActivity(mCapturedImageUrl);
                    }
                }
                break;

            case PROFILE_GALLERY_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = data.getData();
                    String sourceUrl = Utils.getGalleryImagePath(this, selectedImage);
                    File file = Utils.getCustomImagePath(this, "temp");
                    mCapturedImageUrl = file.getAbsolutePath();

                    openCropActivity(sourceUrl);
                }
                break;
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    imageLoader.displayImage(resultUri.toString(), imgCompanyLogo);
                    mCapturedImageUrl = resultUri.getPath();

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    error.printStackTrace();
                }
        }
    }

    private void openCropActivity(String url) {
        CropImage.activity(Uri.fromFile(new File(url)))
                .setGuidelines(CropImageView.Guidelines.OFF)
                .setFixAspectRatio(true)
                .setAspectRatio(1, 1)
                .setAutoZoomEnabled(true)
                .start(this);
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
            startNewActivity(SigninActivity.class);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
