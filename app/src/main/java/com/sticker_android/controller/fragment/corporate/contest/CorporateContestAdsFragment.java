package com.sticker_android.controller.fragment.corporate.contest;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.sticker_android.controller.activities.common.contest.ApplyCorporateContestActivity;
import com.sticker_android.controller.adaptors.CorporateContestListAdapter;
import com.sticker_android.controller.fragment.base.BaseFragment;
import com.sticker_android.model.User;
import com.sticker_android.model.corporateproduct.Product;
import com.sticker_android.model.interfaces.MessageEventListener;
import com.sticker_android.model.interfaces.NetworkPopupEventListener;
import com.sticker_android.model.payload.Payload;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.AppLogger;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.sharedpref.AppPref;
import com.sticker_android.view.EndlessRecyclerViewScrollListener;

import java.util.ArrayList;

import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class CorporateContestAdsFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, CorporateContestListAdapter.OnProductItemClickListener {

    private ApplyCorporateContestActivity mHostActivity;

    private RecyclerView recAdsAndProductList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout llNoDataFound;
    private AppPref appPref;
    private User mUserdata;
    private TextView tvNoAdsUploaded;
    private LinearLayoutManager mLayoutManager;

    private Context mContext;
    private int mCurrentPage = 0;
    private int PAGE_LIMIT;
    private CorporateContestListAdapter mAdapter;
    private RelativeLayout rlContent;
    private LinearLayout llLoaderView;
    private RelativeLayout rlConnectionContainer;
    private TextView txtNoDataFoundTitle, txtNoDataFoundContent;
    private ArrayList<Product> mProductList;
    private static final String TAG = CorporateContestAdsFragment.class.getSimpleName();
    private View view;
    private EndlessRecyclerViewScrollListener scrollListener2;


    public CorporateContestAdsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        PAGE_LIMIT = mHostActivity.getResources().getInteger(R.integer.designed_item_page_limit);
        ;
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_corporate_contest_ads, container, false);
            init();
            getuserInfo();
            setViewReferences(view);
            setViewListeners();
            mAdapter = new CorporateContestListAdapter(getActivity());
            mAdapter.setProductItemClickListener(this);
            llNoDataFound.setVisibility(View.GONE);
            mProductList = new ArrayList<>();
            mCurrentPage = 0;
            getContestApi(false);
            recAdsAndProductList.setAdapter(mAdapter);
            recyclerViewLayout();
            recListener();


        return view;
    }





    private void recListener() {

        scrollListener2= new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public int getFooterViewType(int defaultNoFooterViewType) {

                return 0;
            }

            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                getContestApi(false);
                mAdapter.addLoader();
            }
        };
        // Adds the scroll listener to RecyclerView
        recAdsAndProductList.addOnScrollListener(scrollListener2);
    /*    recAdsAndProductList.addOnScrollListener(new PaginationScrollListener(mLayoutManager) {
            @Override
            protected void loadMoreItems() {
                AppLogger.debug(TAG, "Load more items");

                if (mProductList.size() >= PAGE_LIMIT) {
                    getContestApi(false);
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
        });*/
    }


    private void init() {

        appPref = new AppPref(getActivity());
    }

    private void getuserInfo() {
        mUserdata = appPref.getUserInfo();
    }


    /**
     * Method is used to set the layout on recycler view
     */
    private void recyclerViewLayout() {
        recAdsAndProductList.hasFixedSize();

        mLayoutManager = new LinearLayoutManager(getContext());

        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recAdsAndProductList.setLayoutManager(mLayoutManager);
    }


    @Override
    protected void setViewListeners() {
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    protected void setViewReferences(View view) {

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        recAdsAndProductList = (RecyclerView) view.findViewById(R.id.recAdsAndProductList);
        tvNoAdsUploaded = (TextView) view.findViewById(R.id.tvNoAdsUploaded);
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mHostActivity = (ApplyCorporateContestActivity) context;
    }

    @Override
    public void onRefresh() {
        if (Utils.isConnectedToInternet(mHostActivity)) {
            scrollListener2.resetState();
            getContestApi(true);
        } else {
            swipeRefreshLayout.setRefreshing(false);
            Utils.showToastMessage(mHostActivity, getString(R.string.pls_check_ur_internet_connection));
        }

    }

    private void getContestApi(final boolean isRefreshing) {

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

        Call<ApiResponse> apiResponseCall = RestClient.getService().apiGetProductList(mUserdata.getLanguageId(),mUserdata.getAuthrizedKey(), mUserdata.getId(),
                index, limit, "ads", "product_list", "","[2]");
        apiResponseCall.enqueue(new ApiCall(getActivity(), 1) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {

                if (isAdded() && getActivity() != null) {
                    llLoaderView.setVisibility(View.GONE);
                    rlContent.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);

                    //remove wi-fi symbol when response got
                    if (rlConnectionContainer != null && rlConnectionContainer.getChildCount() > 0) {
                        rlConnectionContainer.removeAllViews();
                    }

                    try {

                        if (apiResponse.status)
                        {

                            Payload payload = apiResponse.paylpad;

                            if (payload != null) {

                                if (isRefreshing) {

                                    if (payload.productList != null && payload.productList.size() != 0) {
                                        mProductList.clear();
                                        mProductList.addAll(payload.productList);
                                        if (mHostActivity != null) {
                                            mHostActivity.disablePost(true);
                                        }
                                        llNoDataFound.setVisibility(View.GONE);
                                        recAdsAndProductList.setVisibility(View.VISIBLE);
                                        mAdapter.setData(mProductList);

                                        mCurrentPage = 0;
                                        mCurrentPage++;
                                    } else {
                                        mProductList.clear();
                                        mAdapter.setData(mProductList);
                                        txtNoDataFoundContent.setText(R.string.no_ads_uploaded_yet);
                                        if (mHostActivity != null) {
                                            mHostActivity.disablePost(false);
                                        }
                                        showNoDataFound();
                                    }
                                } else {

                                    if (mCurrentPage == 0) {
                                        mProductList.clear();
                                        if (payload.productList != null) {
                                            mProductList.addAll(payload.productList);
                                        }

                                        if (mProductList.size() != 0) {
                                            llNoDataFound.setVisibility(View.GONE);
                                            recAdsAndProductList.setVisibility(View.VISIBLE);
                                            mAdapter.setData(mProductList);
                                            if (mHostActivity != null) {
                                                mHostActivity.disablePost(true);
                                            }
                                        } else {
                                            showNoDataFound();

                                            txtNoDataFoundContent.setText(R.string.no_ads_uploaded_yet);
                                            recAdsAndProductList.setVisibility(View.GONE);
                                        }
                                    } else {
                                        AppLogger.error(TAG, "Remove loader...");
                                        mAdapter.removeLoader();
                                        if (payload.productList != null && payload.productList.size() != 0) {
                                            mProductList.addAll(payload.productList);
                                            mAdapter.setData(mProductList);
                                            if (mHostActivity != null) {
                                                mHostActivity.disablePost(true);
                                            }
                                        }
                                    }

                                    if (payload.productList != null && payload.productList.size() != 0) {
                                        mCurrentPage++;
                                    }
                                }
                                AppLogger.error(TAG, "item list size => " + mProductList.size());

                            } else if (mProductList == null || (mProductList != null && mProductList.size() == 0)) {
                                txtNoDataFoundContent.setText(R.string.no_ads_uploaded_yet);
                                showNoDataFound();
                                if (mHostActivity != null) {
                                    mHostActivity.disablePost(false);
                                }
                            }
                        }else {
                            Toast.makeText(getActivity(),apiResponse.error.message,Toast.LENGTH_LONG).show();
                            Utils.showToast(getActivity(),apiResponse.error.message);
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
                    swipeRefreshLayout.setRefreshing(false);

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
                            if (mHostActivity != null) {
                                mHostActivity.disablePost(false);
                            }
                            mHostActivity.manageNoInternetConnectionLayout(mContext, rlConnectionContainer, new NetworkPopupEventListener() {
                                @Override
                                public void onOkClickListener(int reqCode) {
                                    rlContent.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onRetryClickListener(int reqCode) {
                                    getContestApi(isRefreshing);
                                }
                            }, 0);
                        } else {
                            Utils.showToastMessage(mHostActivity, getString(R.string.pls_check_ur_internet_connection));
                            if (mHostActivity != null) {
                                mHostActivity.disablePost(false);
                            }
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

    public void updateTheFragment() {

        if (mProductList != null && mProductList.size() != 0) {
            swipeRefreshLayout.setRefreshing(true);
            getContestApi(true);
        } else {
            getContestApi(false);
        }
    }

    @Override
    public void onProductItemClick(Product product) {

        if (mHostActivity != null) {
            mHostActivity.saveContest(product);
        }
    }
}
