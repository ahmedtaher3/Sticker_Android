package com.sticker_android.controller.activities.fan.home;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.sticker_android.R;
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.controller.fragment.common.ProfileFragment;
import com.sticker_android.model.User;
import com.sticker_android.model.corporateproduct.Category;
import com.sticker_android.model.interfaces.CategoryDataListener;
import com.sticker_android.model.interfaces.ImagePickerListener;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.AppLogger;
import com.sticker_android.utils.ProgressDialogHandler;
import com.sticker_android.utils.UserTypeEnum;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.sharedpref.AppPref;
import com.sticker_android.view.CustomAppCompatTextView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

public class AddNewVote extends AppBaseActivity implements View.OnClickListener {

    private DisplayImageOptions mDisplayImageOptions;
    private DisplayImageOptions mDisplayImageOptions_2;
    private ProfileFragment.OnFragmentProfileListener mListener;
    private File compressedImageFile;
    private File compressedImageFile_2;
    private String TAG = AddNewVote.class.getSimpleName();
    private String mCapturedImageUrl;
    private String mCapturedImageUrl_2;
    public static final int PROFILE_CAMERA_IMAGE = 0;
    public static final int PROFILE_CAMERA_IMAGE_2 = 10;
    public boolean image_1 = true;
    public boolean first_image = false;
    public boolean second_image = false;
    public static final int PROFILE_GALLERY_IMAGE = 1;
    public static final int PROFILE_GALLERY_IMAGE_2 = 11;


    private AppPref appPref;
    private User user;

    private ImageView first_choice_img, second_choice_img, imv_nav_drawer_menu;
    private Button button;
    private Spinner spnrCategory;
    private Toolbar toolbar;
    private TextInputEditText voteDescription , first_description , second_description;
    private CustomAppCompatTextView second_imgPlaceHolder, first_imgPlaceHolder;

    int categoryId;

