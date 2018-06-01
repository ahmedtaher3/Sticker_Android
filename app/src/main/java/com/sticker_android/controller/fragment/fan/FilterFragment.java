package com.sticker_android.controller.fragment.fan;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.sticker_android.R;
import com.sticker_android.constant.AppConstant;
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.controller.activities.fan.home.FanAdShare.FanAdShareActivity;
import com.sticker_android.controller.activities.fan.home.imagealbum.ImageAlbumActivity;
import com.sticker_android.controller.activities.fan.home.imagealbum.ImageAlbumStickers.ImageAlbumStickers;
import com.sticker_android.model.User;
import com.sticker_android.model.corporateproduct.Product;
import com.sticker_android.model.filter.FanFilter;
import com.sticker_android.model.interfaces.ImagePickerListener;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.AWSUtil;
import com.sticker_android.utils.AppLogger;
import com.sticker_android.utils.BitmapUtils;
import com.sticker_android.utils.FileUtil;
import com.sticker_android.utils.ProgressDialogHandler;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.helper.PermissionManager;
import com.sticker_android.utils.sharedpref.AppPref;
import com.sticker_android.view.StickerView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import id.zelory.compressor.Compressor;
import retrofit2.Call;

import static android.app.Activity.RESULT_OK;
import static com.sticker_android.utils.helper.PermissionManager.Constant.WRITE_STORAGE_ACCESS_RQ;

/**
 * Created by satyendra on 4/9/18.
 */

public class FilterFragment extends Fragment implements View.OnClickListener {

    private final String TAG = FilterFragment.class.getSimpleName();
    private Context mContext;
    private AppBaseActivity mHostActivity;

    private View inflatedView;
    private User mLoggedUser;
    public Bitmap mainBitmap;
    private Bitmap originalBitmap;
    public ImageView mainImage;
    private ImageView imgFilterMask;
    private RelativeLayout rlImageContainer, rlPlaceHolderClick, rlResetButtonHolder;
    private LinearLayout rlFilterOptionContainer, llFilter, llSticker, llEmoji;
    private Button btnReset, btnSave;
    private FrameLayout mWorkSpaceFrame;

    private SaveImageTask mSaveImageTask;
    private LoadImageTask mLoadImageTask;
    private StickerView mStickerView;

    public String filePath;
    public String saveFilePath;

    public static final int PROFILE_CAMERA_IMAGE = 30;
    public static final int PROFILE_GALLERY_IMAGE = 31;
    public static final int CHOOSE_GALLERY_FILTER = 32;
    private int mImageSource = -1;
    private String mCapturedImageUrl;
    private android.app.AlertDialog mPermissionDialog;
    private FanFilter mSelectedFilter;
    public static final String IMAGE_PATH = "image_path";
    public static final String STICKER_IMAGE_PATH = "sticker_image_path";
    private Product adObj = null;
    private File compressedImageFile;

    public interface OnImageLoadingListener {
        void onImageLoadCompletion();

        void onImageLoadFailure();
    }

