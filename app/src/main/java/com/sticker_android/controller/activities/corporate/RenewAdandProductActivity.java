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
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.sticker_android.R;
import com.sticker_android.constant.AppConstant;
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.model.User;
import com.sticker_android.model.corporateproduct.Category;
import com.sticker_android.model.corporateproduct.Product;
import com.sticker_android.model.interfaces.ImagePickerListener;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.AWSUtil;
import com.sticker_android.utils.ProgressDialogHandler;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.helper.PermissionManager;
import com.sticker_android.utils.sharedpref.AppPref;
import com.sticker_android.view.CategoryAdapter;
import com.sticker_android.view.SetDate;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import retrofit2.Call;

import static com.sticker_android.utils.helper.PermissionManager.Constant.READ_STORAGE_ACCESS_RQ;
import static com.sticker_android.utils.helper.PermissionManager.Constant.WRITE_STORAGE_ACCESS_RQ;

public class RenewAdandProductActivity extends AppBaseActivity implements View.OnClickListener {

    private AppPref appPref;
    private User userdata;
    private Toolbar toolbar;
    private Button btnRePost;
    private EditText edtExpireDate;
    private EditText edtCorpName, edtDescription;
    private Product productObj;
    private String mExpireDate;
    private SetDate setDate;
    private String type = "";
    private Spinner spnrCategory;
    private ArrayList<Category> corporateCategories = new ArrayList<>();
    private final int PROFILE_CAMERA_IMAGE = 0;
    private final int PROFILE_GALLERY_IMAGE = 1;
    private ImageView imvProductImage;
    private String mCapturedImageUrl;
    private android.app.AlertDialog mPermissionDialog;
    private String TAG = RenewAdandProductActivity.class.getSimpleName();
    ProgressBar pgrImage;
    private boolean isUpdated;
    private ImageView imvProductImage2;
    private int categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renew_adand_product);
        init();
        getuserInfo();
        getProductData();
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
        setProductdataIntoView();
        setButtonText();
        fetchCategoryApi();
        measureImageWidthHeight();
        imvProductImage2.setVisibility(View.GONE);
    }

    private void measureImageWidthHeight() {

        ViewTreeObserver vto = imvProductImage.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                imvProductImage.getViewTreeObserver().removeOnPreDrawListener(this);
                int finalWidth = imvProductImage.getMeasuredWidth();
                int height = finalWidth * 3 /5;
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imvProductImage.getLayoutParams();
                //  LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imvProductImage.getLayoutParams();
                layoutParams.height = height;
                imvProductImage.setLayoutParams(layoutParams);
                return true;
            }
        });
    }

    /**
     * Method is used to fetch the category api
     */
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


    /**
     * Method is used to set the category in spinner
     */
    private void setSpinnerAdaptor() {

        ArrayList<Category> corporate = new ArrayList<>(setCategory());
        corporateCategories.clear();
        corporateCategories.addAll(corporate);
        if (corporateCategories != null) {
            CategoryAdapter categoryAdapter = new CategoryAdapter(this, corporate);
            //  ArrayAdapter<Category> adapter = new ArrayAdapter<Category>(this, android.R.layout.simple_spinner_item, corporateCategories);
            //  adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnrCategory.setAdapter(categoryAdapter);

        }

    }

    private Set<Category> setCategory() {
Category category=new Category();
        Set<Category> temp = new LinkedHashSet<>();
        ArrayList<Product> tempPro = new ArrayList<>();
        if (productObj != null) {
            for (int i = 0; i < corporateCategories.size(); i++) {
                if (corporateCategories.get(i).categoryId == productObj.getCategoryId()) {
                //    temp.add(corporateCategories.get(i));
                    category=corporateCategories.get(i);
                    break;
                }
            }

        }
      temp.add(category);
 temp.addAll(corporateCategories);
        return temp;
    }

    private void setExpireDate() {
        setDate = new SetDate(edtExpireDate, this, R.style.AppThemeAddRenew);
        // setDate.setDate(productObj.getExpireDate());
        setDate.setMinDate(productObj.getExpireDate());
    }

    private void setButtonText() {

        if (type.equals("Edit")) {
            btnRePost.setText("Update");
            setDate = new SetDate(edtExpireDate, this, R.style.AppThemeAddRenew);
        } else {
            edtDescription.setLongClickable(false);
            edtDescription.setEnabled(false);
            edtCorpName.setLongClickable(false);
            edtCorpName.setEnabled(false);
            btnRePost.setText("Update");
            setExpireDate();
        }
    }

    private void setProductdataIntoView() {

        if (productObj != null) {

            edtExpireDate.setText(Utils.dateModify(productObj.getExpireDate()));
            edtCorpName.setText(productObj.getProductname());
            edtDescription.setText(productObj.getDescription());
            edtDescription.setSelection(edtDescription.getText().length());
            edtCorpName.setSelection(edtCorpName.getText().length());
            mExpireDate = productObj.getExpireDate();
            pgrImage.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(productObj.getImagePath()).fitCenter()
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {

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
    }


    private void getProductData() {

        if (getIntent().getExtras() != null) {

            productObj = getIntent().getExtras().getParcelable(AppConstant.PRODUCT_OBJ_KEY);
            type = getIntent().getExtras().getString("edit");
        }
    }

    @Override
    protected void setViewListeners() {
        btnRePost.setOnClickListener(this);
        //  imvProductImage.setOnClickListener(this);
    }

    @Override
    protected void setViewReferences() {

        btnRePost = (Button) findViewById(R.id.act_corp_add_new_btn_re_post);
        edtExpireDate = (EditText) findViewById(R.id.act_add_new_ad_corp_edt_expire_date);
        edtDescription = (EditText) findViewById(R.id.act_add_new_ad_corp_edt_description);
        edtCorpName = (EditText) findViewById(R.id.act_add_new_corp_edt_name);
        spnrCategory = (Spinner) findViewById(R.id.spnrRenewCategory);
        imvProductImage = (ImageView) findViewById(R.id.imvProductImage);
        pgrImage = (ProgressBar) findViewById(R.id.pgrImage);
        imvProductImage2 = (ImageView) findViewById(R.id.imvProductImage2);

    }

    @Override
    protected boolean isValidData() {

        if (edtCorpName.getText().toString().trim().isEmpty()) {
            Utils.showToast(this, "Please enter a name.");
            return false;
        } else if (edtExpireDate.getText().toString().trim().isEmpty()) {
            Utils.showToast(this, "Please enter a expire date.");

            return false;
        } else if (edtDescription.getText().toString().trim().isEmpty()) {
            return false;
        } /*else if (productObj.getExpireDate().trim().equals(mExpireDate)) {
            Utils.showToast(this, "Please select a valid date.");
            return false;
        }*/
        return true;
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
        textView.setText(Utils.capitlizeText(type));
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
    public void onClick(View v) {
        Utils.hideKeyboard(this);
        switch (v.getId()) {
            case R.id.act_corp_add_new_btn_re_post:
                if (isValidData()) {
                    if (isUpdated)
                        beginUpload(mCapturedImageUrl);
                    else
                        renewOrEditApi(productObj.getImagePath());

                    // renewOrEditApi();
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
     *
     * @param imagePath
     */
    private void renewOrEditApi(String imagePath) {
        int categoryId = getCategoryId();
        if (setDate != null)
            mExpireDate = setDate.getChosenDate();
        final ProgressDialogHandler progressDialogHandler = new ProgressDialogHandler(this);
        progressDialogHandler.show();
        final String type = productObj.getType();
        Call<ApiResponse> apiResponseCall = RestClient.getService().apiAddProduct(userdata.getLanguageId(), userdata.getAuthrizedKey(),
                userdata.getId(), edtCorpName.getText().toString().trim(), type, edtDescription.getText().toString().trim()
                , mExpireDate, imagePath, String.valueOf(productObj.getProductid()),categoryId );

        apiResponseCall.enqueue(new ApiCall(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                progressDialogHandler.hide();
                if (apiResponse.status) {
                    Utils.showToast(getApplicationContext(), Utils.capitlizeText(type) + " updated successfully.");
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
                    mCapturedImageUrl = sourceUrl;
                    openCropActivity(sourceUrl);
                }
                break;
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    mCapturedImageUrl = resultUri.getPath();
                    isUpdated = true;
                    imvProductImage2.setVisibility(View.GONE);
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
                .setAspectRatio(2, 1)
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
        final String fileName = userdata.getId() + "_" + System.currentTimeMillis();
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
                    renewOrEditApi(imagePath);
                    isUpdated = false;
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

    public int getCategoryId() {
        return corporateCategories.get(spnrCategory.getSelectedItemPosition()).categoryId;
    }
}
