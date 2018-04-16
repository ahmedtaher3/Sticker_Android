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
import android.widget.TextView;
import android.widget.Toast;

import com.sticker_android.R;
import com.sticker_android.controller.adaptors.CorpAd.CorporateContestAdapter;
import com.sticker_android.controller.adaptors.CorpAd.PaginationAdapterCallback;
import com.sticker_android.controller.fragment.base.BaseFragment;
import com.sticker_android.model.User;
import com.sticker_android.model.corporateproduct.Product;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.helper.PaginationScrollListener;
import com.sticker_android.utils.sharedpref.AppPref;

import java.util.ArrayList;

import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class CorporateContestProductFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, PaginationAdapterCallback,CorporateContestAdapter.OnProductItemClickListener {


    private RecyclerView recAdsAndProductList;
    private SwipeRefreshLayout swipeRefreshLayout;

    private AppPref appPref;
    private User mUserdata;
    ArrayList<Product> productList = new ArrayList<>();
    private CharSequence searchKeyword = "";
    private TextView tvNoAdsUploaded;
    private LinearLayoutManager mLayoutManager;
    private static final int PAGE_START = 0;

    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 3;
    private int currentPage = PAGE_START;
    private CorporateContestAdapter mAdapter;
    private Context mContext;

    public CorporateContestProductFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_corporate_contest_product, container, false);
        init();
        getuserInfo();
        setViewReferences(view);
        setViewListeners();
        mAdapter = new CorporateContestAdapter(mContext);
        mAdapter.setCorporateActionListener(this);
        mAdapter.setOnProductClickListener(this);
        recAdsAndProductList.setAdapter(mAdapter);
        recyclerViewLayout();
        loadFirstPage();
        recListener();
        return view;
    }
    private void recListener() {
        recAdsAndProductList.addOnScrollListener(new PaginationScrollListener(mLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                loadNextPage();
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public int getThresholdValue() {
                return 0;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

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
    }

    @Override
    protected boolean isValidData() {
        return false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onRefresh() {
        currentPage = 0;
        loadFirstPage();
    }

    @Override
    public void retryPageLoad() {
        loadNextPage();
    }


    private void loadFirstPage() {
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(true);
        Call<ApiResponse> apiResponseCall = RestClient.getService().apiGetProductList(mUserdata.getLanguageId(), "", mUserdata.getId(),
                currentPage, TOTAL_PAGES, "product", "product_list", "");
        apiResponseCall.enqueue(new ApiCall(getActivity()) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (swipeRefreshLayout != null)
                    swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), "called", Toast.LENGTH_SHORT).show();
                if (apiResponse.status) {
                  Toast.makeText(getActivity(),"current page"+currentPage+"total pages",Toast.LENGTH_SHORT).show();
                    isLoading = false;
                    if (apiResponse.paylpad.productList != null) {
                        mAdapter.addAll(apiResponse.paylpad.productList);
                        mAdapter.notifyDataSetChanged();
                    }
                    if (currentPage <= TOTAL_PAGES) mAdapter.addLoadingFooter();
                    else isLastPage = true;

                }
            }

            @Override
            public void onFail(Call<ApiResponse> call, Throwable t) {
                if (swipeRefreshLayout != null)
                    swipeRefreshLayout.setRefreshing(false);

            }
        });


    }


    private void loadNextPage() {

        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(true);
        Call<ApiResponse> apiResponseCall = RestClient.getService().apiGetProductList(mUserdata.getLanguageId(), "", mUserdata.getId(),
                currentPage * TOTAL_PAGES, TOTAL_PAGES, "product", "product_list", "");
        apiResponseCall.enqueue(new ApiCall(getActivity()) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (swipeRefreshLayout != null)
                    swipeRefreshLayout.setRefreshing(false);
                if (apiResponse.status) {

                    if (apiResponse.paylpad.productList != null && apiResponse.paylpad.productList.size() > 0)
                        mAdapter.addAll(apiResponse.paylpad.productList);

                    mAdapter.removeLoadingFooter();
                    isLoading = false;
                    if (currentPage != TOTAL_PAGES) mAdapter.addLoadingFooter();
                    else isLastPage = true;
                }
            }

            @Override
            public void onFail(Call<ApiResponse> call, Throwable t) {
                if (swipeRefreshLayout != null)
                    swipeRefreshLayout.setRefreshing(false);

            }
        });


    }

    @Override
    public void onProductItemClick(Product product) {

        Toast.makeText(getActivity(),"scsdc",Toast.LENGTH_SHORT).show();
    }
}
