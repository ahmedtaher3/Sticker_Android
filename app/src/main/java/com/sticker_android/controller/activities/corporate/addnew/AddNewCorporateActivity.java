package com.sticker_android.controller.activities.corporate.addnew;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.sticker_android.utils.AppLogger;
import com.sticker_android.utils.CategorySpinnerAdaptor;
import com.sticker_android.utils.ImagesBottomSheet;
import com.sticker_android.utils.ProgressDialogHandler;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.helper.PermissionManager;
import com.sticker_android.utils.sharedpref.AppPref;
import com.sticker_android.view.SetDate;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import id.zelory.compressor.Compressor;
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
    private RelativeLayout rlJustificationHolder;
    private String mCapturedImageUrl;
    private android.app.AlertDialog mPermissionDialog;
    private ImageView imvProductImage2;
    private TextView txtViewMoreComment, txtRecentComments, edtJustification;
    private TextView imgPlaceHolder;
    private File compressedImageFile;

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

        txtViewMoreComment.setTextColor(ContextCompat.getColor(this, R.color.colorCorporateText));
        changeStatusBarColor(getResources().getColor(R.color.colorstatusBarCorporate));

        measureImageWidthHeight();
        fetchCategoryApi();

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
        imvProductImage = (ImageView) findViewById(R.id.imvProductImage);
        imvProductImage2 = (ImageView) findViewById(R.id.imvProductImage2);
        rlJustificationHolder = (RelativeLayout) findViewById(R.id.rlJustificationHolder);
        edtJustification = (TextView) findViewById(R.id.edtJustification);
        txtViewMoreComment = (TextView) findViewById(R.id.txtViewMoreComment);
        txtRecentComments = (TextView) findViewById(R.id.txtRecentComments);
        imgPlaceHolder = (TextView) findViewById(R.id.imgPlaceHolder);
    }

    @Override
    protected boolean isValidData() {

        if (mCapturedImageUrl == null) {
            Utils.showToast(this, getString(R.string.txt_please_upload_a_image));
            return false;

        } else if (edtCorpName.getText().toString().trim().isEmpty()) {
            Utils.showToast(this, getString(R.string.txt_please_enter_a_name));
            return false;
        } else if (edtExpireDate.getText().toString().trim().isEmpty()) {
            Utils.showToast(this, getString(R.string.txt_please_enter_a_expire_date));
            return false;
        } else if (spnrCategory.getSelectedItemPosition() == 0) {
            Utils.showToast(this, getString(R.string.txt_please_select_a_category));
            return false;
        } else if (edtDescription.getText().toString().trim().isEmpty()) {
            Utils.showToast(this, getString(R.string.txt_please_enter_description));
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

                ImagesBottomSheet addPhotoBottomDialogFragment =
                        new ImagesBottomSheet(new ImagesBottomSheet.DialogListener() {
                            @Override
                            public void camera() {

                                captureImage();

                            }

                            @Override
                            public void gallery() {
                                pickGalleryImage();


                            }
                        });
                addPhotoBottomDialogFragment.show(getSupportFragmentManager(),
                        "add_photo_dialog_fragment");
                break;
        }
    }


    private void captureImage() {

        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = Utils.getCustomImagePath(this, System.currentTimeMillis() + "");
        AppLogger.debug(TAG, "captured url" + mCapturedImageUrl + "file path is:" + file);
        mCapturedImageUrl = file.getAbsolutePath();
        AppLogger.debug(TAG, "captured url" + mCapturedImageUrl);
        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(takePicture, PROFILE_CAMERA_IMAGE);
    }

    private void pickGalleryImage() {
        Intent openGallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(openGallery, PROFILE_GALLERY_IMAGE);
    }

    /**
     * Method is used to call the add ads or product api
     *
     * @param imagePath
     */
    private void addProductOrAdApi(String imagePath) {
        int categoryId = corporateCategories.get(spnrCategory.getSelectedItemPosition()).categoryId;

        if (setDate != null)
            mExpireDate = setDate.getChosenDate();

        final ProgressDialogHandler progressDialogHandler = new ProgressDialogHandler(this);
        progressDialogHandler.show();
        final String type = getSelectedType();
        Call<ApiResponse> apiResponseCall = RestClient.getService().apiAddProduct(userdata.getLanguageId(), userdata.getAuthrizedKey(),
                userdata.getId(), edtCorpName.getText().toString().trim(), type, edtDescription.getText().toString().trim()
                , mExpireDate, imagePath, "", categoryId);

        apiResponseCall.enqueue(new ApiCall(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                progressDialogHandler.hide();
                if (apiResponse.status) {
                    String typeProduct = "Product";
                    if (type.equalsIgnoreCase("ads"))
                        typeProduct = "Ad";

                    if (typeProduct.equalsIgnoreCase("Ad")) {
                        Utils.showToast(getApplicationContext(), getString(R.string.txt_add_added_successfully));
                    } else {
                        Utils.showToast(getApplicationContext(), getString(R.string.txt_product_added_successfully));
                    }
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

            if (tab.getPosition() == 0) {
                imgPlaceHolder.setText(getResources().getString(R.string.txt_upload_ad));
            } else {
                imgPlaceHolder.setText(R.string.txt_upload_product);
            }
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
                        AppLogger.debug(TAG, "captured url" + mCapturedImageUrl + "on activity result:" + mCapturedImageUrl);
                        openCropActivity(mCapturedImageUrl);
                        //uploadImage();

                    }
                } else {
                    mCapturedImageUrl = null;
                }
                break;

            case PROFILE_GALLERY_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = data.getData();
                    String sourceUrl = Utils.getGalleryImagePath(this, selectedImage);
                    File file = Utils.getCustomImagePath(this, "temp");
                    mCapturedImageUrl = file.getAbsolutePath();
                    mCapturedImageUrl = sourceUrl;
                    openCropActivity(sourceUrl);
                }
                break;
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    String path = result.getUri().getPath(); // "/mnt/sdcard/FileName.mp3"
                    try {
/*     File file = new File(getRealPathFromURI(resultUri));
                   */
                        AppLogger.debug(TAG,"Size is befor compress "+resultUri.getPath().length());
                        File file = new File(resultUri.getPath());
                        compressedImageFile = new Compressor(this).compressToFile(file);
                        AppLogger.debug(TAG,"Size is after compress "+Integer.parseInt(String.valueOf(file.length()/1024)));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mCapturedImageUrl = compressedImageFile.getAbsolutePath();

                    //mCapturedImageUrl = resultUri.getPath();
                    imvProductImage2.setVisibility(View.GONE);
                    imgPlaceHolder.setVisibility(View.GONE);
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
                .setAspectRatio(5, 3)
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
            Toast.makeText(this, getResources().getString(R.string.txt_could_not_find_file_path),
                    Toast.LENGTH_LONG).show();
            return;
        }
        final String fileName = Utils.getFileName(userdata.getId());

        File file = new File(filePath);
        TransferObserver observer = new AWSUtil().getTransferUtility(this).upload(AppConstant.BUCKET_NAME, fileName,
                file);
        observer.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                Log.d(TAG, "onStateChanged: " + id + ", " + state);
                if (TransferState.COMPLETED == state) {
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

    String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

}
