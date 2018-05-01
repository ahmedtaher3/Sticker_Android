package com.sticker_android.controller.activities.fan.home.imagealbum;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.sticker_android.R;
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.controller.adaptors.GridViewAdapter;
import com.sticker_android.model.User;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.sharedpref.AppPref;

import java.util.ArrayList;

public class ImageAlbumActivity extends AppBaseActivity {

    ArrayList<String> stringArrayList=new ArrayList<>();
    private GridView gridView;
    private GridViewAdapter gridViewAdapter;
    private AppPref appPref;
    private User userdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_album);

        setViewReferences();
        setViewListeners();
        setAdaptor();
        getFilterApi();

    }

    private void getFilterApi() {
     //   RestClient.getService().apiFilterList(userdata.getLanguageId(),userdata.getAuthrizedKey());

    }

    private void init() {

        appPref = new AppPref(getActivity());
    }

    private void getuserInfo() {
        userdata = appPref.getUserInfo();
    }


    @Override
    protected void setViewListeners() {

    }

    @Override
    protected void setViewReferences() {
        gridView = (GridView) findViewById(R.id.gridView);
    }

    @Override
    protected boolean isValidData() {
        return false;
    }

    private void setAdaptor() {
        gridViewAdapter = new GridViewAdapter(this);
        gridView.setAdapter(gridViewAdapter);
    }

}
