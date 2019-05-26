package com.sticker_android.controller.activities.fan.home.imagealbum;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    private Toolbar toolbar;
    private LinearLayout llNoDataFound;
    private SwipeRefreshLayout swipeRefresh;
    private RelativeLayout rlContent;
    private LinearLayout llLoaderView;
    private RelativeLayout rlConnectionContainer;
    private TextView txtNoDataFoundTitle, txtNoDataFoundContent;
    private int mCurrentPage = 0;
    private int PAGE_LIMIT;
    private ArrayList<FanFilter> mImageAlbumList = new ArrayList<>();
    private String mFilterImageType;
    public static final String FILTER_IMAGE_TYPE = "filter_image_type";
    public static final String SELECTED_FILTER = "selected_filter";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        init();
        getuserInfo();
        setContentView(R.layout.activity_image_album);
        PAGE_LIMIT = getResources().getInteger(R.integer.designed_item_page_limit);
        getIntentData();
        setViewReferences();
        setViewListeners();
        setToolbar();
        changeStatusBarColor(getResources().getColor(R.color.colorstatusBarFan));

        toolbar.setNavigationIcon(R.drawable.back_arrow_small);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        setAdaptor();
        getFilterApi(false);


    }

    private void getIntentData() {
        if (getIntent() != null) {
            mFilterImageType = getIntent().getStringExtra(FILTER_IMAGE_TYPE);
        }
    }

    private void init() {
        appPref = new AppPref(getActivity());
    }

    private void getuserInfo() {
        userdata = appPref.getUserInfo();
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


    @Override
    protected void setViewListeners() {
        swipeRefresh.setOnRefreshListener(this);
    }

    /**
     * Method is used to set the toolbar title
     */
    private void setToolBarTitle() {
        TextView textView = (TextView) toolbar.findViewById(R.id.tvToolbar);
        textView.setText(getToolbarTitle());
        toolbar.setTitle(" ");
    }

    private String getToolbarTitle() {
        if (mFilterImageType.contains("stickers")) {

            return getString(R.string.txt_choose_sticker);
        } else if (mFilterImageType.contains("filter")) {
            return getString(R.string.text_choose_filter);
        } else if (mFilterImageType.contains("emoji")) {
            return getString(R.string.text_choose_emoji);
        }
        return "";
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
        gridViewAdapter.setOnItemClickListener(new GridViewAdapter.OnFilterItemClickListener() {
            @Override
            public void onItemClick(FanFilter product) {
                product.type = mFilterImageType;
                Intent intent = new Intent();
                intent.putExtra(SELECTED_FILTER, product);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
        gridView.setAdapter(gridViewAdapter);
    }


    private void getFilterApi(final boolean isRefresh) {

        if (isRefresh) {
            swipeRefresh.setRefreshing(true);
        } else {
            llLoaderView.setVisibility(View.VISIBLE);
        }


        Log.i("TAAAAAAG" , String.valueOf(userdata.getId()));
        Log.i("TAAAAAAG" , String.valueOf(userdata.getAuthrizedKey()));

        if (appPref.getLoginFlag(false))
        {
            Call<ApiResponse> apiResponseCall = RestClient.getService().apiFilterList(userdata.getLanguageId(), userdata.getAuthrizedKey(),
                    userdata.getId(), 0, 1000000, "", "filter_list", mFilterImageType);
            apiResponseCall.enqueue(new ApiCall(this) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    if (isRefresh) {
                        swipeRefresh.setRefreshing(false);
                    } else {
                        llLoaderView.setVisibility(View.GONE);
                    }
                    if (apiResponse.status) {
                        if (apiResponse.paylpad.fanFilterArrayList != null) {

                            gridViewAdapter.setData(apiResponse.paylpad.fanFilterArrayList);
                        }
                        if (apiResponse.paylpad.fanFilterArrayList == null && apiResponse.paylpad.fanFilterArrayList.size() == 0) {
                            String filterType = "";

                            if (mFilterImageType.contains("stickers")) {
                                filterType= getString(R.string.txt_choose_stickers);
                                txtNoDataFoundContent.setText(filterType);
                            } else if (mFilterImageType.contains("filter")) {
                                filterType= getString(R.string.txt_choose_filter);
                                txtNoDataFoundContent.setText(filterType);
                            } else if (mFilterImageType.contains("emoji")) {
                                filterType= getString(R.string.txt_choose_emoji);
                                txtNoDataFoundContent.setText(filterType);
                            }

                            showNoDataFound();

                        }
                    }

                }

                @Override
                public void onFail(Call<ApiResponse> call, Throwable t) {
                    if (isRefresh) {
                        swipeRefresh.setRefreshing(false);
                    } else {
                        llLoaderView.setVisibility(View.GONE);
                    }
                }
            });
        }
        else
        {
            Call<ApiResponse> apiResponseCall = RestClient.getService().apiFilterList_new("1", 0, 1000000, "", "filter_list", mFilterImageType);
            apiResponseCall.enqueue(new ApiCall(this) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    if (isRefresh) {
                        swipeRefresh.setRefreshing(false);
                    } else {
                        llLoaderView.setVisibility(View.GONE);
                    }
                    if (apiResponse.status) {
                        if (apiResponse.paylpad.fanFilterArrayList != null) {

                            gridViewAdapter.setData(apiResponse.paylpad.fanFilterArrayList);
                        }
                        if (apiResponse.paylpad.fanFilterArrayList == null && apiResponse.paylpad.fanFilterArrayList.size() == 0) {
                            String filterType = "";

                            if (mFilterImageType.contains("stickers")) {
                                filterType= getString(R.string.txt_choose_stickers);
                                txtNoDataFoundContent.setText(filterType);
                            } else if (mFilterImageType.contains("filter")) {
                                filterType= getString(R.string.txt_choose_filter);
                                txtNoDataFoundContent.setText(filterType);
                            } else if (mFilterImageType.contains("emoji")) {
                                filterType= getString(R.string.txt_choose_emoji);
                                txtNoDataFoundContent.setText(filterType);
                            }

                            showNoDataFound();

                        }
                    }

                }

                @Override
                public void onFail(Call<ApiResponse> call, Throwable t) {
                    if (isRefresh) {
                        swipeRefresh.setRefreshing(false);
                    } else {
                        llLoaderView.setVisibility(View.GONE);
                    }
                }
            });

        }


    }

    private void showNoDataFound() {
        llNoDataFound.setVisibility(View.VISIBLE);
        txtNoDataFoundTitle.setText("");
    }

    @Override
    public void onRefresh() {
        if (Utils.isConnectedToInternet(this)) {
            getFilterApi(true);
        } else {
            swipeRefresh.setRefreshing(false);
            Utils.showToastMessage(this, getString(R.string.pls_check_ur_internet_connection));
        }
    }
}
