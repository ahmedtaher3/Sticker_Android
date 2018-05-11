package com.sticker_android.controller.fragment.fan.fanhome;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sticker_android.R;
import com.sticker_android.controller.activities.fan.home.FanHomeActivity;
import com.sticker_android.controller.adaptors.ContestListAdaptor;
import com.sticker_android.controller.fragment.base.BaseFragment;
import com.sticker_android.model.User;
import com.sticker_android.model.contest.FanContest;
import com.sticker_android.model.corporateproduct.Product;
import com.sticker_android.model.interfaces.NetworkPopupEventListener;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.sharedpref.AppPref;

import java.util.ArrayList;

import retrofit2.Call;

/**
 * Created by user on 23/4/18.
 */

public class FanContestFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView rcDesignList;
    private LinearLayout llNoDataFound;
    private SwipeRefreshLayout swipeRefresh;
    private RelativeLayout rlContent;
    private LinearLayout llLoaderView;
    private RelativeLayout rlConnectionContainer;
    private TextView txtNoDataFoundTitle, txtNoDataFoundContent;

    private final String TAG = FanContestFragment.class.getSimpleName();
    private Context mContext;
    private FanHomeActivity mHostActivity;

    private View inflatedView;
    private LinearLayoutManager mLinearLayoutManager;
    private ArrayList<Product> mStickerList;
    private User mLoggedUser;

    private int mCurrentPage = 0;
    private int PAGE_LIMIT;
    private ContestListAdaptor mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fan_home_common, container, false);
        init();
        // PAGE_LIMIT = getActivity().getResources().getInteger(R.integer.designed_item_page_limit);
        setViewReferences(view);
        setViewListeners();
        initRecyclerView();
        mAdapter = new ContestListAdaptor(getActivity());
        rcDesignList.setAdapter(mAdapter);
        llNoDataFound.setVisibility(View.GONE);
        mStickerList = new ArrayList<>();
        mCurrentPage = 0;
        getContestListApi(false);
        return view;
    }

    private void getContestListApi(final boolean isRefresh) {

        if (isRefresh) {
            swipeRefresh.setRefreshing(true);
            llLoaderView.setVisibility(View.GONE);
        } else llLoaderView.setVisibility(View.VISIBLE);
        Call<ApiResponse> apiResponseCall = RestClient.getService().getFanContestList(mLoggedUser.getLanguageId(), mLoggedUser.getAuthrizedKey(), mLoggedUser.getId(), "fan_contest_list");

        apiResponseCall.enqueue(new ApiCall(getActivity()) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (isRefresh)
                    swipeRefresh.setRefreshing(false);

                if (apiResponse.status) {
                    mAdapter.setData(apiResponse.paylpad.fanContestList);
                    mAdapter.notifyDataSetChanged();
                    llLoaderView.setVisibility(View.GONE);

                }
                if (apiResponse.paylpad.fanContestList == null) {
                    llNoDataFound.setVisibility(View.VISIBLE);
                    showNoDataFound();
                    txtNoDataFoundContent.setText(R.string.txt_no_contest_found);
                } else {
                    llNoDataFound.setVisibility(View.GONE);
                    rlContent.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFail(Call<ApiResponse> call, Throwable t) {
                if (isRefresh)
                    swipeRefresh.setRefreshing(false);
                txtNoDataFoundContent.setVisibility(View.GONE);
                llNoDataFound.setVisibility(View.GONE);
                if (!call.isCanceled() && (t instanceof java.net.ConnectException ||
                        t instanceof java.net.SocketTimeoutException ||
                        t instanceof java.net.SocketException ||
                        t instanceof java.net.UnknownHostException)) {

                    mHostActivity.manageNoInternetConnectionLayout(mContext, rlConnectionContainer, new NetworkPopupEventListener() {
                        @Override
                        public void onOkClickListener(int reqCode) {
                            rlContent.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onRetryClickListener(int reqCode) {
                            getContestListApi(false);
                        }
                    }, 0);
                } else {
                    Utils.showToastMessage(mHostActivity, getString(R.string.pls_check_ur_internet_connection));
                }
            }

        });
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

    public void filterData() {
        Toast.makeText(getActivity(), "filter data called", Toast.LENGTH_SHORT).show();
    }

    private void showNoDataFound() {
        llNoDataFound.setVisibility(View.VISIBLE);
        txtNoDataFoundTitle.setText("");
    }


    @Override
    public void onRefresh() {
        if (Utils.isConnectedToInternet(mHostActivity)) {
            getContestListApi(true);
        } else {
            swipeRefresh.setRefreshing(false);
            Utils.showToastMessage(mHostActivity, getString(R.string.pls_check_ur_internet_connection));
        }

        FanHomeFragment parentFrag = ((FanHomeFragment) FanContestFragment.this.getParentFragment());
        if (parentFrag != null)
            parentFrag.closeSearch();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mHostActivity = (FanHomeActivity) context;
    }

    public void searchData(String trim) {

        rcDesignList.setVisibility(View.VISIBLE);
        if (mStickerList != null) {
            ArrayList<FanContest> tempList = mAdapter.filter(trim);
            if (tempList != null) {
                if (tempList.size() == 0) {
                    llNoDataFound.setVisibility(View.VISIBLE);
                    txtNoDataFoundContent.setText(R.string.txt_no_contest_found);
                    showNoDataFound();
                    rcDesignList.setVisibility(View.GONE);

                }
            } else {
                llNoDataFound.setVisibility(View.VISIBLE);
                txtNoDataFoundContent.setText(R.string.txt_no_contest_found);
                showNoDataFound();

                rcDesignList.setVisibility(View.GONE);
            }

        }

    }
}