    private ArrayList<Category> corporateCategories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_vote);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        init();
        setViewReferences();
        setViewListeners();
        setToolBarTitle();
        setToolbarBackground(toolbar);
        setSupportActionBar(toolbar);

        mDisplayImageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .resetViewBeforeLoading(true)
                .considerExifParams(true)
                .build();

        mDisplayImageOptions_2 = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .resetViewBeforeLoading(true)
                .considerExifParams(true)
                .build();


        ArrayList<Category> categoryList = appPref.getCategoryList();

        final Category placeHolderCategory = new Category(-1, getString(R.string.select_category_txt));
        if (categoryList != null && categoryList.size() != 0) {
            corporateCategories = categoryList;
            corporateCategories.add(0, placeHolderCategory);
            setSpinnerAdapter(getActivity(), spnrCategory, corporateCategories);


        } else {

            fetchCategoryApi(new CategoryDataListener() {

                @Override
                public void onCategoryDataRetrieved(ArrayList<Category> categories) {
                    corporateCategories = categories;
                    corporateCategories.add(0, placeHolderCategory);
                    setSpinnerAdapter(getActivity(), spnrCategory, corporateCategories);


                }

                @Override
                public void onFailure() {

                }
            });
        }


    }


    private void init() {
        appPref = new AppPref(this);
        user = appPref.getUserInfo();
    }

    private void setToolbarBackground(Toolbar toolbar) {


        if (appPref.getLoginFlag(false)) {
            UserTypeEnum userTypeEnum = Enum.valueOf(UserTypeEnum.class, user.getUserType().toUpperCase());

            switch (userTypeEnum) {
                case FAN:
                    toolbar.setBackground(getResources().getDrawable(R.drawable.fan_header_xhdpi));
                    break;
                case DESIGNER:
                    toolbar.setBackground(getResources().getDrawable(R.drawable.gradient_bg_des_hdpi));

                    break;
                case CORPORATE:
                    toolbar.setBackground(getResources().getDrawable(R.drawable.gradient_bg_hdpi));

                    break;
            }
        } else {
            toolbar.setBackground(getResources().getDrawable(R.drawable.fan_header_xhdpi));

        }


    }

    private void setToolBarTitle() {
        TextView textView = (TextView) toolbar.findViewById(R.id.tvToolbar);
        textView.setText(getResources().getString(R.string.add_new_vote));
        toolbar.setTitle("");
        centerToolbarText(toolbar, textView);
    }

    private void centerToolbarText(final Toolbar toolbar, final TextView textView) {
        toolbar.postDelayed(new Runnable() {
            @Override
            public void run() {
                int maxWidth = toolbar.getWidth();
                int titleWidth = textView.getWidth();
                int iconWidth = maxWidth - titleWidth;

                if (iconWidth > 0) {
                    //icons (drawer, menu) are on left and right side
                    int width = maxWidth - iconWidth * 2;
                    textView.setMinimumWidth(width);
                    textView.getLayoutParams().width = width;
                }
            }
        }, 0);
    }

    @Override
    protected void setViewListeners() {

        first_choice_img.setOnClickListener(this);
        second_choice_img.setOnClickListener(this);
        imv_nav_drawer_menu.setOnClickListener(this);
        button.setOnClickListener(this);

    }

    @Override
    protected void setViewReferences() {
        first_choice_img = (ImageView) findViewById(R.id.first_choice_img);
        second_choice_img = (ImageView) findViewById(R.id.second_choice_img);
        imv_nav_drawer_menu = (ImageView) findViewById(R.id.imv_nav_drawer_menu);
        button = (Button) findViewById(R.id.uploadVote);
        spnrCategory = (Spinner) findViewById(R.id.spnrCategory);
        voteDescription = (TextInputEditText) findViewById(R.id.vote_description);
        first_description = (TextInputEditText) findViewById(R.id.first_description);
        second_description = (TextInputEditText) findViewById(R.id.second_description);
        first_imgPlaceHolder = (CustomAppCompatTextView) findViewById(R.id.first_imgPlaceHolder);
        second_imgPlaceHolder = (CustomAppCompatTextView) findViewById(R.id.second_imgPlaceHolder);
        spnrCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                categoryId = corporateCategories.get(spnrCategory.getSelectedItemPosition()).categoryId;

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    private void captureImage(int code) {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = Utils.getCustomImagePath(getActivity(), System.currentTimeMillis() + "");
        mCapturedImageUrl = file.getAbsolutePath();
        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        getActivity().startActivityForResult(takePicture, code);
    }

    private void captureImage_2(int code) {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = Utils.getCustomImagePath(getActivity(), System.currentTimeMillis() + "");
        mCapturedImageUrl_2 = file.getAbsolutePath();
        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        getActivity().startActivityForResult(takePicture, code);
    }

    private void pickGalleryImage(int code) {
        Intent openGallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        getActivity().startActivityForResult(openGallery, code);
    }

    private void openCropActivity(String url) {
        CropImage.activity(Uri.fromFile(new File(url)))
                .setGuidelines(CropImageView.Guidelines.OFF)
                .setFixAspectRatio(true)
                .setAspectRatio(1, 1)
                .setAutoZoomEnabled(true)
                .start(AddNewVote.this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("Profile fragment", "on activity");
        switch (requestCode) {

            case PROFILE_CAMERA_IMAGE:
                if (resultCode == RESULT_OK) {
                    if (mCapturedImageUrl != null) {
                        openCropActivity(mCapturedImageUrl);
                        //uploadImage();
                    }
                }
                break;

            case PROFILE_CAMERA_IMAGE_2:
                if (resultCode == RESULT_OK) {
                    if (mCapturedImageUrl_2 != null) {
                        openCropActivity(mCapturedImageUrl_2);
                        //uploadImage();
                    }
                }
                break;

            case PROFILE_GALLERY_IMAGE:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    String sourceUrl = Utils.getGalleryImagePath(getActivity(), selectedImage);
                    File file = Utils.getCustomImagePath(getActivity(), "temp");
                    mCapturedImageUrl = file.getAbsolutePath();
                    mCapturedImageUrl = sourceUrl;
                    openCropActivity(sourceUrl);
                    //uploadImage();
                }
                break;

            case PROFILE_GALLERY_IMAGE_2:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    String sourceUrl = Utils.getGalleryImagePath(getActivity(), selectedImage);
                    File file = Utils.getCustomImagePath(getActivity(), "temp");
                    mCapturedImageUrl_2 = file.getAbsolutePath();
                    mCapturedImageUrl_2 = sourceUrl;
                    openCropActivity(sourceUrl);
                    //uploadImage();
                }
                break;

            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:


                if (image_1) {
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    if (resultCode == RESULT_OK) {
                        Uri resultUri = result.getUri();
                        imageLoader.displayImage(resultUri.toString(), first_choice_img, mDisplayImageOptions);
                        first_imgPlaceHolder.setVisibility(View.GONE);
                       first_image = true;
                        mCapturedImageUrl = resultUri.getPath();




                        /*Compress code added*/
                        try {
                            /*     File file = new File(getRealPathFromURI(resultUri));
                             */
                            File file = new File(mCapturedImageUrl);
                            AppLogger.debug(TAG, "Size is before compress in unit " + Utils.getFileSize(file));
                            compressedImageFile = new Compressor(getActivity()).compressToFile(file);
                            AppLogger.debug(TAG, "Size is after compress " + Integer.parseInt(String.valueOf(compressedImageFile.length() / 1024)));
                            AppLogger.debug(TAG, "Size is after compress in unit " + Utils.getFileSize(compressedImageFile));

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mCapturedImageUrl = compressedImageFile.getAbsolutePath();
                        //   uploadImage();
                    } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        Exception error = result.getError();
                        error.printStackTrace();
                        Log.i("TestImages", "CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE");
                    }
                } else {

                    CropImage.ActivityResult result_2 = CropImage.getActivityResult(data);
                    if (resultCode == RESULT_OK) {
                        Uri resultUri = result_2.getUri();
                        imageLoader.displayImage(resultUri.toString(), second_choice_img, mDisplayImageOptions_2);
                        second_imgPlaceHolder.setVisibility(View.GONE);
                        second_image = true;
                        mCapturedImageUrl_2 = resultUri.getPath();


                        /*Compress code added*/
                        try {
                            /*     File file = new File(getRealPathFromURI(resultUri));
                             */
                            File file = new File(mCapturedImageUrl_2);
                            AppLogger.debug(TAG, "Size is before compress in unit " + Utils.getFileSize(file));
                            compressedImageFile_2 = new Compressor(getActivity()).compressToFile(file);
                            AppLogger.debug(TAG, "Size is after compress " + Integer.parseInt(String.valueOf(compressedImageFile_2.length() / 1024)));
                            AppLogger.debug(TAG, "Size is after compress in unit " + Utils.getFileSize(compressedImageFile_2));

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mCapturedImageUrl_2 = compressedImageFile_2.getAbsolutePath();
                        //   uploadImage();
                    } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        Exception error = result_2.getError();
                        error.printStackTrace();
                        Log.i("TestImages", "CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE");

                    }
                }


        }
    }


    @Override
    protected boolean isValidData() {


        if (voteDescription.getText().toString().trim().isEmpty()) {
            Utils.showToast(this, getString(R.string.pls_add_vote_desc));
            return false;
        }  else if (spnrCategory.getSelectedItemPosition() == 0) {
            Utils.showToast(this, getString(R.string.pls_select_category));
            return false;
        }else if (!first_image) {
                Utils.showToast(this, getString(R.string.pls_upload_image));
            return false;
        }  else if (first_description.getText().toString().trim().isEmpty()) {
            Utils.showToast(this, getString(R.string.add_first_desc));
            return false;
        }else if (!second_image) {
            Utils.showToast(this, getString(R.string.pls_upload_image));
            return false;
        }  else if (second_description.getText().toString().trim().isEmpty()) {
            Utils.showToast(this, getString(R.string.add_secound_desc));
            return false;
        }else if (mCapturedImageUrl_2 == null || mCapturedImageUrl == null) {
            Utils.showToast(this, getString(R.string.pls_upload_image));
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.first_choice_img:
                image_1 = true;
                Utils.showAlertDialogToGetPic(getActivity(), new ImagePickerListener() {
                    @Override
                    public void pickFromGallery() {
                        pickGalleryImage(PROFILE_GALLERY_IMAGE);
                    }

                    @Override
                    public void captureFromCamera() {
                        captureImage(PROFILE_CAMERA_IMAGE);
                    }

                    @Override
                    public void selectedItemPosition(int position) {

                    }
                });
                break;

            case R.id.second_choice_img:
                image_1 = false;
                Utils.showAlertDialogToGetPic(getActivity(), new ImagePickerListener() {
                    @Override
                    public void pickFromGallery() {
                        pickGalleryImage(PROFILE_GALLERY_IMAGE_2);
                    }

                    @Override
                    public void captureFromCamera() {
                        captureImage_2(PROFILE_CAMERA_IMAGE_2);
                    }

                    @Override
                    public void selectedItemPosition(int position) {

                    }
                });

                break;

            case R.id.imv_nav_drawer_menu:
                finish();
                break;

            case R.id.uploadVote:

                if (isValidData()) {
                    uploadVote();
                }

                break;
        }


    }


    void uploadVote() {

        final ProgressDialogHandler progressDialogHandler = new ProgressDialogHandler(this);
        progressDialogHandler.show();

        File file = new File(mCapturedImageUrl);
        MultipartBody.Part body = MultipartBody.Part.createFormData("first_choice_img", file.getName(),
                RequestBody.create(MediaType.parse("multipart/form-data"), file));

        File file2 = new File(mCapturedImageUrl_2);
        MultipartBody.Part body_2 = MultipartBody.Part.createFormData("second_choice_img", file2.getName(),
                RequestBody.create(MediaType.parse("multipart/form-data"), file2));

        RequestBody user_id = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(user.getId()));
        RequestBody category_id = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(categoryId));
        RequestBody vote_desc = RequestBody.create(MediaType.parse("multipart/form-data"), voteDescription.getText().toString().trim());
        RequestBody first_choice_desc = RequestBody.create(MediaType.parse("multipart/form-data"), first_description.getText().toString().trim());
        RequestBody second_choice_desc = RequestBody.create(MediaType.parse("multipart/form-data"), second_description.getText().toString().trim());


        Call<ApiResponse> apiResponseCall = RestClient.getService().createNewVote_2(user_id, category_id, vote_desc, first_choice_desc, body, body_2, second_choice_desc);
        apiResponseCall.enqueue(new ApiCall(getActivity()) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.status) {
                    progressDialogHandler.hide();
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                    Log.i("apiResponse", "done");
                } else {
                    progressDialogHandler.hide();
                    Toast.makeText(AddNewVote.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                    Log.i("apiResponse", apiResponse.error.message);
                }

            }

            @Override
            public void onFail(Call<ApiResponse> call, Throwable t) {

            }
        });


    }


    private void fetchCategoryApi(final CategoryDataListener categoryDataListener) {

        final ProgressDialogHandler progressDialogHandler = new ProgressDialogHandler(this);
        progressDialogHandler.show();
        Call<ApiResponse> apiResponseCall = RestClient.getService().apiCorporateCategoryList(user.getLanguageId(), user.getAuthrizedKey()
                , user.getId(), "corporate_category");

        apiResponseCall.enqueue(new ApiCall(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                progressDialogHandler.hide();
                if (apiResponse.status) {
                    corporateCategories = apiResponse.paylpad.corporateCategories;
                    if (corporateCategories != null && corporateCategories.size() != 0) {
                        appPref.saveCategoryList(corporateCategories);
                    }
                    categoryDataListener.onCategoryDataRetrieved(corporateCategories);
                }
            }

            @Override
            public void onFail(Call<ApiResponse> call, Throwable t) {
                progressDialogHandler.hide();
                categoryDataListener.onFailure();
            }
        });

    }

    public void setSpinnerAdapter(final Context context, Spinner spinnerView, final ArrayList<Category> dataSource) {
        Log.e(TAG, "Category list size => " + dataSource.size());
        final Resources resources = context.getResources();
        ArrayAdapter adapter = new ArrayAdapter<Category>(context, android.R.layout.simple_spinner_item, dataSource) {

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {

                View v = null;

                if (position == 0) {
                    TextView tv = new TextView(context);
                    tv.setHeight(0);
                    tv.setVisibility(View.GONE);
                    v = tv;
                } else {
                    v = super.getDropDownView(position, null, parent);
                }

                if (v instanceof TextView) {
                    TextView txtView = (TextView) v;
                    Configuration config = context.getResources().getConfiguration();
                    final boolean isLeftToRight;
                    isLeftToRight = config.getLayoutDirection() != View.LAYOUT_DIRECTION_RTL;
                    if (isLeftToRight) {
                    } else {
                        txtView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                    }

                    Object object = dataSource.get(position);

                    if (object instanceof String) {
                        txtView.setText((String) object);
                    } else if (object instanceof Category) {
                        txtView.setText(((Category) object).categoryName);
                    }
                }
                parent.setVerticalScrollBarEnabled(false);
                return v;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                TextView tv = (TextView) super.getView(position, convertView, parent);
                tv.setBackgroundColor(Color.TRANSPARENT);
                if (position == 0) {
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.text_size_edittext));
                tv.setPadding(0, 0, 0, 0);
                Configuration config = context.getResources().getConfiguration();
                final boolean isLeftToRight;
                isLeftToRight = config.getLayoutDirection() != View.LAYOUT_DIRECTION_RTL;
                if (isLeftToRight) {
                    tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                } else {

                }

                //  tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

                Object object = dataSource.get(position);

                if (object instanceof String) {
                    tv.setText((String) object);
                } else if (object instanceof Category) {
                    tv.setText(((Category) object).categoryName);
                }
                return tv;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerView.setAdapter(adapter);
    }

}
