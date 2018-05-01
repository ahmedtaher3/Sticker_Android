package com.sticker_android.controller.activities.fan.home.imagealbum;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sticker_android.R;
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.controller.adaptors.GridViewAdapter;
import com.sticker_android.model.User;
import com.sticker_android.model.filter.FanFilter;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.sharedpref.AppPref;

import java.util.ArrayList;

import retrofit2.Call;

public class ImageAlbumActivity extends AppBaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    ArrayList<String> stringArrayList = new ArrayList<>();
    private GridView gridView;
    private GridViewAdapter gridViewAdapter;
    private AppPref appPref;
    private User userdata;
    private LinearLayout llNoDataFound;
    private SwipeRefreshLayout swipeRefresh;
    private RelativeLayout rlContent;
    private LinearLayout llLoaderView;
    private RelativeLayout rlConnectionContainer;
    private TextView txtNoDataFoundTitle, txtNoDataFoundContent;
    private int mCurrentPage = 0;
    private int PAGE_LIMIT;
    private ArrayList<FanFilter> mImageAlbumList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        getuserInfo();
        setContentView(R.layout.activity_image_album);
        PAGE_LIMIT = getResources().getInteger(R.integer.designed_item_page_limit);
        setViewReferences();
        setViewListeners();
        setAdaptor();
        getFilterApi(false, "");

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
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        rlContent = (RelativeLayout) findViewById(R.id.rlContent);
        llNoDataFound = (LinearLayout) findViewById(R.id.llNoDataFound);
        txtNoDataFoundTitle = (TextView) findViewById(R.id.txtNoDataFoundTitle);
        txtNoDataFoundContent = (TextView) findViewById(R.id.txtNoDataFoundContent);
        rlConnectionContainer = (RelativeLayout) findViewById(R.id.rlConnectionContainer);
        llLoaderView = (LinearLayout) findViewById(R.id.llLoader);

    }

    @Override
    protected boolean isValidData() {
        return false;
    }

    private void setAdaptor() {
        gridViewAdapter = new GridViewAdapter(this);
        gridView.setAdapter(gridViewAdapter);
    }


    private void getFilterApi(final boolean isRefreshing, final String searchKeyword) {


        Call<ApiResponse> apiResponseCall = RestClient.getService().apiFilterList(userdata.getLanguageId(), userdata.getAuthrizedKey(),
                userdata.getId(), 0, 1000, "", "filter_list", "stickers");

        apiResponseCall.enqueue(new ApiCall(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.status) {
                    if (apiResponse.paylpad.fanFilterArrayList != null) {
                        gridViewAdapter.setData(apiResponse.paylpad.fanFilterArrayList);
                    }
                }
            }

            @Override
            public void onFail(Call<ApiResponse> call, Throwable t) {

            }
        });
    }

    private void showNoDataFound() {
        llNoDataFound.setVisibility(View.VISIBLE);
        txtNoDataFoundTitle.setText("");
    }

    @Override
    public void onRefresh() {
        if (Utils.isConnectedToInternet(this)) {
            getFilterApi(true, "");
        } else {
            swipeRefresh.setRefreshing(false);
            Utils.showToastMessage(this, getString(R.string.pls_check_ur_internet_connection));
        }
    }


}
