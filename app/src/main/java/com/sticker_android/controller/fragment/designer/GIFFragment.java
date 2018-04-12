package com.sticker_android.controller.fragment.designer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.sticker_android.R;
import com.sticker_android.constant.AppConstant;
import com.sticker_android.controller.activities.designer.addnew.AddNewDesignActivity;
import com.sticker_android.controller.activities.designer.home.DesignerHomeActivity;
import com.sticker_android.controller.adaptors.DesignListAdapter;
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
import com.sticker_android.utils.helper.PaginationScrollListener;
import com.sticker_android.utils.sharedpref.AppPref;

import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;

/**
 * Created by satyendra on 4/9/18.
 */

public class GIFFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        DesignListAdapter.OnProductItemClickListener, DesignerActionListener {

    private RecyclerView rcDesignList;
    private LinearLayout llNoDataFound;
    private SwipeRefreshLayout swipeRefresh;
    private RelativeLayout rlContent;
    private LinearLayout llLoaderView;
    private RelativeLayout rlConnectionContainer;
    private TextView txtNoDataFoundTitle, txtNoDataFoundContent;

    private final String TAG = GIFFragment.class.getSimpleName();
    private Context mContext;
    private DesignerHomeActivity mHostActivity;

    private View inflatedView;
    private LinearLayoutManager mLinearLayoutManager;
    private DesignListAdapter mAdapter;
    private ArrayList<Product> mGifList;
    private User mLoggedUser;

    private int mCurrentPage = 0;
    private int PAGE_LIMIT;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mHostActivity = (DesignerHomeActivity) context;
        mLoggedUser = new AppPref(mContext).getUserInfo();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        AppLogger.error(TAG, "Inside onCreateView() method");

        PAGE_LIMIT = mHostActivity.getResources().getInteger(R.integer.designed_item_page_limit);

        if (inflatedView == null) {
            inflatedView = LayoutInflater.from(mContext).inflate(R.layout.layout_design_item_list, container, false);

            setViewReferences();
            initRecyclerView();
            setListenerOnViews();
            swipeRefresh.setOnRefreshListener(this);

            mAdapter = new DesignListAdapter(mHostActivity);
            rcDesignList.setAdapter(mAdapter);

            mAdapter.setDesignerActionListener(this);
            mAdapter.setOnProductClickListener(this);

            llNoDataFound.setVisibility(View.GONE);
            mGifList = new ArrayList<>();
            mCurrentPage = 0;
            getDesignFromServer(false, "");

        } else {
            if (inflatedView.getParent() != null)
                ((ViewGroup) inflatedView.getParent()).removeView(inflatedView);
            updateTheFragment();
        }

