package com.sticker_android.controller.activities.fan.home;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sticker_android.R;
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.controller.fragment.fan.FilterFragment;
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

public class EditImageActivity extends AppBaseActivity {

    private Toolbar toolbar;
    private RelativeLayout rlContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_edit);
        setToolbar();
        setViewReferences();
        changeStatusBarColor(getResources().getColor(R.color.colorstatusBarFan));

        toolbar.setNavigationIcon(R.drawable.back_arrow_small);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent intent = getIntent();
        if(intent != null){
            String path = intent.getStringExtra(FilterFragment.IMAGE_PATH);
            String stickerPath = intent.getStringExtra(FilterFragment.STICKER_IMAGE_PATH);
            addFilterFragment(path, stickerPath);
        }
    }

    private void addFilterFragment(String imagePath, String stickerPath){
        FragmentManager fragMan = getSupportFragmentManager();
        FragmentTransaction fragTransaction = fragMan.beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putString(FilterFragment.STICKER_IMAGE_PATH, stickerPath);
        bundle.putString(FilterFragment.IMAGE_PATH, imagePath);

        Fragment myFrag = new FilterFragment();
        myFrag.setArguments(bundle);
        fragTransaction.add(R.id.rlContainer, myFrag);
        fragTransaction.commit();
    }

    @Override
    protected void setViewListeners() {
        rlContainer = (RelativeLayout) findViewById(R.id.rlContainer);
    }

    @Override
    protected void setViewReferences() {

    }

    @Override
    protected boolean isValidData() {
        return false;
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
        toolbar.setBackground(getResources().getDrawable(R.drawable.fan_header_hdpi));
    }

    /**
     * Method is used to set the toolbar title
     */
    private void setToolBarTitle() {
        TextView textView = (TextView) toolbar.findViewById(R.id.tvToolbar);
        textView.setText(R.string.apply_filter_title);
        toolbar.setTitle(" ");
    }
}
