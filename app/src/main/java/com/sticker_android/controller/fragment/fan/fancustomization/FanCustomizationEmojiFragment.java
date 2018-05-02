package com.sticker_android.controller.fragment.fan.fancustomization;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sticker_android.R;
import com.sticker_android.controller.activities.fan.home.EditImageActivity;
import com.sticker_android.controller.activities.fan.home.FanHomeActivity;
import com.sticker_android.controller.adaptors.FanDownloadListAdaptor;
import com.sticker_android.controller.fragment.base.BaseFragment;
import com.sticker_android.controller.fragment.fan.FilterFragment;
import com.sticker_android.controller.fragment.fan.fanhome.FanHomeStickerFragment;
import com.sticker_android.model.User;
import com.sticker_android.model.contest.FanContestDownload;
import com.sticker_android.model.corporateproduct.Product;
import com.sticker_android.model.enums.DesignType;
import com.sticker_android.model.interfaces.ImagePickerListener;
import com.sticker_android.model.interfaces.MessageEventListener;
import com.sticker_android.model.interfaces.NetworkPopupEventListener;
import com.sticker_android.model.payload.Payload;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.AppLogger;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.helper.PaginationScrollListener;
import com.sticker_android.utils.helper.PermissionManager;
import com.sticker_android.utils.sharedpref.AppPref;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;

import static android.app.Activity.RESULT_OK;
import static com.sticker_android.utils.helper.PermissionManager.Constant.WRITE_STORAGE_ACCESS_RQ;

/**
 * Created by user on 30/4/18.
 */

public class FanCustomizationEmojiFragment  extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener{
    private RecyclerView rcDesignList;
    private LinearLayout llNoDataFound;
    private SwipeRefreshLayout swipeRefresh;
    private RelativeLayout rlContent;
    private LinearLayout llLoaderView;
    private RelativeLayout rlConnectionContainer;
    private TextView txtNoDataFoundTitle, txtNoDataFoundContent;

    private final String TAG = FanHomeStickerFragment.class.getSimpleName();
    private Context mContext;
    private FanHomeActivity mHostActivity;

    private View inflatedView;
    private LinearLayoutManager mLinearLayoutManager;
    private ArrayList<FanContestDownload> mEmojiList;
    private User mLoggedUser;
    private android.app.AlertDialog mPermissionDialog;

    private int mCurrentPage = 0;
    private int PAGE_LIMIT;
    private FanDownloadListAdaptor mAdapter;

