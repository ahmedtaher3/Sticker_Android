package com.sticker_android.controller.fragment.fan.fanhome.fanfilter;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sticker_android.R;
import com.sticker_android.controller.activities.fan.home.imagealbum.ImageAlbumStickers.ImageAlbumStickers;
import com.sticker_android.controller.adaptors.FanCommonFilterList;
import com.sticker_android.controller.fragment.base.BaseFragment;
import com.sticker_android.controller.fragment.fan.fanhome.FanHomeFragment;
import com.sticker_android.model.User;
import com.sticker_android.model.filter.FanFilter;
import com.sticker_android.model.interfaces.NetworkPopupEventListener;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.AppLogger;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.helper.PaginationScrollListener;
import com.sticker_android.utils.sharedpref.AppPref;
import com.sticker_android.view.GridSpacingItemDecoration;

import java.util.ArrayList;

import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class FanFilterAdminStickerFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView rcItemStickers;
    private LinearLayout llNoDataFound;
    private SwipeRefreshLayout swipeRefresh;
    private RelativeLayout rlContent;
    private LinearLayout llLoaderView;
    private RelativeLayout rlConnectionContainer;
    private TextView txtNoDataFoundTitle, txtNoDataFoundContent;

    private final String TAG = FanFilterAdminStickerFragment.class.getSimpleName();
    private Context mContext;
    private ImageAlbumStickers mHostActivity;

    private View inflatedView;
    private GridLayoutManager mLinearLayoutManager;
    private ArrayList<FanFilter> albumArrayList;
    private User mLoggedUser;

    private int mCurrentPage = 0;
    private int PAGE_LIMIT;
    private FanCommonFilterList mAdapter;
    private String categories = "";
    String filterData = "";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;


    public FanFilterAdminStickerFragment() {
        // Required empty public constructor
    }
    // TODO: Rename and change types and number of parameters
    public static FanFilterAdminStickerFragment newInstance(String param1) {
        FanFilterAdminStickerFragment fragment = new FanFilterAdminStickerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fan_filter_admin_sticker, container, false);

        init();
        PAGE_LIMIT = getActivity().getResources().getInteger(R.integer.designed_item_page_limit);
        setViewReferences(view);
        setViewListeners();
        initRecyclerView();
        mAdapter = new FanCommonFilterList(getActivity(),mParam1);
        rcItemStickers.setAdapter(mAdapter);
        llNoDataFound.setVisibility(View.GONE);
        albumArrayList = new ArrayList<>();
        mCurrentPage = 0;
        getDesignFromServer(false,"");
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

        rcItemStickers = (RecyclerView) view.findViewById(R.id.rcItemStickers);
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
        rcItemStickers.setHasFixedSize(true);

        mLinearLayoutManager = new GridLayoutManager(mContext,4, LinearLayoutManager.VERTICAL,false);
        // use a linear layout manager
        rcItemStickers.setLayoutManager(mLinearLayoutManager);
        int spanCount = 4; // 3 columns
        int spacing = 5; // 50px
        boolean includeEdge = false;

        rcItemStickers.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
        rcItemStickers.setNestedScrollingEnabled(true);

    }

    private void showNoDataFound() {
        llNoDataFound.setVisibility(View.VISIBLE);
        txtNoDataFoundTitle.setText("");
    }

    @Override
    public void onRefresh() {
        categories = "";
        mCurrentPage = 0;
        if (Utils.isConnectedToInternet(mHostActivity)) {
            // scrollListener2.resetState();
            getDesignFromServer(true, "");
        } else {
            swipeRefresh.setRefreshing(false);
            Utils.showToastMessage(mHostActivity, getString(R.string.pls_check_ur_internet_connection));
        }
        FanHomeFragment parentFrag = ((FanHomeFragment) FanFilterAdminStickerFragment.this.getParentFragment());
        if (parentFrag != null)
            parentFrag.closeSearch();
    }

    public void setRecScrollListener() {

        rcItemStickers.addOnScrollListener(new PaginationScrollListener(mLinearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                AppLogger.debug(TAG, "Load more items");

                if (albumArrayList.size() >= PAGE_LIMIT) {
                    AppLogger.debug(TAG, "page limit" + PAGE_LIMIT + " list size" + albumArrayList.size());
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
        Call<ApiResponse> apiResponseCall = RestClient.getService().apiFilterList(mLoggedUser.getLanguageId(), mLoggedUser.getAuthrizedKey(),
                mLoggedUser.getId(), 0, 1000000, "", "filter_list", mParam1);

        /*Call<ApiResponse> apiResponseCall = RestClient.getService().getFanHomeProductList(mLoggedUser.getLanguageId(), mLoggedUser.getAuthrizedKey(), mLoggedUser.getId(),
                index, limit, DesignType.stickers.getType().toLowerCase(Locale.ENGLISH), "all_product_list", searchKeyword);
        */
        apiResponseCall.enqueue(new ApiCall(getActivity(), 1) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (isRefreshing) {
                    swipeRefresh.setRefreshing(false);
                } else {
                    llLoaderView.setVisibility(View.GONE);
                }
                if (apiResponse.status) {
                    if (apiResponse.paylpad.fanFilterArrayList != null) {

                        mAdapter.setData(apiResponse.paylpad.fanFilterArrayList);
                    }
                }



              /*  if (isAdded() && getActivity() != null) {
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

                                    if (payload.fanFilterArrayList != null && payload.fanFilterArrayList.size() != 0) {
                                        albumArrayList.clear();
                                        albumArrayList.addAll(payload.fanFilterArrayList);

                                        llNoDataFound.setVisibility(View.GONE);
                                        rcItemStickers.setVisibility(View.VISIBLE);
                                        mAdapter.setData(albumArrayList);

                                        mCurrentPage = 0;
                                        mCurrentPage++;
                                    } else {
                                        albumArrayList.clear();
                                        mAdapter.setData(albumArrayList);
                                        if (searchKeyword.length() != 0) {
                                            txtNoDataFoundContent.setText(getString(R.string.no_search_found));
                                        } else {
                                            txtNoDataFoundContent.setText(R.string.no_stickers_uploaded_yet);
                                        }
                                        showNoDataFound();
                                    }
                                } else {
                                    AppLogger.error(TAG, "else callled");
                                    if (mCurrentPage == 0) {
                                        albumArrayList.clear();
                                        if (payload.productListAll != null) {
                                            albumArrayList.addAll(payload.fanFilterArrayList);
                                        }

                                        if (albumArrayList.size() != 0) {
                                            llNoDataFound.setVisibility(View.GONE);
                                            rcItemStickers.setVisibility(View.VISIBLE);
                                            mAdapter.setData(albumArrayList);
                                        } else {
                                            showNoDataFound();
                                            if (searchKeyword.length() != 0) {
                                                txtNoDataFoundContent.setText(getString(R.string.no_search_found));
                                            } else {
                                                txtNoDataFoundContent.setText(R.string.no_stickers_uploaded_yet);
                                            }
                                            rcItemStickers.setVisibility(View.GONE);
                                        }
                                    } else {
                                        AppLogger.error(TAG, "Remove loader...");
                                        mAdapter.removeLoader();
                                        if (payload.productListAll != null && payload.fanFilterArrayList.size() != 0) {
                                            albumArrayList.addAll(payload.fanFilterArrayList);
                                            mAdapter.setData(albumArrayList);
                                        }
                                    }

                                    if (payload.fanFilterArrayList != null && payload.fanFilterArrayList.size() != 0) {
                                        mCurrentPage++;
                                    }
                                }
                                AppLogger.error(TAG, "item list size => " + albumArrayList.size());

                            } else if (albumArrayList == null || (albumArrayList != null && albumArrayList.size() == 0)) {
                                if (searchKeyword.length() != 0) {
                                    txtNoDataFoundContent.setText(getString(R.string.no_search_found));
                                } else {
                                    txtNoDataFoundContent.setText(R.string.no_stickers_uploaded_yet);
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
*/
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
                                    getDesignFromServer(isRefreshing, searchKeyword );
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
        mHostActivity = (ImageAlbumStickers) context;
    }




    /**/



/**/
}
