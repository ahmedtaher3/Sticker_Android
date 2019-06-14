package com.sticker_android.controller.activities.designer.addnew;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Movie;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.sticker_android.R;
import com.sticker_android.constant.AppConstant;
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.controller.activities.common.comments.CommentsActivity;
import com.sticker_android.controller.activities.designer.home.DesignerHomeActivity;
import com.sticker_android.controller.adaptors.ViewPagerAdapter;
import com.sticker_android.model.User;
import com.sticker_android.model.corporateproduct.Category;
import com.sticker_android.model.corporateproduct.Product;
import com.sticker_android.model.enums.DesignType;
import com.sticker_android.model.interfaces.CategoryDataListener;
import com.sticker_android.model.interfaces.ImagePickerListener;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.AWSUtil;
import com.sticker_android.utils.AppLogger;
import com.sticker_android.utils.ImageFileFilter;
import com.sticker_android.utils.ImagesBottomSheet;
import com.sticker_android.utils.ProgressDialogHandler;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.helper.PermissionManager;
import com.sticker_android.utils.sharedpref.AppPref;
import com.sticker_android.view.CustomAppCompatTextView;
import com.sticker_android.view.SetDate;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import id.zelory.compressor.Compressor;
import retrofit2.Call;

import static com.sticker_android.utils.helper.PermissionManager.Constant.READ_STORAGE_ACCESS_RQ;
import static com.sticker_android.utils.helper.PermissionManager.Constant.WRITE_STORAGE_ACCESS_RQ;


public class AddNewDesignActivity extends AppBaseActivity implements View.OnClickListener {

    private final String TAG = AddNewDesignActivity.class.getSimpleName();
    private Toolbar toolbar;
    private AppPref appPref;
    private User userdata;
    private ViewPagerAdapter adapter;
    private TabLayout tabLayout;
    private Button btnPost;
    private ImageView imgCategoryDropDown;
    private RelativeLayout rlTabLayoutContainer;
    private CustomAppCompatTextView imgPlaceHolder;
    private EditText edtCorpName;
    private String mExpireDate = "";
    private SetDate setDate;
    private Spinner spnrCategory;
    private ArrayList<Category> corporateCategories = new ArrayList<>();

    private final int PROFILE_CAMERA_IMAGE = 0;
    private final int PROFILE_GALLERY_IMAGE = 1;
    private final int PROFILE_GALLERY_IMAGE_GIF = 2;
    private ImageView imvProductImage;
    private RelativeLayout rlJustificationHolder;
    private TextView txtViewMoreComment, txtRecentComments, edtJustification;
    private String mCapturedImageUrl;
    private android.app.AlertDialog mPermissionDialog;

