package com.sticker_android.controller.fragment.corporate.contentapproval;

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

import com.sticker_android.R;
import com.sticker_android.controller.activities.corporate.home.CorporateHomeActivity;
import com.sticker_android.controller.adaptors.ContentForApprovalAdapter;
import com.sticker_android.controller.fragment.base.BaseFragment;
import com.sticker_android.model.User;
import com.sticker_android.model.corporateproduct.Product;
import com.sticker_android.model.enums.DesignType;
import com.sticker_android.model.interfaces.DesignerActionListener;
import com.sticker_android.model.interfaces.MessageEventListener;
import com.sticker_android.model.interfaces.NetworkPopupEventListener;
import com.sticker_android.model.payload.Payload;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.AppLogger;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.sharedpref.AppPref;

import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;

/**
 * Created by user on 18/4/18.
 */

public class CorporateContentApprovalProductFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {


    private CorporateHomeActivity mHostActivity;

    private RecyclerView recContentApproval;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout llNoDataFound;
    private AppPref appPref;
    private User mUserdata;
    private TextView tvNoAdsUploaded;
    private LinearLayoutManager mLayoutManager;
    private Context mContext;
    private ContentForApprovalAdapter mAdapter;
    private RelativeLayout rlContent;
    private LinearLayout llLoaderView;
    private RelativeLayout rlConnectionContainer;
    private TextView txtNoDataFoundTitle, txtNoDataFoundContent;
    private ArrayList<Product> mProductList;
    private static final String TAG = CorporateContentApprovalAdsFragment.class.getSimpleName();
    private View view;
    private int mCurrentPage = 0;
    private int PAGE_LIMIT;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        PAGE_LIMIT = mHostActivity.getResources().getInteger(R.integer.designed_item_page_limit);

        View view = inflater.inflate(R.layout.frag_content_approval, container, false);
        init();
        getuserInfo();
        setViewReferences(view);
        setViewListeners();
        //  mAdapter     =    new CorporateContentApproval(getActivity());
        mAdapter = new ContentForApprovalAdapter(getActivity());
        mAdapter.setDesignerActionListener(new DesignerActionListener() {
            @Override
            public void onEdit(Product product) {

            }

            @Override
            public void onRemove(Product product) {
                mAdapter.notifyDataSetChanged();
                onRefresh();
            }

            @Override
            public void onResubmit(Product product) {

            }
        });
        llNoDataFound.setVisibility(View.GONE);
        mProductList = new ArrayList<>();
        getDesignFromServer(false);
        recContentApproval.setAdapter(mAdapter);
        recyclerViewLayout();
        return view;
    }

    private void getContentApi() {

        mProductList.add(new Product());
        mAdapter.setData(mProductList);
    }

    private void init() {

        appPref = new AppPref(getActivity());
    }

    private void getuserInfo() {
        mUserdata = appPref.getUserInfo();
    }

    @Override
    protected void setViewListeners() {
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    protected void setViewReferences(View view) {
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        recContentApproval = (RecyclerView) view.findViewById(R.id.recContentApproval);
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

    private void getDesignFromServer(final boolean isRefreshing) {

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

        Call<ApiResponse> apiResponseCall = RestClient.getService().getUserPendingList(mUserdata.getLanguageId(), mUserdata.getAuthrizedKey(), mUserdata.getId(),
                index, limit, DesignType.products.getType().toLowerCase(Locale.ENGLISH), "product_list","[1]");
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
                        if (apiResponse.status) {
                            Payload payload = apiResponse.paylpad;

                            if (payload != null) {

                                if (isRefreshing) {

                                    if (payload.productList != null && payload.productList.size() != 0) {
                                        mProductList.clear();
                                        mProductList.addAll(payload.productList);

                                        llNoDataFound.setVisibility(View.GONE);
                                        recContentApproval.setVisibility(View.VISIBLE);
                                        mAdapter.setData(mProductList);

                                        mCurrentPage = 0;
                                        mCurrentPage++;
                                    } else {
                                        mProductList.clear();
                                        mAdapter.setData(mProductList);

                                        txtNoDataFoundContent.setText(R.string.no_product_uploaded_yet);

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
                                            recContentApproval.setVisibility(View.VISIBLE);
                                            mAdapter.setData(mProductList);
                                        } else {
                                            showNoDataFound();

                                            txtNoDataFoundContent.setText(R.string.no_product_uploaded_yet);

                                            recContentApproval.setVisibility(View.GONE);
                                        }
                                    } else {
                                        AppLogger.error(TAG, "Remove loader...");
                                        mAdapter.removeLoader();
                                        if (payload.productList != null && payload.productList.size() != 0) {
                                            mProductList.addAll(payload.productList);
                                            mAdapter.setData(mProductList);
                                        }
                                    }

                                    if (payload.productList != null && payload.productList.size() != 0) {
                                        mCurrentPage++;
                                    }
                                }
                                AppLogger.error(TAG, "item list size => " + mProductList.size());

                            } else if (mProductList == null || (mProductList != null && mProductList.size() == 0)) {

                                txtNoDataFoundContent.setText(R.string.no_product_uploaded_yet);

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
                            mHostActivity.manageNoInternetConnectionLayout(mContext, rlConnectionContainer, new NetworkPopupEventListener() {
                                @Override
                                public void onOkClickListener(int reqCode) {
                                    rlContent.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onRetryClickListener(int reqCode) {
                                    getDesignFromServer(isRefreshing);
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

    private void showNoDataFound() {
        llNoDataFound.setVisibility(View.VISIBLE);
        txtNoDataFoundTitle.setText("");
    }

    @Override
    public void onRefresh() {
        if (Utils.isConnectedToInternet(mHostActivity)) {
            getDesignFromServer(true);
        } else {
            swipeRefreshLayout.setRefreshing(false);
            Utils.showToastMessage(mHostActivity, getString(R.string.pls_check_ur_internet_connection));
        }
    }

    /**
     * Method is used to set the layout on recycler view
     */
    private void recyclerViewLayout() {

        recContentApproval.hasFixedSize();

        mLayoutManager = new LinearLayoutManager(getContext());

        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recContentApproval.setLayoutManager(mLayoutManager);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mHostActivity = (CorporateHomeActivity) context;
    }

}
