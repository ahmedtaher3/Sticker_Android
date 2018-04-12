package com.sticker_android.controller.activities.corporate.addnew;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.sticker_android.R;
import com.sticker_android.constant.AppConstant;
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.controller.adaptors.ViewPagerAdapter;
import com.sticker_android.model.User;
import com.sticker_android.model.corporateproduct.Category;
import com.sticker_android.model.interfaces.ImagePickerListener;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.AWSUtil;
import com.sticker_android.utils.CategorySpinnerAdaptor;
import com.sticker_android.utils.ProgressDialogHandler;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.helper.PermissionManager;
import com.sticker_android.utils.sharedpref.AppPref;
import com.sticker_android.view.SetDate;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;

import retrofit2.Call;

import static com.sticker_android.utils.helper.PermissionManager.Constant.READ_STORAGE_ACCESS_RQ;
import static com.sticker_android.utils.helper.PermissionManager.Constant.WRITE_STORAGE_ACCESS_RQ;

public class AddNewCorporateActivity extends AppBaseActivity implements View.OnClickListener {

    private final String TAG = AddNewCorporateActivity.class.getSimpleName();
    private Toolbar toolbar;
    private AppPref appPref;
    private User userdata;
    private ViewPagerAdapter adapter;
    private TabLayout tabLayout;
    private Button btnPost;
    private EditText edtExpireDate;
    private EditText edtCorpName, edtDescription;
    private String mExpireDate = "2018-04-06";
    private SetDate setDate;
    private Spinner spnrCategory;
    private ArrayList<Category> corporateCategories = new ArrayList<>();

