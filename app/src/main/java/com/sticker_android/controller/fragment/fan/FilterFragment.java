package com.sticker_android.controller.fragment.fan;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sticker_android.R;
import com.sticker_android.constant.AppConstant;
import com.sticker_android.controller.activities.designer.addnew.AddNewDesignActivity;
import com.sticker_android.controller.activities.designer.home.DesignerHomeActivity;
import com.sticker_android.controller.activities.fan.home.EditImageActivity;
import com.sticker_android.controller.activities.fan.home.FanHomeActivity;
import com.sticker_android.controller.adaptors.DesignListAdapter;
import com.sticker_android.controller.fragment.designer.DesignerHomeFragment;
import com.sticker_android.model.User;
import com.sticker_android.model.corporateproduct.Product;
import com.sticker_android.model.enums.DesignType;
import com.sticker_android.model.interfaces.DesignerActionListener;
import com.sticker_android.model.interfaces.ImagePickerListener;
import com.sticker_android.model.interfaces.MessageEventListener;
import com.sticker_android.model.interfaces.NetworkPopupEventListener;
import com.sticker_android.model.payload.Payload;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.AppLogger;
import com.sticker_android.utils.ImageFileFilter;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.helper.PaginationScrollListener;
import com.sticker_android.utils.sharedpref.AppPref;
import com.sticker_android.view.StickerView;
import com.sticker_android.view.imagezoom.ImageViewTouch;
import com.sticker_android.view.imagezoom.ImageViewTouchBase;
import com.sticker_android.view.imagezoom.easing.Linear;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;

import static android.app.Activity.RESULT_OK;

/**
 * Created by satyendra on 4/9/18.
 */

public class FilterFragment extends Fragment implements View.OnClickListener{

    private RelativeLayout rlContent;
    private LinearLayout llLoaderView;
    private RelativeLayout rlConnectionContainer;
    private TextView txtNoDataFoundTitle, txtNoDataFoundContent;

    private final String TAG = FilterFragment.class.getSimpleName();
    private Context mContext;
    private Activity mHostActivity;

    private View inflatedView;
    private User mLoggedUser;
    public Bitmap mainBitmap;
    private Bitmap originalBitmap;
    public ImageViewTouch mainImage;
    private RelativeLayout rlImageContainer, rlPlaceHolderClick;
    private LinearLayout rlFilterOptionContainer, llFilter, llSticker, llEmoji;
    private Button btnReset, btnSave;

    private SaveImageTask mSaveImageTask;
    private LoadImageTask mLoadImageTask;
    private StickerView mStickerView;

    public String filePath;
    public String saveFilePath;
    private int imageWidth, imageHeight;

    private final int PROFILE_CAMERA_IMAGE = 0;
    private final int PROFILE_GALLERY_IMAGE = 1;
    private String mCapturedImageUrl;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mHostActivity = (FanHomeActivity) context;
        mLoggedUser = new AppPref(mContext).getUserInfo();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        AppLogger.error(TAG, "Inside onCreateView() method");

        if (inflatedView == null) {
            inflatedView = LayoutInflater.from(mContext).inflate(R.layout.layout_apply_filter, container, false);

            setViewReferences();
            setListenerOnViews();

            /*getFilterFromServer(false, "");*/

            //loadImage(null);

        } else {
            if (inflatedView.getParent() != null)
                ((ViewGroup) inflatedView.getParent()).removeView(inflatedView);
        }

        rlImageContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rlImageContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width = rlImageContainer.getWidth();
                int height = rlImageContainer.getHeight();