    public FilterFragment(){
        AppLogger.debug(TAG, "FilterFragment new instance created");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mHostActivity = (AppBaseActivity) context;
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

            Bundle argument = getArguments();
            if (argument != null) {
                mCapturedImageUrl = argument.getString(IMAGE_PATH);
                String stickerPath = argument.getString(STICKER_IMAGE_PATH);
                rlPlaceHolderClick.setVisibility(View.GONE);
                loadImage(mCapturedImageUrl);
                mSelectedFilter = new FanFilter();
                mSelectedFilter.type = "sticker";
                mSelectedFilter.imageUrl = stickerPath;

                if (mLoadImageTask != null) {
                    mLoadImageTask.setImageLoadingListener(new OnImageLoadingListener() {
                        @Override
                        public void onImageLoadCompletion() {
                            setSelectedFilter();
                        }

                        @Override
                        public void onImageLoadFailure() {

                        }
                    });
                }

                rlFilterOptionContainer.setVisibility(View.VISIBLE);
                rlResetButtonHolder.setVisibility(View.VISIBLE);
                makeSaveButtonEnable(true);
            } else {
                makeSaveButtonEnable(false);
                makeFilterOptionEnable(false);
            }

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
        /*if (new AppPref(getActivity()).getAds() != null)
            adDialog();*/

        getRandomAdApi();
        //   startActivity(new Intent(getActivity(), FanAdShareActivity.class));
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

        if (originalBitmap != null && !originalBitmap.isRecycled()) {
            originalBitmap.recycle();
            originalBitmap = null;
        }
        if (mLoadImageTask != null) {
            mLoadImageTask.cancel(true);
        }

        if (mSaveImageTask != null) {
            mSaveImageTask.cancel(true);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void setViewReferences() {
        mStickerView = (StickerView) inflatedView.findViewById(R.id.sticker_panel);
        mainImage = (ImageView) inflatedView.findViewById(R.id.main_image);
        rlImageContainer = (RelativeLayout) inflatedView.findViewById(R.id.rlImageContainer);
        rlFilterOptionContainer = (LinearLayout) inflatedView.findViewById(R.id.rlFilterOptionContainer);
        llSticker = (LinearLayout) inflatedView.findViewById(R.id.llSticker);
        llFilter = (LinearLayout) inflatedView.findViewById(R.id.llFilter);
        llEmoji = (LinearLayout) inflatedView.findViewById(R.id.llEmoji);
        btnReset = (Button) inflatedView.findViewById(R.id.btnReset);
        btnSave = (Button) inflatedView.findViewById(R.id.btnSave);
        imgFilterMask = (ImageView) inflatedView.findViewById(R.id.imgFilterMask);
        rlPlaceHolderClick = (RelativeLayout) inflatedView.findViewById(R.id.rlPlaceHolderClick);
        rlResetButtonHolder = (RelativeLayout) inflatedView.findViewById(R.id.rlResetButtonHolder);
        mWorkSpaceFrame = (FrameLayout) inflatedView.findViewById(R.id.work_space);
    }

    public void setListenerOnViews() {
        llFilter.setOnClickListener(this);
        llSticker.setOnClickListener(this);
        llEmoji.setOnClickListener(this);
        btnReset.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        rlPlaceHolderClick.setOnClickListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AppLogger.error(TAG, "Inside onActivityResult() " + requestCode+"result"+resultCode + " picked url => " + mCapturedImageUrl);

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
                    rlPlaceHolderClick.setVisibility(View.GONE);
                    Log.e(TAG, "Path => " + mCapturedImageUrl);
                    makeFilterOptionEnable(true);
                    loadImage(mCapturedImageUrl);

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    error.printStackTrace();
                }
                break;
            case CHOOSE_GALLERY_FILTER:
                if (resultCode == Activity.RESULT_OK) {
                    mSelectedFilter = data.getParcelableExtra(ImageAlbumActivity.SELECTED_FILTER);
                    AppLogger.error(TAG, "Inside onActivityResult()" + mSelectedFilter.type);
                    if (mSelectedFilter != null) {
                        /*mStickerView.clear();
                        imgFilterMask.setImageBitmap(null);
                        imgFilterMask.setVisibility(View.GONE);*/
                        makeSaveButtonEnable(true);
                        setSelectedFilter();
                    }
                }
                break;
        }
    }

