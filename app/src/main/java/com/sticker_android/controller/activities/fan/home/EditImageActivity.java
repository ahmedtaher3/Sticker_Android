package com.sticker_android.controller.activities.fan.home;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import com.sticker_android.R;
import com.sticker_android.utils.BitmapUtils;
import com.sticker_android.view.StickerView;
import com.sticker_android.view.imagezoom.ImageViewTouch;
import com.sticker_android.view.imagezoom.ImageViewTouchBase;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by satyendra
 */

public class EditImageActivity extends EditBaseActivity implements View.OnClickListener, View.OnTouchListener {

    public static final String FILE_PATH = "extra_input";
    public static final String EXTRA_OUTPUT = "extra_output";
    public static final String IMAGE_IS_EDIT = "image_is_edit";

    public String filePath;
    public String saveFilePath;
    private int imageWidth, imageHeight;

    public static int mode;

    /**
     * Number of times image has been edited. Indicates whether image has been edited or not.
     */
    protected int mOpTimes = 0;
    protected boolean isBeenSaved = false;

    private LoadImageTask mLoadImageTask;

    private EditImageActivity mContext;
    public Bitmap mainBitmap;
    private Bitmap originalBitmap;
    public ImageViewTouch mainImage;

    private SaveImageTask mSaveImageTask;
    private StickerView mStickerView;

    public ArrayList<Bitmap> bitmapsForUndo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        checkInitImageLoader();
        setContentView(R.layout.activity_image_edit);
        getViewReferences();
        initView();
        getData();
    }

    /**
     * Gets the image to be loaded from the intent and displays this image.
     */
    private void getData() {
        loadImage(filePath);
        if (null != getIntent() && null != getIntent().getExtras()){
            Bundle bundle = getIntent().getExtras();
            filePath = bundle.getString(FILE_PATH);
            saveFilePath = bundle.getString(EXTRA_OUTPUT);
            loadImage(filePath);
            return;
        }
    }

    /**
     * Called from onCreate().
     * Initializes all view objects and fragments to be used.
     */
    private void initView() {
        mContext = this;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        imageWidth = metrics.widthPixels / 2;
        imageHeight = metrics.heightPixels / 2;

        bitmapsForUndo = new ArrayList<>();
    }

    private void getViewReferences(){
        mStickerView = (StickerView) findViewById(R.id.sticker_panel);
        mainImage = (ImageViewTouch) findViewById(R.id.main_image);
    }

    private void recycleBitmapList(int fromIndex){
        while (fromIndex < bitmapsForUndo.size()){
            bitmapsForUndo.get(fromIndex).recycle();
            bitmapsForUndo.remove(fromIndex);
        }
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

    protected void doSaveImage() {
        if (mSaveImageTask != null) {
            mSaveImageTask.cancel(true);
        }

        mSaveImageTask = new SaveImageTask();
        mSaveImageTask.execute(mainBitmap);
    }

    //Increment no. of times the image has been edited
    public void increaseOpTimes() {
        mOpTimes++;
        isBeenSaved = false;
    }

    public void resetOpTimes() {
        isBeenSaved = true;
    }

    /**
     * Allow exit only if image has not been modified or has been modified and saved.
     * @return true if can exit, false if cannot.
     */
    public boolean canAutoExit() {
        return isBeenSaved || mOpTimes == 0;
    }

    private ArrayList<String> addStickerImages(String folderPath) {
        ArrayList<String> pathList = new ArrayList<>();
        try {
            String[] files = getAssets().list(folderPath);

            for (String name : files) {
                pathList.add(folderPath + File.separator + name);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pathList;
    }

    private Bitmap getImageFromAssetsFile(String fileName) {
        Bitmap image = null;
        AssetManager am = getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public void selectedStickerItem(String path) {
        mStickerView.addBitImage(getImageFromAssetsFile(path));
    }

    private void setSelectedStickerItem(Bitmap bitmap){
        mStickerView.addBitImage(bitmap);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
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
                resetOpTimes();
                //onSaveTaskDone();
            } else {
                //SnackBarHandler.show(parentLayout,R.string.save_error);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
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

        recycleBitmapList(0);
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){}
    }
}