    private Product mProduct;
    private boolean isDesignedImageChanges;
    private boolean comingFromDetailActivity;
    private File compressedImageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_design);
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

        setImageHeight();
        getIntentValues();
        changeStatusBarColor(getResources().getColor(R.color.colorstatusBarDesigner));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setSelectedTabIndicatorColor(Color.TRANSPARENT);

        txtViewMoreComment.setTextColor(ContextCompat.getColor(this, R.color.colorDesignerText));

        ArrayList<Category> categoryList = appPref.getCategoryList();

        final Category placeHolderCategory = new Category(-1, getString(R.string.select_category_txt));

        if (categoryList != null && categoryList.size() != 0) {
            corporateCategories = categoryList;
            corporateCategories.add(0, placeHolderCategory);
            setSpinnerAdapter(getActivity(), spnrCategory, corporateCategories);

            setDetailOfDesignedItem();
        } else {

            fetchCategoryApi(new CategoryDataListener() {

                @Override
                public void onCategoryDataRetrieved(ArrayList<Category> categories) {
                    corporateCategories = categories;
                    corporateCategories.add(0, placeHolderCategory);
                    setSpinnerAdapter(getActivity(), spnrCategory, corporateCategories);

                    setDetailOfDesignedItem();
                }

                @Override
                public void onFailure() {

                }
            });
        }
        if (mProduct != null)
            manageStatus();
    }

    private void manageStatus() {


        if (mProduct.productStatus == 3) {
            rlJustificationHolder.setVisibility(View.VISIBLE);
            setAdminCommentData();
            btnPost.setText(getResources().getString(R.string.txt_resubmit));
            imvProductImage.setOnClickListener(this);
        } else {
            rlJustificationHolder.setVisibility(View.GONE);
            btnPost.setText(R.string.act_txt_update_add_new_design);
            imvProductImage.setOnClickListener(null);
        }
    }

    private void setAdminCommentData() {
        if (mProduct.rejectionList != null) {
            if (mProduct.rejectionList.size() > 0) {

                txtRecentComments.setText("" + mProduct.rejectionList.get(mProduct.rejectionList.size() - 1).description);
            }
            if (mProduct.rejectionList.size() > 1) {
                txtViewMoreComment.setVisibility(View.VISIBLE);
                txtViewMoreComment.setTextColor(getResources().getColor(R.color.colorDesignerText));
            } else {
                txtViewMoreComment.setVisibility(View.GONE);

            }
        }
    }


    private void setDetailOfDesignedItem() {

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                setProductDetail();
            }
        });
    }

    private void getIntentValues() {
        Intent intent = getIntent();
        if (intent != null) {
            mProduct = intent.getParcelableExtra(AppConstant.PRODUCT);
            comingFromDetailActivity = intent.getBooleanExtra(AppConstant.DATA_REFRESH_NEEDED, false);
        }
    }

    /**
     * will set the product detail
     */
    private void setProductDetail() {

        if (mProduct != null) {

            edtCorpName.setText(mProduct.getProductname());
            edtCorpName.setSelection(edtCorpName.getText().toString().trim().length());
            Category category = new Category();
            category.categoryId = mProduct.getCategoryId();
            int categoryIndex = corporateCategories.indexOf(category);
            AppLogger.error(TAG, "Category index => " + categoryIndex);

            if (categoryIndex != -1) {
                spnrCategory.setSelection(categoryIndex);
            }
            btnPost.setText(R.string.update);
            imgPlaceHolder.setVisibility(View.GONE);
            tabLayout.setVisibility(View.GONE);
            rlTabLayoutContainer.setVisibility(View.GONE);

            setToolBarTitle();

            Glide.with(this)
                    .load(mProduct.getImagePath())
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(imvProductImage);
        }
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

    private void fetchCategoryApi(final CategoryDataListener categoryDataListener) {

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
        if (mProduct != null) {
            textView.setText(getString(R.string.edit));
        } else {
            textView.setText(getResources().getString(R.string.act_corp_txt_add_new));
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
        tabLayout.addOnTabSelectedListener(new TabListeners());
        btnPost.setOnClickListener(this);
        imvProductImage.setOnClickListener(this);
        imgCategoryDropDown.setOnClickListener(this);
        txtViewMoreComment.setOnClickListener(this);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    imgPlaceHolder.setText(getString(R.string.upload_sticker));
                } else if (tab.getPosition() == 1) {
                    imgPlaceHolder.setText(R.string.upload_gif);
                } else if (tab.getPosition() == 2) {
                    imgPlaceHolder.setText(R.string.upload_emoji);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setSelectedTabColor() {
        tabLayout.setTabTextColors(Color.parseColor("#AAFFFFFF"), Color.WHITE);
    }

    @Override
    protected void setViewReferences() {
        imgCategoryDropDown = (ImageView) findViewById(R.id.imgDown2);
        edtCorpName = (EditText) findViewById(R.id.act_add_new_corp_edt_name);
        tabLayout = (TabLayout) findViewById(R.id.act_landing_tab);
        btnPost = (Button) findViewById(R.id.act_corp_add_new_btn_post);
        spnrCategory = (Spinner) findViewById(R.id.spnrCategory);
        imvProductImage = (ImageView) findViewById(R.id.imvProductImage);
        imgPlaceHolder = (CustomAppCompatTextView) findViewById(R.id.imgPlaceHolder);
        rlTabLayoutContainer = (RelativeLayout) findViewById(R.id.rlTabLayoutContainer);
        rlJustificationHolder = (RelativeLayout) findViewById(R.id.rlJustificationHolder);
        edtJustification = (TextView) findViewById(R.id.edtJustification);
        txtViewMoreComment = (TextView) findViewById(R.id.txtViewMoreComment);
        txtRecentComments = (TextView) findViewById(R.id.txtRecentComments);
    }

    @Override
    protected boolean isValidData() {

        if (mProduct == null && mCapturedImageUrl == null) {
            Utils.showToast(this, getString(R.string.pls_upload_image));
            return false;
        } else if (edtCorpName.getText().toString().trim().isEmpty()) {
            Utils.showToast(this, getString(R.string.pls_enter_name));
            return false;
        } else if (spnrCategory.getSelectedItemPosition() == 0) {
            Utils.showToast(this, getString(R.string.pls_select_category));
            return false;
        }
        return true;
    }

    private void setBackground() {
        tabLayout.setBackground(getResources().getDrawable(R.drawable.side_nav_designer));
    }

    public void addTabsDynamically() {

        TabLayout.Tab stickerTab = tabLayout.newTab();
        stickerTab.setText(getString(R.string.stickers)); // set the Text for the first Tab
        tabLayout.addTab(stickerTab);

        TabLayout.Tab gifTab = tabLayout.newTab();
        gifTab.setText(getString(R.string.gif)); // set the Text for the first Tab
        tabLayout.addTab(gifTab);

        TabLayout.Tab emojiTab = tabLayout.newTab();
        emojiTab.setText(getString(R.string.emoji)); // set the Text for the first Tab
        tabLayout.addTab(emojiTab);

        Utils.setTabLayoutDivider(tabLayout, this);
    }

    @Override
    public void onClick(View v) {
        Utils.hideKeyboard(this);
        switch (v.getId()) {
            case R.id.act_corp_add_new_btn_post:
                if (isValidData()) {
                    if (mProduct != null) {
                        if (mProduct.productStatus == 3) {
                            if (isDesignedImageChanges)
                                beginUpload(mCapturedImageUrl);
                            else
                                resubmitApiCall(mProduct.getImagePath());
                        } else {
                            if (isDesignedImageChanges) {
                                beginUpload(mCapturedImageUrl);
                            } else {
                                addDesignApi(mProduct.getImagePath());
                            }
                        }

                    } else {
                        beginUpload(mCapturedImageUrl);
                    }
                }
                break;
            case R.id.imvProductImage:
                if (getDesignType().equalsIgnoreCase(DesignType.gif.getType())) {
                    Utils.showAlertDialogToGetGif(this, new ImagePickerListener() {
                        @Override
                        public void pickFromGallery() {
                            pickGifFromGalleryImage();
                        }

                        @Override
                        public void captureFromCamera() {

                        }

                        @Override
                        public void selectedItemPosition(int position) {

                        }
                    });
                } else {

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
                }
                break;
            case R.id.imgDown2:
                spnrCategory.performClick();
                break;
            case R.id.txtViewMoreComment:
                Intent intent = new Intent(getActivity(), CommentsActivity.class);
                Bundle bundle = new Bundle();

                bundle.putParcelable(AppConstant.PRODUCT_OBJ_COMMENTS, mProduct);
                intent.putExtras(bundle);
                startActivityForResult(intent, 101);
                getActivity().overridePendingTransition(R.anim.activity_animation_enter,
                        R.anim.activity_animation_exit);
                break;
        }
    }

    private String getDesignType() {
        if (mProduct != null) {
            return mProduct.getType();
        } else {
            return getSelectedDesignType();
        }
    }


    private void resubmitApiCall(String imagePath) {
        int categoryId = corporateCategories.get(spnrCategory.getSelectedItemPosition()).categoryId;

        final ProgressDialogHandler progressDialogHandler = new ProgressDialogHandler(this);
        progressDialogHandler.show();
        final String type = mProduct != null ? mProduct.getType() : getSelectedDesignType().toLowerCase(Locale.ENGLISH);
        Call<ApiResponse> apiResponseCall = RestClient.getService().apiSaveProjectRejection(userdata.getLanguageId(), userdata.getAuthrizedKey(),
                userdata.getId(), edtCorpName.getText().toString().trim(), type, ""
                , mExpireDate, imagePath, String.valueOf(mProduct.getProductid()), edtJustification.getText().toString().trim(), categoryId, "");

        apiResponseCall.enqueue(new ApiCall(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.status) {
                    progressDialogHandler.hide();
                    if (apiResponse.status) {
                        if (mProduct != null) {
                            Utils.showToast(getApplicationContext(), getString(R.string.txt_data_updated_successfully));
                        } else {

                            if (type.equalsIgnoreCase("stickers")) {
                                Utils.showToast(getApplicationContext(), getResources().getString(R.string.stickers) + " " + getString(R.string.txt_added_successfully));

                            } else if (type.equalsIgnoreCase("emoji")) {
                                Utils.showToast(getApplicationContext(), getResources().getString(R.string.emoji) + " " + getString(R.string.txt_added_successfully));

                            } else {
                                Utils.showToast(getApplicationContext(), getResources().getString(R.string.gif) + " " + getString(R.string.txt_added_successfully));

                            }
                        }


                        if (comingFromDetailActivity) {
                            Intent intent = new Intent(AddNewDesignActivity.this, DesignerHomeActivity.class);
                            intent.putExtra(AppConstant.DATA_REFRESH_NEEDED, true);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent();
                            intent.putExtra(AppConstant.PRODUCT, apiResponse.paylpad.product);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                }

            }

            @Override
            public void onFail(Call<ApiResponse> call, Throwable t) {
                progressDialogHandler.hide();
            }
        });

    }


    private void captureImage() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = Utils.getCustomImagePath(this, System.currentTimeMillis() + "");
        mCapturedImageUrl = file.getAbsolutePath();
        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(takePicture, PROFILE_CAMERA_IMAGE);
    }

    private void pickGalleryImage() {
        Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(openGallery, PROFILE_GALLERY_IMAGE);
    }

    private void pickGifFromGalleryImage() {
        String[] mimeTypes = {"image/gif"};
        Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        openGallery.setType("image/*");
        openGallery.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(openGallery, PROFILE_GALLERY_IMAGE_GIF);
    }

    /**
     * Method is used to call the add ads or product api
     *
     * @param imagePath
     */
    private void addDesignApi(String imagePath) {

        int categoryId = corporateCategories.get(spnrCategory.getSelectedItemPosition()).categoryId;

        final ProgressDialogHandler progressDialogHandler = new ProgressDialogHandler(this);
        progressDialogHandler.show();
        final String type = mProduct != null ? mProduct.getType() : getSelectedDesignType().toLowerCase(Locale.ENGLISH);
        Call<ApiResponse> apiResponseCall = RestClient.getService().apiAddProduct(userdata.getLanguageId(), userdata.getAuthrizedKey(),
                userdata.getId(), edtCorpName.getText().toString().trim(), type, ""
                , mExpireDate, imagePath, mProduct != null ? String.valueOf(mProduct.getProductid()) : "", categoryId, AppConstant.PRODUCT);

        apiResponseCall.enqueue(new ApiCall(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                progressDialogHandler.hide();
                if (apiResponse.status) {
                    if (mProduct != null) {
                        if (type.equalsIgnoreCase("stickers")) {
                            Utils.showToast(getApplicationContext(), getResources().getString(R.string.stickers) + " " + getString(R.string.txt_data_updated_successfully));

                        } else if (type.equalsIgnoreCase("emoji")) {
                            Utils.showToast(getApplicationContext(), getResources().getString(R.string.emoji) + " " + getString(R.string.txt_data_updated_successfully));

                        } else {
                            Utils.showToast(getApplicationContext(), getResources().getString(R.string.gif) + " " + getString(R.string.txt_data_updated_successfully));

                        }
                        //Utils.showToast(getApplicationContext(), Utils.capitlizeText(type) + " updated successfully.");
                    } else {
                        if (type.equalsIgnoreCase("stickers")) {
                            Utils.showToast(getApplicationContext(), getResources().getString(R.string.stickers) + " " + getString(R.string.txt_added_successfully));

                        } else if (type.equalsIgnoreCase("emoji")) {
                            Utils.showToast(getApplicationContext(), getResources().getString(R.string.emoji) + " " + getString(R.string.txt_added_successfully));

                        } else {
                            Utils.showToast(getApplicationContext(), getResources().getString(R.string.gif) + " " + getString(R.string.txt_added_successfully));

                        }
                        //  Utils.showToast(getApplicationContext(), Utils.capitlizeText(type) + " added successfully.");
                    }


                    if (comingFromDetailActivity) {
                        Intent intent = new Intent(AddNewDesignActivity.this, DesignerHomeActivity.class);
                        intent.putExtra(AppConstant.DATA_REFRESH_NEEDED, true);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra(AppConstant.PRODUCT, apiResponse.paylpad.product);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                } else {
                    Utils.showToast(getApplicationContext(), "" + apiResponse.error.message);

                }
            }

            @Override
            public void onFail(Call<ApiResponse> call, Throwable t) {
                progressDialogHandler.hide();
            }
        });
    }

    /**
     * Method is used to get the type of posted design
     *
     * @return rerurns the type
     */
    public String getSelectedDesignType() {
        DesignType designType = DesignType.stickers;

        if (tabLayout.getSelectedTabPosition() == 0) {
            designType = DesignType.stickers;
        } else if (tabLayout.getSelectedTabPosition() == 1) {
            designType = DesignType.gif;
        } else if (tabLayout.getSelectedTabPosition() == 2) {
            designType = DesignType.emoji;
        }
        return designType.getType();
    }

    public class TabListeners implements TabLayout.OnTabSelectedListener {

        public TabListeners() {

        }

        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            mCapturedImageUrl = null;
            imgPlaceHolder.setVisibility(View.VISIBLE);
            imvProductImage.setImageResource(R.color.image_background_color);
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
                    ImageFileFilter imageFileFilter = new ImageFileFilter();
                    if (imageFileFilter.accept(file)) {
                        AppLogger.debug("Image filter", "filter image gif");
                        Glide.with(this).load(mCapturedImageUrl).asGif()
                                .into(imvProductImage);
                        imgPlaceHolder.setVisibility(View.GONE);

                    } else {
                        AppLogger.debug("Image filter", "filter image crop");
                        openCropActivity(sourceUrl);
                    }

                }
                break;
            case PROFILE_GALLERY_IMAGE_GIF:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = data.getData();
                    String sourceUrl = Utils.getGalleryImagePath(this, selectedImage);
                    if (checkImage(sourceUrl)) {
                        File file = Utils.getCustomImagePath(this, "temp");
                        mCapturedImageUrl = file.getAbsolutePath();
                        mCapturedImageUrl = sourceUrl;
                        Glide.with(this).load(mCapturedImageUrl).asGif()
                                .into(imvProductImage);
                        imgPlaceHolder.setVisibility(View.GONE);
                    } else {
                        mCapturedImageUrl = null;
                        Utils.showToast(this, getString(R.string.txt_please_select_a_valid_gif));
                    }

                }
                break;
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    try {
/*     File file = new File(getRealPathFromURI(resultUri));
                   */
                        AppLogger.debug(TAG, "Size is befor compress " + resultUri.getPath().length());
                        File file = new File(resultUri.getPath());
                        AppLogger.debug(TAG, "Size is before compress in unit " + Utils.getFileSize(file));
                        compressedImageFile = new Compressor(this).compressToFile(file);
                        AppLogger.debug(TAG, "Size is after compress " + Integer.parseInt(String.valueOf(compressedImageFile.length() / 1024)));
                        AppLogger.debug(TAG, "Size is after compress in unit " + Utils.getFileSize(compressedImageFile));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mCapturedImageUrl = compressedImageFile.getAbsolutePath();

                    //mCapturedImageUrl = resultUri.getPath();
                    imageLoader.displayImage(resultUri.toString(), imvProductImage, displayImageOptions);
                    imgPlaceHolder.setVisibility(View.GONE);
                    isDesignedImageChanges = true;
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    error.printStackTrace();
                }
        }
    }

    private boolean checkImage(String absolutePath) {
        boolean isGif = false;
        String type = "";
        try {
            //filePath is a String converted from a selected image's URI
            File file = new File(absolutePath);
            FileInputStream fileInputStream = new FileInputStream(file);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int len = 0;

            while ((len = fileInputStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }

            fileInputStream.close();
            byte[] bytes = outStream.toByteArray();

            Movie gif = Movie.decodeByteArray(bytes, 0, bytes.length);
            //If the result is true, its a animated GIF
            if (gif != null) {
                type = "Animated";
                isGif = true;
                Log.d("Test", "Animated: " + type);
            } else {
                type = "notAnimated";
                Log.d("Test", "Animated: " + type);
            }
        } catch (IOException ie) {
            ie.printStackTrace();
        }
        return isGif;
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
            Toast.makeText(this, R.string.txt_could_not_find_file_path,
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
                    addDesignApi(imagePath);
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
                Utils.showToast(AddNewDesignActivity.this, "Please upload again.");
            }
        });
    }
}
