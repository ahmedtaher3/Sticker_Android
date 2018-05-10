package com.sticker_android.controller.activities.fan.home.contest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sticker_android.R;
import com.sticker_android.constant.AppConstant;
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.controller.adaptors.FanAllContestListAdaptor;
import com.sticker_android.model.User;
import com.sticker_android.model.contest.FanContest;
import com.sticker_android.model.contest.FanContestAll;
import com.sticker_android.model.interfaces.MessageEventListener;
import com.sticker_android.model.interfaces.NetworkPopupEventListener;
import com.sticker_android.model.notification.NotificationApp;
import com.sticker_android.model.payload.Payload;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.AppLogger;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.helper.PaginationScrollListener;
import com.sticker_android.utils.sharedpref.AppPref;

import java.util.ArrayList;

import retrofit2.Call;

public class FanContestListActivity extends AppBaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView rcItemListContest;
    private LinearLayout llNoDataFound;
    private SwipeRefreshLayout swipeRefresh;
    private RelativeLayout rlContent;
    private LinearLayout llLoaderView;
    private RelativeLayout rlConnectionContainer;
    private TextView txtNoDataFoundTitle, txtNoDataFoundContent;

    private final String TAG = FanContestListActivity.class.getSimpleName();
    private Context mContext;

    private View inflatedView;
    private LinearLayoutManager mLinearLayoutManager;
    private User mLoggedUser;
    private int mCurrentPage = 0;
    private int PAGE_LIMIT;
    private ArrayList<FanContestAll> mContestList;
    private FanAllContestListAdaptor mAdapter;
    private FanContest mContestObj;
    private Toolbar toolbar;
    private NotificationApp notificationObj;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fan_contest_list);
        PAGE_LIMIT = getResources().getInteger(R.integer.designed_item_page_limit);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setToolBarTitle();
        setToolbarBackground(toolbar);
        setSupportActionBar(toolbar);
        getIntentValues();
        toolbar.setNavigationIcon(R.drawable.back_arrow_small);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        init();
        setViewReferences();
        setViewListeners();
        initRecyclerView();
        mAdapter = new FanAllContestListAdaptor(getActivity());
        rcItemListContest.setAdapter(mAdapter);
        llNoDataFound.setVisibility(View.GONE);
        mContestList = new ArrayList<>();
        mCurrentPage = 0;
        getContestListFromServer(false, "");
        setListenerOnRecview();
        changeStatusBarColor(getResources().getColor(R.color.colorstatusBarFan));

    }


    private void getIntentValues() {

        Intent intent = getIntent();
        if (intent != null) {
            mContestObj = intent.getParcelableExtra(AppConstant.FAN_CONTEST_OBJ);
        }
        if (getIntent().getExtras() != null) {
            notificationObj = getIntent().getExtras().getParcelable(AppConstant.NOTIFICATION_OBJ);
            if (notificationObj != null) {
                mContestObj = new FanContest();
                mContestObj.contestId = notificationObj.acme.contestObj.contestId;
            }
        }
    }

    private void setToolBarTitle() {
        TextView textView = (TextView) toolbar.findViewById(R.id.tvToolbar);
        textView.setText(getResources().getString(R.string.txt_contest));
        toolbar.setTitle("");
    }

    private void setToolbarBackground(Toolbar toolbar) {
        toolbar.setBackground(getResources().getDrawable(R.drawable.fan_header_xhdpi));
    }


    public void setListenerOnRecview() {

        rcItemListContest.addOnScrollListener(new PaginationScrollListener(mLinearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                AppLogger.debug(TAG, "Load more items");

                if (mContestList.size() >= PAGE_LIMIT) {
                    AppLogger.debug(TAG, "page limit" + PAGE_LIMIT + " list size" + mContestList.size());
                    getContestListFromServer(false, "");
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

    @Override
    protected void setViewReferences() {

        rcItemListContest = (RecyclerView) findViewById(R.id.rcItemListContest);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        rlContent = (RelativeLayout) findViewById(R.id.rlContent);
        llNoDataFound = (LinearLayout) findViewById(R.id.llNoDataFound);
        txtNoDataFoundTitle = (TextView) findViewById(R.id.txtNoDataFoundTitle);
        txtNoDataFoundContent = (TextView) findViewById(R.id.txtNoDataFoundContent);
        rlConnectionContainer = (RelativeLayout) findViewById(R.id.rlConnectionContainer);
        llLoaderView = (LinearLayout) findViewById(R.id.llLoader);

    }


    private void init() {
        mLoggedUser = new AppPref(getActivity()).getUserInfo();
    }


    @Override
    protected void setViewListeners() {
        swipeRefresh.setOnRefreshListener(this);
    }


    @Override
    protected boolean isValidData() {
        return false;
    }


    private void initRecyclerView() {
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        rcItemListContest.setHasFixedSize(true);

        mLinearLayoutManager = new LinearLayoutManager(mContext);
        // use a linear layout manager
        rcItemListContest.setLayoutManager(mLinearLayoutManager);
        //rcItemListContest.addItemDecoration(new VerticalSpaceItemDecoration((int) getResources().getDimension(R.dimen.margin_5)));
        rcItemListContest.setNestedScrollingEnabled(true);
    }

    public void filterData() {
        Toast.makeText(getActivity(), "filter data called", Toast.LENGTH_SHORT).show();
    }

    private void getContestListFromServer(final boolean isRefreshing, final String searchKeyword) {

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

        Call<ApiResponse> apiResponseCall = RestClient.getService().getFanAllContestList(mLoggedUser.getLanguageId(), mLoggedUser.getAuthrizedKey(), mLoggedUser.getId(),
                mContestObj.contestId, index, limit, "fan_contest_list_all");
        apiResponseCall.enqueue(new ApiCall(getActivity(), 1) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {

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

                                if (payload.fanContestAllArrayList != null && payload.fanContestAllArrayList.size() != 0) {
                                    mContestList.clear();
                                    mContestList.addAll(payload.fanContestAllArrayList);
                                    llNoDataFound.setVisibility(View.GONE);
                                    rcItemListContest.setVisibility(View.VISIBLE);
                                    mAdapter.setData(mContestList);
                                    mCurrentPage = 0;
                                    mCurrentPage++;
                                } else {
                                    mContestList.clear();
                                    mAdapter.setData(mContestList);
                                    if (searchKeyword.length() != 0) {
                                        txtNoDataFoundContent.setText(getString(R.string.no_search_found));
                                    } else {
                                        txtNoDataFoundContent.setText(R.string.txt_no_contest_found);
                                    }
                                    showNoDataFound();
                                }
                            } else {

                                if (mCurrentPage == 0) {
                                    mContestList.clear();
                                    if (payload.fanContestAllArrayList != null) {
                                        mContestList.addAll(payload.fanContestAllArrayList);
                                    }

                                    if (mContestList.size() != 0) {
                                        llNoDataFound.setVisibility(View.GONE);
                                        rcItemListContest.setVisibility(View.VISIBLE);
                                        mAdapter.setData(mContestList);
                                    } else {
                                        showNoDataFound();
                                        if (searchKeyword.length() != 0) {
                                            txtNoDataFoundContent.setText(getString(R.string.no_search_found));
                                        } else {
                                            txtNoDataFoundContent.setText(R.string.txt_no_contest_found);
                                        }
                                        rcItemListContest.setVisibility(View.GONE);
                                    }
                                } else {
                                    AppLogger.error(TAG, "Remove loader...");
                                    mAdapter.removeLoader();
                                    if (payload.fanContestAllArrayList != null && payload.fanContestAllArrayList.size() != 0) {
                                        mContestList.addAll(payload.fanContestAllArrayList);
                                        mAdapter.setData(mContestList);
                                    }
                                }

                                if (payload.fanContestAllArrayList != null && payload.fanContestAllArrayList.size() != 0) {
                                    mCurrentPage++;
                                }
                            }
                            AppLogger.error(TAG, "item list size => " + mContestList.size());

                        } else if (mContestList == null || (mContestList != null && mContestList.size() == 0)) {
                            if (searchKeyword.length() != 0) {
                                txtNoDataFoundContent.setText(getString(R.string.no_search_found));
                            } else {
                                txtNoDataFoundContent.setText(R.string.txt_no_contest_found);
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


            @Override
            public void onFail(final Call<ApiResponse> call, Throwable t) {

                if (getActivity() != null) {
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
                            manageNoInternetConnectionLayout(getActivity(), rlConnectionContainer, new NetworkPopupEventListener() {
                                @Override
                                public void onOkClickListener(int reqCode) {
                                    rlContent.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onRetryClickListener(int reqCode) {
                                    getContestListFromServer(isRefreshing, searchKeyword);
                                }
                            }, 0);
                        } else {
                            Utils.showToastMessage(FanContestListActivity.this, getString(R.string.pls_check_ur_internet_connection));
                        }
                    }
                }
            }
        });
    }

    private void showNoDataFound() {
        llNoDataFound.setVisibility(View.VISIBLE);
        txtNoDataFoundTitle.setText("");
    }

    @Override
    public void onRefresh() {
        if (Utils.isConnectedToInternet(FanContestListActivity.this)) {
            getContestListFromServer(true, "");
        } else {
            swipeRefresh.setRefreshing(false);
            Utils.showToastMessage(FanContestListActivity.this, getString(R.string.pls_check_ur_internet_connection));
        }
    }


}
