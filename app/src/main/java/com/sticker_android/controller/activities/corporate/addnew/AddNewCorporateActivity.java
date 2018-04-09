package com.sticker_android.controller.activities.corporate.addnew;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
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
import com.sticker_android.model.corporateproduct.CorporateCategory;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.AWSUtil;
import com.sticker_android.utils.ProgressDialogHandler;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.sharedpref.AppPref;
import com.sticker_android.view.CategoryAdapter;
import com.sticker_android.view.NothingSelectedSpinnerAdapter;
import com.sticker_android.view.SetDate;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;

import retrofit2.Call;

public class AddNewCorporateActivity extends AppBaseActivity implements View.OnClickListener {

    private final String TAG = AddNewCorporateActivity.class.getSimpleName();
    private Toolbar toolbar;
    private AppPref appPref;
    private User userdata;
    private ViewPagerAdapter adapter;
    private TabLayout tabLayout;
    private ImageView imgCorporateItem;
    private Button btnPost;
    private EditText edtExpireDate;
    private EditText edtCorpName, edtDescription;
    private String mExpireDate = "2018-04-06";
    private SetDate setDate;
    private Spinner spnrCategory;
    private ArrayList<CorporateCategory> corporateCategories = new ArrayList<>();

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
        setDate = new SetDate(edtExpireDate, this);
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
            CategoryAdapter categoryAdapter=new CategoryAdapter(this,corporateCategories);
          //  ArrayAdapter<CorporateCategory> adapter = new ArrayAdapter<CorporateCategory>(this, android.R.layout.simple_spinner_item, corporateCategories);
          //  adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnrCategory.setAdapter(
                    new NothingSelectedSpinnerAdapter(
                            categoryAdapter,
                            R.layout.spinner_without_selection,
                            this));
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
        imgCorporateItem.setOnClickListener(this);
    }

    private void setSelectedTabColor() {
        tabLayout.setTabTextColors(Color.parseColor("#AAFFFFFF"), Color.WHITE);
    }

    @Override
    protected void setViewReferences() {
        imgCorporateItem = (ImageView) findViewById(R.id.imgCorporateItem);
        edtCorpName = (EditText) findViewById(R.id.act_add_new_corp_edt_name);
        tabLayout = (TabLayout) findViewById(R.id.act_landing_tab);
        btnPost = (Button) findViewById(R.id.act_corp_add_new_btn_post);
        edtExpireDate = (EditText) findViewById(R.id.act_add_new_ad_corp_edt_expire_date);
        edtDescription = (EditText) findViewById(R.id.edtDescription);
        spnrCategory = (Spinner) findViewById(R.id.spnrCategory);
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
            Utils.showToast(this, "Please enter a description.");
            return false;
        }else if(spnrCategory.getSelectedItem().equals("Select a Category")){
            Utils.showToast(this, "Please select a category.");
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

    }

    @Override
    public void onClick(View v) {
        Utils.hideKeyboard(this);
        switch (v.getId()) {
            case R.id.act_corp_add_new_btn_post:
                if (isValidData()) {
                    addProductOrAdApi();
                }
                break;
            case R.id.imgCorporateItem:
                pickGalleryImage();
                break;
        }
    }

    /**
     * Method is used to call the add ads or product api
     */
    private void addProductOrAdApi() {
        int categoryId=corporateCategories.get(spnrCategory.getSelectedItemPosition()-1).categoryId;

        if (setDate != null)
            mExpireDate = setDate.getChosenDate();

        final ProgressDialogHandler progressDialogHandler = new ProgressDialogHandler(this);
        progressDialogHandler.show();
        final String type = getSelectedType();
        Call<ApiResponse> apiResponseCall = RestClient.getService().apiAddProduct(userdata.getLanguageId(), userdata.getAuthrizedKey(),
                userdata.getId(), edtCorpName.getText().toString().trim(), type, edtDescription.getText().toString().trim()
                , mExpireDate, "", "",categoryId);

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

    private void pickGalleryImage() {
        Intent openGallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(openGallery, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            String sourceUrl = Utils.getGalleryImagePath(getActivity(), selectedImage);
            beginUpload(sourceUrl);
        }
    }

    /*
     * Begins to upload the file specified by the file path.
     */
    private void beginUpload(String filePath) {
        if (filePath == null) {
            Toast.makeText(this, "Could not find the filepath of the selected file",
                    Toast.LENGTH_LONG).show();
            return;
        }
        File file = new File(filePath);
        TransferObserver observer = new AWSUtil().getTransferUtility(this).upload(AppConstant.BUCKET_NAME, file.getName(),
                file);
        /*
         * Note that usually we set the transfer listener after initializing the
         * transfer. However it isn't required in this sample app. The flow is
         * click upload button -> start an activity for image selection
         * startActivityForResult -> onActivityResult -> beginUpload -> onResume
         * -> set listeners to in progress transfers.
         */
        observer.setTransferListener(new TransferListener(){

            @Override
            public void onStateChanged(int id, TransferState state) {
                Log.d(TAG, "onStateChanged: " + id + ", " + state);
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                Log.d(TAG, String.format("onProgressChanged: %d, total: %d, current: %d",
                        id, bytesTotal, bytesCurrent));
            }

            @Override
            public void onError(int id, Exception ex) {
                Log.e(TAG, "Error during upload: " + id, ex);
            }
        });
    }

    /*
     * Gets the file path of the given Uri.
     */
    @SuppressLint("NewApi")
    private String getPath(Uri uri) throws URISyntaxException {
        final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        // deal with different Uris.
        if (needToCheckUri && DocumentsContract.isDocumentUri(getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[] {
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                cursor = getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