                int size = Math.max(width, height);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rlImageContainer.getLayoutParams();
                params.width = size;
                params.height = size;
                rlImageContainer.setLayoutParams(params);
            }
        });

        AppLogger.debug(TAG, "Outside onCreateView() method");
        return inflatedView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void setViewReferences() {
        rlContent = (RelativeLayout) inflatedView.findViewById(R.id.rlContent);
        txtNoDataFoundTitle = (TextView) inflatedView.findViewById(R.id.txtNoDataFoundTitle);
        txtNoDataFoundContent = (TextView) inflatedView.findViewById(R.id.txtNoDataFoundContent);
        rlConnectionContainer = (RelativeLayout) inflatedView.findViewById(R.id.rlConnectionContainer);
        llLoaderView = (LinearLayout) inflatedView.findViewById(R.id.llLoader);
        mStickerView = (StickerView) inflatedView.findViewById(R.id.sticker_panel);
        mainImage = (ImageViewTouch) inflatedView.findViewById(R.id.main_image);
        rlImageContainer = (RelativeLayout) inflatedView.findViewById(R.id.rlImageContainer);
        rlFilterOptionContainer = (LinearLayout) inflatedView.findViewById(R.id.rlFilterOptionContainer);
        llSticker = (LinearLayout) inflatedView.findViewById(R.id.llSticker);
        llFilter = (LinearLayout) inflatedView.findViewById(R.id.llFilter);
        llEmoji = (LinearLayout) inflatedView.findViewById(R.id.llEmoji);
        btnReset = (Button) inflatedView.findViewById(R.id.btnReset);
        btnSave = (Button) inflatedView.findViewById(R.id.btnSave);
    }

    public void setListenerOnViews() {
        llFilter.setOnClickListener(this);
        llSticker.setOnClickListener(this);
        llEmoji.setOnClickListener(this);
        btnReset.setOnClickListener(this);
        btnSave.setOnClickListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AppLogger.error(TAG, "Inside onActivityResult()");

        switch (requestCode) {

            case PROFILE_CAMERA_IMAGE:
                if (resultCode == RESULT_OK) {
                    if (mCapturedImageUrl != null) {
                        openCropActivity(mCapturedImageUrl);
                    }
                }
                break;

            case PROFILE_GALLERY_IMAGE:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    String sourceUrl = Utils.getGalleryImagePath(mHostActivity, selectedImage);
                    File file = Utils.getCustomImagePath(mHostActivity, "temp");
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
                    rlImageContainer.setVisibility(View.GONE);
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
                .start(mHostActivity);
    }

    private void setSelectedStickerItem(Bitmap bitmap){
        mStickerView.addBitImage(bitmap);
    }

    /**
     * Load the image from filepath into mainImage imageView.
     * @param filepath The image to be loaded.
     */
    public void loadImage(String filepath) {
        if (mLoadImageTask != null) {
            mLoadImageTask.cancel(true);
        }
        mLoadImageTask = new LoadImageTask();
        mLoadImageTask.execute(filepath);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnReset:

                break;
            case R.id.btnSave:

                break;
            case R.id.llFilter:

                break;
            case R.id.llSticker:

                break;
            case R.id.llEmoji:

                break;
            case R.id.rlPlaceHolderClick:
                Utils.showAlertDialogToGetPicFromFragment(mHostActivity, new ImagePickerListener() {
                    @Override
                    public void pickFromGallery() {
                        pickGalleryImage();
                    }

                    @Override
                    public void captureFromCamera() {
                        captureImage();
                    }
                }, this);
                break;
        }
    }

    private void pickGalleryImage() {
        Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(openGallery, PROFILE_GALLERY_IMAGE);
    }

    private void captureImage() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = Utils.getCustomImagePath(mHostActivity, System.currentTimeMillis() + "");
        mCapturedImageUrl = file.getAbsolutePath();
        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(takePicture, PROFILE_CAMERA_IMAGE);
    }

    private final class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {

            /*return BitmapUtils.getSampledBitmap(params[0], imageWidth,
                    imageHeight);*/
            return BitmapFactory.decodeResource(getResources(),
                    R.drawable.gradient_bg_des_hdpi);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if (mainBitmap != null) {
                mainBitmap.recycle();
                mainBitmap = null;
                System.gc();
            }
            mainBitmap = result;
            mainImage.setImageBitmap(result);
            mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
            originalBitmap = mainBitmap.copy(mainBitmap.getConfig(), true);
            mStickerView.mainBitmap = mainBitmap;
            mStickerView.mainImage = mainImage;
            mStickerView.setVisibility(View.VISIBLE);

            mStickerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mStickerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    setSelectedStickerItem(BitmapFactory.decodeResource(getResources(),
                            R.drawable.forgot_password_hdpi));
                }
            });
        }
    }

    private final class SaveImageTask extends AsyncTask<Bitmap, Void, Boolean> {
        private Dialog dialog;

        @Override
        protected Boolean doInBackground(Bitmap... params) {
            if (TextUtils.isEmpty(saveFilePath))
                return false;
            //return BitmapUtils.saveBitmap(params[0], saveFilePath);
            return false;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            dialog.dismiss();
        }

        @Override
        protected void onCancelled(Boolean result) {
            super.onCancelled(result);
            dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = getLoadingDialog(mContext, "Saving image", false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            dialog.dismiss();

            if (result) {
                //onSaveTaskDone();
            } else {
                //SnackBarHandler.show(parentLayout,R.string.save_error);
            }
        }
    }

    public static Dialog getLoadingDialog(Context context, String title, boolean canCancel) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setCancelable(canCancel);
        dialog.setMessage(title);
        return dialog;
    }

    /*private void getFilterFromServer(final boolean isRefreshing, final String searchKeyword) {

        //remove wi-fi symbol when response got
        if (rlConnectionContainer != null && rlConnectionContainer.getChildCount() > 0) {
            rlConnectionContainer.removeAllViews();
        }

        if (mCurrentPage == 0 && !isRefreshing) {
            llLoaderView.setVisibility(View.VISIBLE);
        }

        llNoDataFound.setVisibility(View.GONE);
        int index = 0;
        int limit = PAGE_LIMIT;

        if (isRefreshing) {
            index = 0;
        } else if (mCurrentPage != -1) {
            index = mCurrentPage * PAGE_LIMIT;
        }

        if (PAGE_LIMIT != -1) {
            limit = PAGE_LIMIT;
        }

        Call<ApiResponse> apiResponseCall = RestClient.getService().apiGetProductList(mLoggedUser.getLanguageId(), "", mLoggedUser.getId(),
                index, limit, DesignType.stickers.getType().toLowerCase(Locale.ENGLISH), "product_list", searchKeyword);
        apiResponseCall.enqueue(new ApiCall(getActivity(), 1) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {

                if (isAdded() && getActivity() != null) {
                    llLoaderView.setVisibility(View.GONE);
                    rlContent.setVisibility(View.VISIBLE);
                    swipeRefresh.setRefreshing(false);

                    //remove wi-fi symbol when response got
                    if (rlConnectionContainer != null && rlConnectionContainer.getChildCount() > 0) {
                        rlConnectionContainer.removeAllViews();
                    }

                    try {
                        if (apiResponse.status) {
                            Payload payload = apiResponse.paylpad;

                            if (payload != null) {

                                if (isRefreshing) {

                                    if (payload.productList != null && payload.productList.size() != 0) {
                                        mStickerList.clear();
                                        mStickerList.addAll(payload.productList);

                                        llNoDataFound.setVisibility(View.GONE);
                                        rcDesignList.setVisibility(View.VISIBLE);
                                        mAdapter.setData(mStickerList);

                                        mCurrentPage = 0;
                                        mCurrentPage++;
                                    } else {
                                        mStickerList.clear();
                                        mAdapter.setData(mStickerList);
                                        if(searchKeyword.length() != 0){
                                            txtNoDataFoundContent.setText(getString(R.string.no_search_found));
                                        }
                                        else{
                                            txtNoDataFoundContent.setText(R.string.no_stickers_uploaded_yet);
                                        }
                                        showNoDataFound();
                                    }
                                } else {

                                    if (mCurrentPage == 0) {
                                        mStickerList.clear();
                                        if(payload.productList != null){
                                            mStickerList.addAll(payload.productList);
                                        }

                                        if (mStickerList.size() != 0) {
                                            llNoDataFound.setVisibility(View.GONE);
                                            rcDesignList.setVisibility(View.VISIBLE);
                                            mAdapter.setData(mStickerList);
                                        } else {
                                            showNoDataFound();
                                            if(searchKeyword.length() != 0){
                                                txtNoDataFoundContent.setText(getString(R.string.no_search_found));
                                            }
                                            else{
                                                txtNoDataFoundContent.setText(R.string.no_stickers_uploaded_yet);
                                            }
                                            rcDesignList.setVisibility(View.GONE);
                                        }
                                    } else {
                                        AppLogger.error(TAG, "Remove loader...");
                                        mAdapter.removeLoader();
                                        if (payload.productList != null && payload.productList.size() != 0) {
                                            mStickerList.addAll(payload.productList);
                                            mAdapter.setData(mStickerList);
                                        }
                                    }

                                    if (payload.productList != null && payload.productList.size() != 0) {
                                        mCurrentPage++;
                                    }
                                }
                                AppLogger.error(TAG, "item list size => " + mStickerList.size());

                            } else if (mStickerList == null || (mStickerList != null && mStickerList.size() == 0)) {
                                if(searchKeyword.length() != 0){
                                    txtNoDataFoundContent.setText(getString(R.string.no_search_found));
                                }
                                else{
                                    txtNoDataFoundContent.setText(R.string.no_stickers_uploaded_yet);
                                }
                                showNoDataFound();
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Utils.showAlertMessage(mContext, new MessageEventListener() {
                            @Override
                            public void onOkClickListener(int reqCode) {

                            }
                        }, getString(R.string.server_unreachable), getString(R.string.oops), 0);
                    }
                }

            }

            @Override
            public void onFail(final Call<ApiResponse> call, Throwable t) {

                if (isAdded() && getActivity() != null) {
                    llLoaderView.setVisibility(View.GONE);
                    mAdapter.removeLoader();
                    swipeRefresh.setRefreshing(false);

                    if (mCurrentPage == 0) {
                        rlContent.setVisibility(View.GONE);
                    } else {
                        rlContent.setVisibility(View.VISIBLE);
                    }
                    if (!call.isCanceled() && (t instanceof java.net.ConnectException ||
                            t instanceof java.net.SocketTimeoutException ||
                            t instanceof java.net.SocketException ||
                            t instanceof java.net.UnknownHostException)) {

                        if (mCurrentPage == 0) {
                            mHostActivity.manageNoInternetConnectionLayout(mContext, rlConnectionContainer, new NetworkPopupEventListener() {
                                @Override
                                public void onOkClickListener(int reqCode) {
                                    rlContent.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onRetryClickListener(int reqCode) {
                                    getDesignFromServer(isRefreshing, searchKeyword);
                                }
                            }, 0);
                        } else {
                            Utils.showToastMessage(mHostActivity, getString(R.string.pls_check_ur_internet_connection));
                        }
                    }
                }
            }
        });
    }

    private void showNoDataFound() {
        llNoDataFound.setVisibility(View.VISIBLE);
        txtNoDataFoundTitle.setText("");
    }*/
}