    private void setSelectedFilter() {
        final ProgressDialogHandler progressDialogHandler = new ProgressDialogHandler(mHostActivity);
        progressDialogHandler.show();

        DrawableTypeRequest request = Glide.with(this).load(mSelectedFilter.imageUrl);
        SimpleTarget<Bitmap> bitmapSimpleTarget = new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(final Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                progressDialogHandler.hide();

                if (mSelectedFilter.type.contains("filter")) {
                            /* mStickerView.setVisibility(View.GONE);*/
                    imgFilterMask.setVisibility(View.VISIBLE);

                    int width = imgFilterMask.getWidth();
                    if (width != 0) {
                        int imgHeight = (width * bitmap.getHeight()) / bitmap.getWidth();
                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) imgFilterMask.getLayoutParams();
                        params.height = imgHeight;
                        imgFilterMask.setLayoutParams(params);
                        imgFilterMask.setImageBitmap(bitmap);
                    } else {
                        imgFilterMask.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                imgFilterMask.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                int width = imgFilterMask.getWidth();
                                int height = imgFilterMask.getHeight();

                                int imgHeight = (width * bitmap.getHeight()) / bitmap.getWidth();
                                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) imgFilterMask.getLayoutParams();
                                params.height = imgHeight;
                                imgFilterMask.setLayoutParams(params);
                                imgFilterMask.setImageBitmap(bitmap);
                            }
                        });
                    }

                } else {
                    mStickerView.setVisibility(View.VISIBLE);
                            /*imgFilterMask.setVisibility(View.GONE);*/
                    setSelectedStickerItem(bitmap);
                }
            }
        };

        request.listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                progressDialogHandler.hide();
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                return false;
            }
        });
        request.diskCacheStrategy(DiskCacheStrategy.SOURCE);
        request.asBitmap()
                .into(bitmapSimpleTarget);
    }

    private void openCropActivity(String url) {
        CropImage.activity(Uri.fromFile(new File(url)))
                .setGuidelines(CropImageView.Guidelines.OFF)
                .setFixAspectRatio(true)
                .setAspectRatio(1, 1)
                .setAutoZoomEnabled(true)
                .start(mContext, this);
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
                    if (mImageSource == PROFILE_GALLERY_IMAGE) {
                        pickGalleryImage();
                    } else if (mImageSource == PROFILE_CAMERA_IMAGE) {
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

    private void setSelectedStickerItem(Bitmap bitmap) {
        mStickerView.addBitImage(bitmap);
    }

    /**
     * Load the image from filepath into mainImage imageView.
     *
     * @param filepath The image to be loaded.
     */
    public void loadImage(String filepath) {
        if (mLoadImageTask != null) {
            mLoadImageTask.cancel(true);
        }
        mLoadImageTask = new LoadImageTask();
        mLoadImageTask.execute(filepath);
    }

    protected void doSaveImage() {
        if (mSaveImageTask != null) {
            mSaveImageTask.cancel(true);
        }

        saveFilePath = Utils.getCustomImagePath(mHostActivity, null).getAbsolutePath();
        //  beginUpload(saveFilePath);
        mainImage.setDrawingCacheEnabled(true);
        mainImage.buildDrawingCache(true);

        mStickerView.setDrawingCacheEnabled(true);
        mStickerView.buildDrawingCache(true);

        imgFilterMask.setDrawingCacheEnabled(true);
        imgFilterMask.buildDrawingCache(true);

        mSaveImageTask = new SaveImageTask();
        mSaveImageTask.execute(mergeBitmap(mainImage.getDrawingCache(), mStickerView.getDrawingCache(), imgFilterMask.getDrawingCache()));
    }

    /**
     * will merge the filter bitmap and sticker or emoji bitmap with original image bitmap
     *
     * @param bmp1 original image
     * @param bmp2 sticker or emoji
     * @param bmp3 filter
     * @return Merged bitmap
     */
    public Bitmap mergeBitmap(Bitmap bmp1, Bitmap bmp2, Bitmap bmp3) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);

        Resources resources = getActivity().getResources();
        Bitmap waterMark = BitmapFactory.decodeResource(resources, R.drawable.watermark);

        if(waterMark != null){
            canvas.drawBitmap(waterMark, bmp1.getWidth() - waterMark.getWidth() - 10, 0, null);
        }

        if (bmp3 != null) {
            canvas.drawBitmap(bmp3, 0, bmp1.getHeight() - bmp3.getHeight(), null);
        }

        if (bmp2 != null) {
            canvas.drawBitmap(bmp2, 0, 0, null);
        }
        return bmOverlay;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnReset:
                mStickerView.clear();
                mainImage.setImageBitmap(null);
                imgFilterMask.setImageBitmap(null);
                rlPlaceHolderClick.setVisibility(View.VISIBLE);
                mCapturedImageUrl = null;
                makeSaveButtonEnable(false);
                makeFilterOptionEnable(false);
                break;
            case R.id.btnSave:
                if (mSelectedFilter.type.contains("filter")
                        || mStickerView.getBank().size() != 0) {
                    mStickerView.hideHelpBoxTool();
                    doSaveImage();
                } else {
                    Toast.makeText(mHostActivity, R.string.no_customization_alert, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.llFilter:
                openFilterGallery("filter");
                break;
            case R.id.llSticker:
                openFilterGallery("stickers");
                break;
            case R.id.llEmoji:
                openFilterGallery("emoji");
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

                    @Override
                    public void selectedItemPosition(int position) {
                        if (position == 0) {
                            mImageSource = PROFILE_CAMERA_IMAGE;
                        } else if (position == 1) {
                            mImageSource = PROFILE_GALLERY_IMAGE;
                        }
                    }
                }, this);
                break;
        }
    }

    private void makeSaveButtonEnable(boolean enable) {
        if (enable) {
            btnSave.setEnabled(true);
            btnSave.setAlpha(1.0f);
        } else {
            btnSave.setEnabled(false);
            btnSave.setAlpha(0.3f);
        }
    }

    private void makeFilterOptionEnable(boolean enable) {
        if (enable) {
            rlFilterOptionContainer.setEnabled(true);
            rlFilterOptionContainer.setAlpha(1.0f);
        } else {
            rlFilterOptionContainer.setEnabled(false);
            rlFilterOptionContainer.setAlpha(0.3f);
        }
    }

    private void openFilterGallery(String type) {
        if (mCapturedImageUrl == null) {
            //Toast.makeText(mHostActivity, R.string.select_image_for_filter, Toast.LENGTH_SHORT).show();
            return;
        }

        if (type.equalsIgnoreCase("filter")) {
            Intent intent = new Intent(getActivity(), ImageAlbumActivity.class);
            intent.putExtra(ImageAlbumActivity.FILTER_IMAGE_TYPE, type);
            startActivityForResult(intent, CHOOSE_GALLERY_FILTER);

        } else {
            Intent intent = new Intent(getActivity(), ImageAlbumStickers.class);
            intent.putExtra(ImageAlbumActivity.FILTER_IMAGE_TYPE, type);
            startActivityForResult(intent, CHOOSE_GALLERY_FILTER);
        }
    }

    private void pickGalleryImage() {
        Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        getActivity().startActivityForResult(openGallery, PROFILE_GALLERY_IMAGE);
    }

    private void captureImage() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = Utils.getCustomImagePath(mHostActivity, System.currentTimeMillis() + "");
        mCapturedImageUrl = file.getAbsolutePath();
        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        AppLogger.error(TAG, "capture url = > " + mCapturedImageUrl);
        getActivity().startActivityForResult(takePicture, PROFILE_CAMERA_IMAGE);
    }

    private final class LoadImageTask extends AsyncTask<String, Void, Bitmap> {

        public OnImageLoadingListener imageLoadingListener;

        public void setImageLoadingListener(OnImageLoadingListener listener) {
            imageLoadingListener = listener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mStickerView.setVisibility(View.GONE);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                FileInputStream inputStream = new FileInputStream(new File(mHostActivity.getCacheDir(),
                        getFileName(params[0])));

                return BitmapFactory.decodeStream(inputStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return null;
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
            //mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
            originalBitmap = mainBitmap.copy(mainBitmap.getConfig(), true);
            mStickerView.mainBitmap = mainBitmap;
            mStickerView.mainImage = mainImage;
            mStickerView.setVisibility(View.VISIBLE);
            mWorkSpaceFrame.requestLayout();

            if (imageLoadingListener != null) {
                imageLoadingListener.onImageLoadCompletion();
            }
        }
    }

    private String getFileName(String fileName) {
        String name = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());
        Log.e(TAG, "File name => " + name);
        return name;
    }

    private final class SaveImageTask extends AsyncTask<Bitmap, Void, Boolean> {
        private Dialog dialog;

        @Override
        protected Boolean doInBackground(Bitmap... params) {
            if (TextUtils.isEmpty(saveFilePath))
                return false;
            return BitmapUtils.saveBitmap(params[0], saveFilePath);
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
            dialog = getLoadingDialog(mContext, getString(R.string.txt_saving_image), false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            dialog.dismiss();

            if (result) {
                FileUtil.albumUpdate(mHostActivity, saveFilePath);

/*Compress code added*/
                try {
/*     File file = new File(getRealPathFromURI(resultUri));
                   */
                    AppLogger.debug(TAG, "Size is befor compress " + saveFilePath.length());
                    File file = new File(saveFilePath);
                    AppLogger.debug(TAG, "Size is before compress in unit " + Utils.getFileSize(file));
                    compressedImageFile = new Compressor(getActivity()).compressToFile(file);
                    AppLogger.debug(TAG, "Size is after compress " + Integer.parseInt(String.valueOf(compressedImageFile.length() / 1024)));
                    AppLogger.debug(TAG, "Size is after compress in unit " + Utils.getFileSize(compressedImageFile));

                } catch (IOException e) {
                    e.printStackTrace();
                }
                mCapturedImageUrl = compressedImageFile.getAbsolutePath();
                beginUpload(mCapturedImageUrl);
                /* Compress code end
                * */

                //beginUpload(saveFilePath); /*Upload image without compress old code*/
                mStickerView.clear();
                mCapturedImageUrl = null;
                mainImage.setImageBitmap(null);
                imgFilterMask.setImageBitmap(null);
                rlPlaceHolderClick.setVisibility(View.VISIBLE);
                makeSaveButtonEnable(false);
                makeFilterOptionEnable(false);
                  /*if (rlFilterOptionContainer.getVisibility() == View.INVISIBLE) {
                    mHostActivity.onBackPressed();
                }*/
            } else {
                Toast.makeText(mHostActivity, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static Dialog getLoadingDialog(Context context, String title, boolean canCancel) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setCancelable(canCancel);
        dialog.setMessage(title);
        return dialog;
    }


    /*
       * Begins to upload the file specified by the file path.
       */
    private void beginUpload(final String filePath) {

        final ProgressDialogHandler progressDialogHandler = new ProgressDialogHandler(getActivity());
        progressDialogHandler.show();
        if (filePath == null) {
            Toast.makeText(getActivity(), "Could not find the filepath of the selected file",
                    Toast.LENGTH_LONG).show();
            return;
        }
        final String fileName = Utils.getFileName(mLoggedUser.getId());

        File file = new File(filePath);
        TransferObserver observer = new AWSUtil().getTransferUtility(getActivity()).upload(AppConstant.BUCKET_NAME, fileName,
                file);
        observer.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                Log.d(TAG, "onStateChanged: " + id + ", " + state);
               /* if (TransferState.COMPLETED == state) {
                    if (progressDialogHandler != null)
                        progressDialogHandler.hide();
                    String imagePath = AppConstant.BUCKET_IMAGE_BASE_URL + fileName;
                    saveImageApi(imagePath, filePath);
                }*/


                Log.d(TAG, "onStateChanged: " + id + ", " + state);
                if (TransferState.COMPLETED == state) {
                    if (progressDialogHandler != null)
                        progressDialogHandler.hide();
                    String imagePath = AppConstant.BUCKET_IMAGE_BASE_URL + fileName;
                    Toast.makeText(mHostActivity, getString(R.string.saved_successfully), Toast.LENGTH_SHORT).show();

                    saveImageApi(imagePath, filePath);

                    Intent intent=new Intent(getActivity(),FanAdShareActivity.class);
                    intent.putExtra("link", imagePath);
                    intent.putExtra("local_file_path", saveFilePath);
                    startActivity(intent);

                    if (rlFilterOptionContainer.getVisibility() == View.INVISIBLE) {
                        mHostActivity.onBackPressed();
                    }
                }

            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                Log.d(TAG, String.format("onProgressChanged: %d, total: %d, current: %d",
                        id, bytesTotal, bytesCurrent));
            }

            @Override
            public void onError(int id, Exception ex) {
                if (progressDialogHandler != null)
                    progressDialogHandler.hide();
                Log.e(TAG, "Error during upload: " + id, ex);
            }

        });
    }

    private void saveImageApi(final String imagePath, final String localFilePath) {


        new Handler().post(new Runnable() {
            @Override
            public void run() {

                Call<ApiResponse> apiResponseCall = RestClient.getService().saveCustomizeImage(mLoggedUser.getLanguageId(), mLoggedUser.getAuthrizedKey()
                        , mLoggedUser.getId(), imagePath);

                apiResponseCall.enqueue(new ApiCall(getActivity()) {
                    @Override
                    public void onSuccess(ApiResponse apiResponse) {

                        if (apiResponse.status) {

                        }
                    }

                    @Override
                    public void onFail(Call<ApiResponse> call, Throwable t) {
                    }
                });
            }
        });

       /* final ProgressDialogHandler progressDialogHandler = new ProgressDialogHandler(getActivity());
        progressDialogHandler.show();

        Call<ApiResponse> apiResponseCall = RestClient.getService().saveCustomizeImage(mLoggedUser.getLanguageId(), mLoggedUser.getAuthrizedKey()
                , mLoggedUser.getId(), imagePath);

        apiResponseCall.enqueue(new ApiCall(getActivity()) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                progressDialogHandler.hide();
                if (apiResponse.status) {
                    Toast.makeText(mHostActivity, getString(R.string.saved_successfully), Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(getActivity(),FanAdShareActivity.class);
                    intent.putExtra("link",imagePath);
                    intent.putExtra("local_file_path", saveFilePath);

                    startActivity(intent);
                    // adDialog();
                    if (rlFilterOptionContainer.getVisibility() == View.INVISIBLE) {
                        mHostActivity.onBackPressed();
                    }
                }

            }

            @Override
            public void onFail(Call<ApiResponse> call, Throwable t) {
                progressDialogHandler.hide();
            }
        });
*/
    }


    public void adDialog() {
        adObj = new AppPref(mContext).getAds();
        if (adObj != null) {
            final Dialog dialog = new Dialog(getActivity());
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(LayoutInflater.from(getActivity()).inflate(R.layout.image_dialog, null));
            dialog.show();
            dialog.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            final ProgressBar pgrImage = (ProgressBar) dialog.findViewById(R.id.pgrImage);
            ImageView image = (ImageView) dialog.findViewById(R.id.image);
            Glide.with(getActivity())
                    .load(adObj.getImagePath())
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            pgrImage.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            pgrImage.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(image);

        }
    }


    private void getRandomAdApi() {
        final AppPref appPref = new AppPref(getActivity());
        if (appPref.getLoginFlag(false)) {
            User adObj = appPref.getUserInfo();
            Call<ApiResponse> apiResponseCall = RestClient.getService().getRandomFeaturedProduct(adObj.getLanguageId(), adObj.getAuthrizedKey(), adObj.getId(), "product");

            apiResponseCall.enqueue(new ApiCall(getActivity()) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    if (apiResponse.status) {
                        appPref.saveAds(apiResponse.paylpad.product);
                    }
                }

                @Override
                public void onFail(Call<ApiResponse> call, Throwable t) {

                }
            });
        }

    }

}