        AppLogger.debug(TAG, "Outside onCreateView() method");
        return inflatedView;
    }

    public void updateTheFragment() {

        if(mGifList != null && mGifList.size() != 0){
            swipeRefresh.setRefreshing(true);
            getDesignFromServer(true, "");
        }
        else{
            getDesignFromServer(false, "");
        }
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

    public void searchByKeyword(String keyword){
        mCurrentPage = 0;
        mGifList.clear();
        mAdapter.setData(mGifList);
        getDesignFromServer(false, keyword);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void setViewReferences() {
        rcDesignList = (RecyclerView) inflatedView.findViewById(R.id.rcItemList);
        swipeRefresh = (SwipeRefreshLayout) inflatedView.findViewById(R.id.swiperefresh);
        rlContent = (RelativeLayout) inflatedView.findViewById(R.id.rlContent);
        llNoDataFound = (LinearLayout) inflatedView.findViewById(R.id.llNoDataFound);
        txtNoDataFoundTitle = (TextView) inflatedView.findViewById(R.id.txtNoDataFoundTitle);
        txtNoDataFoundContent = (TextView) inflatedView.findViewById(R.id.txtNoDataFoundContent);
        rlConnectionContainer = (RelativeLayout) inflatedView.findViewById(R.id.rlConnectionContainer);
        llLoaderView = (LinearLayout) inflatedView.findViewById(R.id.llLoader);
    }

    public void setListenerOnViews() {

        rcDesignList.addOnScrollListener(new PaginationScrollListener(mLinearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                AppLogger.debug(TAG, "Load more items");

                if (mGifList.size() >= PAGE_LIMIT) {
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

    public void addNewGIF(Product gif){

        if(gif != null){
            llNoDataFound.setVisibility(View.GONE);
            rcDesignList.setVisibility(View.VISIBLE);

            mGifList.add(0, gif);
            mAdapter.updateAdapterData(mGifList);
            mAdapter.notifyItemInserted(0);
            rcDesignList.smoothScrollToPosition(0);
        }
    }

    public void editGif(Product product){

        if(product != null){
            int index = mGifList.indexOf(product);
            if(index != -1){
                mGifList.set(index, product);
                mAdapter.updateModifiedItem(product);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AppLogger.error(TAG, "Inside onActivityResult()");
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

        Call<ApiResponse> apiResponseCall = RestClient.getService().apiGetProductList(mLoggedUser.getLanguageId(), "", mLoggedUser.getId(),
                index, limit, DesignType.gif.getType().toLowerCase(Locale.ENGLISH), "product_list", searchKeyword);
        apiResponseCall.enqueue(new ApiCall(getActivity(), 1) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {

                if (isAdded() && getActivity() != null) {
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

                                    if (payload.productList != null && payload.productList.size() != 0) {
                                        mGifList.clear();
                                        mGifList.addAll(payload.productList);

                                        llNoDataFound.setVisibility(View.GONE);
                                        rcDesignList.setVisibility(View.VISIBLE);
                                        mAdapter.setData(mGifList);

                                        mCurrentPage = 0;
                                        mCurrentPage++;
                                    } else {
                                        mGifList.clear();
                                        mAdapter.setData(mGifList);
                                        if(searchKeyword.length() != 0){
                                            txtNoDataFoundContent.setText(getString(R.string.no_search_found));
                                        }
                                        else{
                                            txtNoDataFoundContent.setText(R.string.no_gif_uploaded_yet);
                                        }
                                        showNoDataFound();
                                    }
                                } else {

                                    if (mCurrentPage == 0) {
                                        mGifList.clear();
                                        if(payload.productList != null){
                                            mGifList.addAll(payload.productList);
                                        }

                                        if (mGifList.size() != 0) {
                                            llNoDataFound.setVisibility(View.GONE);
                                            rcDesignList.setVisibility(View.VISIBLE);
                                            mAdapter.setData(mGifList);
                                        } else {
                                            showNoDataFound();
                                            if(searchKeyword.length() != 0){
                                                txtNoDataFoundContent.setText(getString(R.string.no_search_found));
                                            }
                                            else{
                                                txtNoDataFoundContent.setText(R.string.no_gif_uploaded_yet);
                                            }
                                            rcDesignList.setVisibility(View.GONE);
                                        }
                                    } else {
                                        AppLogger.error(TAG, "Remove loader...");
                                        mAdapter.removeLoader();
                                        if (payload.productList != null && payload.productList.size() != 0) {
                                            mGifList.addAll(payload.productList);
                                            mAdapter.setData(mGifList);
                                        }
                                    }

                                    if (payload.productList != null && payload.productList.size() != 0) {
                                        mCurrentPage++;
                                    }
                                }
                                AppLogger.error(TAG, "item list size => " + mGifList.size());

                            } else if (mGifList == null || (mGifList != null && mGifList.size() == 0)) {
                                if(searchKeyword.length() != 0){
                                    txtNoDataFoundContent.setText(getString(R.string.no_search_found));
                                }
                                else{
                                    txtNoDataFoundContent.setText(R.string.no_gif_uploaded_yet);
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
                    if (!call.isCanceled() && (t instanceof java.net.ConnectException || t instanceof java.net.SocketTimeoutException || t instanceof java.net.SocketException || t instanceof java.net.UnknownHostException)) {

                        if (mCurrentPage == 0) {
                            mHostActivity.manageNoInternetConnectionLayout(mContext, rlConnectionContainer, new NetworkPopupEventListener() {
                                @Override
                                public void onOkClickListener(int reqCode) {
                                    rlContent.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onRetryClickListener(int reqCode) {
                                    getDesignFromServer(isRefreshing, searchKeyword);
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
            getDesignFromServer(true, "");
        } else {
            Utils.showToastMessage(mHostActivity, getString(R.string.pls_check_ur_internet_connection));
        }
    }

    @Override
    public void onProductItemClick(Product product) {

    }

    @Override
    public void onEdit(Product product) {
        Intent intent = new Intent(getActivity(), AddNewDesignActivity.class);
        intent.putExtra(AppConstant.PRODUCT, product);
        startActivityForResult(intent, DesignerHomeFragment.EDIT_DESIGN);
    }

    @Override
    public void onRemove(Product product) {
        if(product != null){
            int index = mGifList.indexOf(product);

            if(index != -1){
                mGifList.remove(index);
                mAdapter.removeProductData(index);
            }
        }
    }

    @Override
    public void onResubmit(Product product) {

    }
}