    private final int PROFILE_CAMERA_IMAGE = 0;
    private final int PROFILE_GALLERY_IMAGE = 1;
    private int mImageSource = -1;
    private String mCapturedImageUrl;
    private Product mSelectedItem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.layout_design_item_list, container, false);
        init();
        PAGE_LIMIT = getActivity().getResources().getInteger(R.integer.designed_item_page_limit);
        setViewReferences(view);
        setViewListeners();
        initRecyclerView();
        mAdapter = new FanDownloadListAdaptor(getActivity());
        mAdapter.setOnProductClickListener(new FanDownloadListAdaptor.OnProductItemClickListener() {
            @Override
            public void onProductItemClick(Product product) {
                mSelectedItem = product;
                Utils.showAlertDialogToGetPicFromFragment(mHostActivity, new ImagePickerListener() {
                    @Override
                    public void pickFromGallery() {
                        pickGalleryImage();
                    }

                    @Override
                    public void captureFromCamera() {
                        captureImage();
                    }

                    @Override
                    public void selectedItemPosition(int position) {
                        if(position == 0){
                            mImageSource = PROFILE_CAMERA_IMAGE;
                        }
                        else if(position == 1){
                            mImageSource = PROFILE_GALLERY_IMAGE;
                        }
                    }
                }, FanCustomizationEmojiFragment.this);
            }
        });

        rcDesignList.setAdapter(mAdapter);
        llNoDataFound.setVisibility(View.GONE);
        mEmojiList = new ArrayList<>();
        mCurrentPage = 0;
        getDesignFromServer(false, "");
        setRecScrollListener();
        return view;
    }

    private void init() {
        mLoggedUser = new AppPref(getActivity()).getUserInfo();
    }


    @Override
    protected void setViewListeners() {
        swipeRefresh.setOnRefreshListener(this);
    }

    @Override
    protected void setViewReferences(View view) {

        rcDesignList = (RecyclerView) view.findViewById(R.id.rcItemList);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        rlContent = (RelativeLayout) view.findViewById(R.id.rlContent);
        llNoDataFound = (LinearLayout) view.findViewById(R.id.llNoDataFound);
        txtNoDataFoundTitle = (TextView) view.findViewById(R.id.txtNoDataFoundTitle);
        txtNoDataFoundContent = (TextView) view.findViewById(R.id.txtNoDataFoundContent);
        rlConnectionContainer = (RelativeLayout) view.findViewById(R.id.rlConnectionContainer);
        llLoaderView = (LinearLayout) view.findViewById(R.id.llLoader);

    }

    @Override
    protected boolean isValidData() {
        return false;
    }


    private void initRecyclerView() {
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        rcDesignList.setHasFixedSize(true);

        mLinearLayoutManager = new LinearLayoutManager(mContext);
        // use a linear layout manager
        rcDesignList.setLayoutManager(mLinearLayoutManager);
        //rcDesignList.addItemDecoration(new VerticalSpaceItemDecoration((int) getResources().getDimension(R.dimen.margin_5)));
        rcDesignList.setNestedScrollingEnabled(true);
    }

    public void filterData(String query) {
        mEmojiList.clear();
        mAdapter.setData(mEmojiList);
        mCurrentPage=0;
        getDesignFromServer(false,query );
    }

    private void showNoDataFound() {
        llNoDataFound.setVisibility(View.VISIBLE);
        txtNoDataFoundTitle.setText("");
    }

    @Override
    public void onRefresh() {
        if (Utils.isConnectedToInternet(mHostActivity)) {
            getDesignFromServer(true, "");
        } else {
            swipeRefresh.setRefreshing(false);
            Utils.showToastMessage(mHostActivity, getString(R.string.pls_check_ur_internet_connection));
        }
    }

    public void setRecScrollListener() {

        rcDesignList.addOnScrollListener(new PaginationScrollListener(mLinearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                AppLogger.debug(TAG, "Load more items");

                if (mEmojiList.size() >= PAGE_LIMIT) {
                    AppLogger.debug(TAG, "page limit" + PAGE_LIMIT + " list size" + mEmojiList.size());
                    getDesignFromServer(false, "");
                    mAdapter.addLoader();
                }
            }

            @Override
            public int getTotalPageCount() {
                return 0;//not required
            }

            @Override
            public int getThresholdValue() {
                return PAGE_LIMIT / 2;
            }

            @Override
            public boolean isLastPage() {
                return false;
            }

            @Override
            public boolean isLoading() {
                return mAdapter.isLoaderVisible;
            }
        });
    }


    private void getDesignFromServer(final boolean isRefreshing, final String searchKeyword) {

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
        Call<ApiResponse> apiResponseCall = RestClient.getService().getFanDownloads(mLoggedUser.getLanguageId(), mLoggedUser.getAuthrizedKey(), mLoggedUser.getId(),
                index, limit, DesignType.emoji.getType().toLowerCase(Locale.ENGLISH), "fan_download_list", searchKeyword);

        /*Call<ApiResponse> apiResponseCall = RestClient.getService().getFanHomeProductList(mLoggedUser.getLanguageId(), mLoggedUser.getAuthrizedKey(), mLoggedUser.getId(),
                index, limit, DesignType.stickers.getType().toLowerCase(Locale.ENGLISH), "all_product_list", searchKeyword);
        */
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

                                    if (payload.fanDownloadList != null && payload.fanDownloadList.size() != 0) {
                                        mEmojiList.clear();
                                        mEmojiList.addAll(payload.fanDownloadList);

                                        llNoDataFound.setVisibility(View.GONE);
                                        rcDesignList.setVisibility(View.VISIBLE);
                                        mAdapter.setData(mEmojiList);

                                        mCurrentPage = 0;
                                        mCurrentPage++;
                                    } else {
                                        mEmojiList.clear();
                                        mAdapter.setData(mEmojiList);
                                        if (searchKeyword.length() != 0) {
                                            txtNoDataFoundContent.setText(getString(R.string.no_search_found));
                                        } else {
                                            txtNoDataFoundContent.setText(R.string.txt_no_downloaded_emoji_found);
                                        }
                                        showNoDataFound();
                                    }
                                } else {

                                    if (mCurrentPage == 0) {
                                        mEmojiList.clear();
                                        if (payload.fanDownloadList != null) {
                                            mEmojiList.addAll(payload.fanDownloadList);
                                        }

                                        if (mEmojiList.size() != 0) {
                                            llNoDataFound.setVisibility(View.GONE);
                                            rcDesignList.setVisibility(View.VISIBLE);
                                            mAdapter.setData(mEmojiList);
                                        } else {
                                            showNoDataFound();
                                            if (searchKeyword.length() != 0) {
                                                txtNoDataFoundContent.setText(getString(R.string.no_search_found));
                                            } else {
                                                txtNoDataFoundContent.setText(R.string.txt_no_downloaded_emoji_found);
                                            }
                                            rcDesignList.setVisibility(View.GONE);
                                        }
                                    } else {
                                        AppLogger.error(TAG, "Remove loader...");
                                        mAdapter.removeLoader();
                                        if (payload.fanDownloadList != null && payload.fanDownloadList.size() != 0) {
                                            mEmojiList.addAll(payload.fanDownloadList);
                                            mAdapter.setData(mEmojiList);
                                        }
                                    }

                                    if (payload.fanDownloadList != null && payload.fanDownloadList.size() != 0) {
                                        mCurrentPage++;
                                    }
                                }
                                AppLogger.error(TAG, "item list size => " + mEmojiList.size());

                            } else if (mEmojiList == null || (mEmojiList != null && mEmojiList.size() == 0)) {
                                if (searchKeyword.length() != 0) {
                                    txtNoDataFoundContent.setText(getString(R.string.no_search_found));
                                } else {
                                    txtNoDataFoundContent.setText(R.string.txt_no_downloaded_emoji_found);
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mHostActivity = (FanHomeActivity) context;
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
                    gotoImageEditActivity(mCapturedImageUrl);

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    error.printStackTrace();
                }
                break;
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case WRITE_STORAGE_ACCESS_RQ:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if(mImageSource == PROFILE_GALLERY_IMAGE){
                        pickGalleryImage();
                    }
                    else if(mImageSource == PROFILE_CAMERA_IMAGE){
                        captureImage();
                    }

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    boolean isDenied = ActivityCompat.shouldShowRequestPermissionRationale(mHostActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                    if (!isDenied) {
                        //If the user turned down the permission request in the past and chose the Don't ask again option in the permission request system dialog

                        mPermissionDialog = PermissionManager.showCustomPermissionDialog(mHostActivity, getString(R.string.external_storage_permission_msg), new PermissionManager.CustomPermissionDialogCallback() {
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

    private void gotoImageEditActivity(String capturedImagePath){
        Intent intent = new Intent(mHostActivity, EditImageActivity.class);
        intent.putExtra(FilterFragment.STICKER_IMAGE_PATH, mSelectedItem.getImagePath());
        intent.putExtra(FilterFragment.IMAGE_PATH, capturedImagePath);
        startActivity(intent);
    }
}