    private final int PROFILE_CAMERA_IMAGE = 0;
    private final int PROFILE_GALLERY_IMAGE = 1;
    private ImageView imvProductImage;
    private String mCapturedImageUrl;
    private android.app.AlertDialog mPermissionDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_corporate);
        init();
        getuserInfo();
        setToolbar();
        setViewReferences();
        setViewListeners();
        toolbar.setNavigationIcon(R.drawable.back_arrow_small);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        addTabsDynamically();
        setBackground();
        setSelectedTabColor();
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setSelectedTabIndicatorColor(Color.TRANSPARENT);
        setDate = new SetDate(edtExpireDate, this, R.style.AppThemeAddRenew);
        fetchCategoryApi();

    }

    private void fetchCategoryApi() {

        final ProgressDialogHandler progressDialogHandler = new ProgressDialogHandler(this);
        progressDialogHandler.show();
        Call<ApiResponse> apiResponseCall = RestClient.getService().apiCorporateCategoryList(userdata.getLanguageId(), userdata.getAuthrizedKey()
                , userdata.getId(), "corporate_category");

        apiResponseCall.enqueue(new ApiCall(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                progressDialogHandler.hide();
                if (apiResponse.status) {
                    corporateCategories = apiResponse.paylpad.corporateCategories;
                    setSpinnerAdaptor();
                }

            }

            @Override
            public void onFail(Call<ApiResponse> call, Throwable t) {
                progressDialogHandler.hide();
            }
        });

    }

    private void setSpinnerAdaptor() {

        if (corporateCategories != null) {
            CategorySpinnerAdaptor categorySpinnerAdaptor = new CategorySpinnerAdaptor(this, corporateCategories);
            spnrCategory.setAdapter(categorySpinnerAdaptor);
            /*
            CategoryAdapter categoryAdapter=new CategoryAdapter(this,corporateCategories);
          //  ArrayAdapter<Category> adapter = new ArrayAdapter<Category>(this, android.R.layout.simple_spinner_item, corporateCategories);
          //  adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnrCategory.setAdapter(
                    new NothingSelectedSpinnerAdapter(
                            categoryAdapter,
                            R.layout.spinner_without_selection,
                            this));
*/
        }
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
        textView.setText(getResources().getString(R.string.act_corp_txt_add_new));
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
        toolbar.setBackground(getResources().getDrawable(R.drawable.corporate_header_hdpi));
    }

    @Override
    protected void setViewListeners() {
        tabLayout.addOnTabSelectedListener(new TabListeners());
        btnPost.setOnClickListener(this);
        edtExpireDate.setOnClickListener(this);
        imvProductImage.setOnClickListener(this);
    }

    private void setSelectedTabColor() {
        tabLayout.setTabTextColors(Color.parseColor("#AAFFFFFF"), Color.WHITE);
    }

    @Override
    protected void setViewReferences() {

        edtCorpName = (EditText) findViewById(R.id.act_add_new_corp_edt_name);
        tabLayout = (TabLayout) findViewById(R.id.act_landing_tab);
        btnPost = (Button) findViewById(R.id.act_corp_add_new_btn_post);
        edtExpireDate = (EditText) findViewById(R.id.act_add_new_ad_corp_edt_expire_date);
        edtDescription = (EditText) findViewById(R.id.edtDescription);
        spnrCategory = (Spinner) findViewById(R.id.spnrCategory);
        imvProductImage=(ImageView)findViewById(R.id.imvProductImage);
    }

    @Override
    protected boolean isValidData() {

        if(mCapturedImageUrl==null){
            Utils.showToast(this, "Please upload a image.");
            return false;

        }else
        if (edtCorpName.getText().toString().trim().isEmpty()) {
            Utils.showToast(this, "Please enter a name.");
            return false;
        } else if (edtExpireDate.getText().toString().trim().isEmpty()) {
            Utils.showToast(this, "Please enter a expire date.");
            return false;
        }else if(spnrCategory.getSelectedItemPosition()==0){
            Utils.showToast(this, "Please select a category.");
            return false;
        } else if (edtDescription.getText().toString().trim().isEmpty()) {
            Utils.showToast(this, "Please enter a description.");
            return false;
        }
        return true;
    }


    private void setBackground() {
        tabLayout.setBackground(getResources().getDrawable(R.drawable.side_nav_corporate));
    }

    public void addTabsDynamically() {

        TabLayout.Tab firstTab = tabLayout.newTab();
        firstTab.setText(getString(R.string.txt_add_new_ads_frag)); // set the Text for the first Tab
        tabLayout.addTab(firstTab);

        TabLayout.Tab sec = tabLayout.newTab();
        sec.setText(getString(R.string.txt__add_products_frag)); // set the Text for the first Tab
        tabLayout.addTab(sec);

        Utils.setTabLayoutDivider(tabLayout, this);
    }

    @Override
    public void onClick(View v) {
        Utils.hideKeyboard(this);
        switch (v.getId()) {
            case R.id.act_corp_add_new_btn_post:
                if (isValidData()) {
                    beginUpload(mCapturedImageUrl);
                    //addProductOrAdApi();
                }
                break;
            case R.id.imvProductImage:
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

    /**
     * Method is used to call the add ads or product api
     * @param imagePath
     */
    private void addProductOrAdApi(String imagePath) {
        int categoryId=corporateCategories.get(spnrCategory.getSelectedItemPosition()).categoryId;

        if (setDate != null)
            mExpireDate = setDate.getChosenDate();

        final ProgressDialogHandler progressDialogHandler = new ProgressDialogHandler(this);
        progressDialogHandler.show();
        final String type = getSelectedType();
        Call<ApiResponse> apiResponseCall = RestClient.getService().apiAddProduct(userdata.getLanguageId(), userdata.getAuthrizedKey(),
                userdata.getId(), edtCorpName.getText().toString().trim(), type, edtDescription.getText().toString().trim()
                , mExpireDate, imagePath, "",categoryId);

        apiResponseCall.enqueue(new ApiCall(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                progressDialogHandler.hide();
                if (apiResponse.status) {
                    Utils.showToast(getApplicationContext(), type + " added successfully.");
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

    /**
     * Method is used to get the type of posted product
     *
     * @return rerurns the type
     */
    public String getSelectedType() {
        String type = "ads";
        if (tabLayout.getSelectedTabPosition() == 1)
            type = "product";
        return type;
    }


    public class TabListeners implements TabLayout.OnTabSelectedListener {


        public TabListeners() {

        }

        @Override
        public void onTabSelected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

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
                        //uploadImage();

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
                    openCropActivity(sourceUrl);
                }
                break;
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    mCapturedImageUrl = resultUri.getPath();
                    imageLoader.displayImage(resultUri.toString(), imvProductImage, displayImageOptions);
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
                .setAspectRatio(4, 3)
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

    /*
         * Begins to upload the file specified by the file path.
         */
    private void beginUpload(String filePath) {
        final ProgressDialogHandler progressDialogHandler = new ProgressDialogHandler(this);
        progressDialogHandler.show();
        if (filePath == null) {
            Toast.makeText(this, "Could not find the filepath of the selected file",
                    Toast.LENGTH_LONG).show();
            return;
        }
        final String fileName = Utils.getFileName(userdata.getId());

        File file = new File(filePath);
        TransferObserver observer = new AWSUtil().getTransferUtility(this).upload(AppConstant.BUCKET_NAME, fileName,
                file);
        observer.setTransferListener(new TransferListener(){

            @Override
            public void onStateChanged(int id, TransferState state) {
                Log.d(TAG, "onStateChanged: " + id + ", " + state);
                if(TransferState.COMPLETED==state){
                    progressDialogHandler.hide();
                    String imagePath = AppConstant.BUCKET_IMAGE_BASE_URL + fileName;
                    addProductOrAdApi(imagePath);
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                Log.d(TAG, String.format("onProgressChanged: %d, total: %d, current: %d",
                        id, bytesTotal, bytesCurrent));
            }

            @Override
            public void onError(int id, Exception ex) {
                progressDialogHandler.hide();
                Log.e(TAG, "Error during upload: " + id, ex);
            }

        });
    }
}
