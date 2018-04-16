package com.sticker_android.controller.fragment.corporate.contest;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.sticker_android.R;
import com.sticker_android.controller.fragment.base.BaseFragment;
import com.sticker_android.model.User;
import com.sticker_android.model.corporateproduct.Product;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.helper.PaginationScrollListener;
import com.sticker_android.utils.sharedpref.AppPref;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class CorporateContestOngoingFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {


    private RecyclerView recOngoingContestCorp;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AppPref appPref;
    private User mUserdata;
    private CorporateContestAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private static final int PAGE_START = 0;

    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 3;
    private int currentPage = PAGE_START;

    public CorporateContestOngoingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_corporate_contest_ongoing, container, false);
        init();
        getuserInfo();
        setViewReferences(view);
        setViewListeners();
        mAdapter = new CorporateContestAdapter(getActivity());
        recOngoingContestCorp.setAdapter(mAdapter);
        recyclerViewLayout();
        loadFirstPage();
        recListener();
        return view;
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
        recOngoingContestCorp = (RecyclerView) view.findViewById(R.id.recOngoingContestCorp);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
    }

    @Override
    protected boolean isValidData() {
        return false;
    }

    @Override
    public void onRefresh() {
        currentPage = 0;
        loadFirstPage();
    }

    /**
     * Method is used to set the layout on recycler view
     */
    private void recyclerViewLayout() {
        recOngoingContestCorp.hasFixedSize();

        mLayoutManager = new LinearLayoutManager(getContext());

        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recOngoingContestCorp.setLayoutManager(mLayoutManager);
    }


    public class CorporateContestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        // View Types
        private static final int ITEM = 0;
        private static final int LOADING = 1;

        private List<Product> productList;
        private Context context;

        private boolean isLoadingAdded = false;
        private boolean retryPageLoad = false;

        private String errorMsg;

        public CorporateContestAdapter(Context context) {
            this.context = context;
            productList = new ArrayList<>();
        }

        public List<Product> getProductList() {
            return productList;
        }

        public void setProduct(List<Product> productList) {
            this.productList = productList;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder viewHolder = null;
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());

            switch (viewType) {
                case ITEM:
                    View viewItem = inflater.inflate(R.layout.contest_view_ongoing, parent, false);
                    viewHolder = new CorporateContestAdapter.ProductVH(viewItem);
                    break;
                case LOADING:
                    View viewLoading = inflater.inflate(R.layout.progress_item, parent, false);
                    viewHolder = new CorporateContestAdapter.LoadingVH(viewLoading);
                    break;

            }
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            final Product product = productList.get(position); // Movie

            switch (getItemViewType(position)) {

                case ITEM:
                    final CorporateContestAdapter.ProductVH productVH = (CorporateContestAdapter.ProductVH) holder;
                    Glide.with(context)
                            .load(product.getImagePath()).fitCenter()
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    ((CorporateContestAdapter.ProductVH) holder).pgrImage.setVisibility(View.GONE);
                                    ((CorporateContestAdapter.ProductVH) holder).imvProductImage.setVisibility(View.GONE);

                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    ((CorporateContestAdapter.ProductVH) holder).pgrImage.setVisibility(View.GONE);
                                    ((CorporateContestAdapter.ProductVH) holder).imvProductImage.setVisibility(View.GONE);

                                    return false;
                                }
                            })
                            .into(((CorporateContestAdapter.ProductVH) holder).imvOfContest);

                    break;

                case LOADING:
                    CorporateContestAdapter.LoadingVH loadingVH = (CorporateContestAdapter.LoadingVH) holder;
                    if (retryPageLoad)
                        loadingVH.mProgressBar.setVisibility(View.GONE);
                    else
                        loadingVH.mProgressBar.setVisibility(View.VISIBLE);
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return productList == null ? 0 : productList.size();
        }

        @Override
        public int getItemViewType(int position) {

            return (position == productList.size() - 1 && isLoadingAdded) ? LOADING : ITEM;

        }


        public void add(Product r) {
            productList.add(r);
            notifyItemInserted(productList.size() - 1);
        }

        public void addAll(List<Product> moveResults) {
            for (Product result : moveResults) {
                add(result);
            }

            notifyDataSetChanged();
        }

        public void remove(Product r) {
            int position = productList.indexOf(r);
            if (position > -1) {
                productList.remove(position);
                notifyItemRemoved(position);
            }
        }

        public void clear() {
            isLoadingAdded = false;
            while (getItemCount() > 0) {
                remove(getItem(0));
            }
        }

        public boolean isEmpty() {
            return getItemCount() == 0;
        }


        public void addLoadingFooter() {
            isLoadingAdded = true;
            add(new Product());
        }

        public void removeLoadingFooter() {
            isLoadingAdded = false;

            int position = productList.size() - 1;
            Product result = getItem(position);

            if (result != null) {
                productList.remove(position);
                notifyItemRemoved(position);
            }
        }

        public Product getItem(int position) {
            return productList.get(position);
        }

        /**
         * Displays Pagination retry footer view along with appropriate errorMsg
         *
         * @param show
         * @param errorMsg to display if page load fails
         */
        public void showRetry(boolean show, @Nullable String errorMsg) {
            retryPageLoad = show;
            notifyItemChanged(productList.size() - 1);

            if (errorMsg != null) this.errorMsg = errorMsg;
        }


        /**
         * Main list's content ViewHolder
         */
        protected class ProductVH extends RecyclerView.ViewHolder {
            ImageView imvSelected, imvOfContest, imvProductImage;
            CardView cardItem;
            ProgressBar pgrImage;

            public ProductVH(View itemView) {
                super(itemView);
                cardItem = (CardView) itemView.findViewById(R.id.card_view);
                imvSelected = (ImageView) itemView.findViewById(R.id.imvSelected);
                imvOfContest = (ImageView) itemView.findViewById(R.id.imvOfContest);
                imvProductImage = (ImageView) itemView.findViewById(R.id.imvProductImage);
                pgrImage = (ProgressBar) itemView.findViewById(R.id.pgrImage);
            }
        }


        protected class LoadingVH extends RecyclerView.ViewHolder implements View.OnClickListener {
            private ProgressBar mProgressBar;


            public LoadingVH(View itemView) {
                super(itemView);

                mProgressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            }

            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.loadmore_retry:
                    case R.id.loadmore_errorlayout:

                        showRetry(false, null);

                        break;
                }
            }
        }

    }


    private void loadFirstPage() {
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(true);
        Call<ApiResponse> apiResponseCall = RestClient.getService().apiGetProductList(mUserdata.getLanguageId(), "", mUserdata.getId(),
                currentPage, TOTAL_PAGES, "ads", "product_list", "");
        apiResponseCall.enqueue(new ApiCall(getActivity()) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (swipeRefreshLayout != null)
                    swipeRefreshLayout.setRefreshing(false);
                if (apiResponse.status) {
                    isLoading = false;
                    if (apiResponse.paylpad.productList != null) {
                        mAdapter.addAll(apiResponse.paylpad.productList);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        mAdapter.removeLoadingFooter();
                        isLoading = false;
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

    private void recListener() {
        recOngoingContestCorp.addOnScrollListener(new PaginationScrollListener(mLayoutManager) {
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

    private void loadNextPage() {

        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(true);
        Call<ApiResponse> apiResponseCall = RestClient.getService().apiGetProductList(mUserdata.getLanguageId(), "", mUserdata.getId(),
                currentPage * TOTAL_PAGES, TOTAL_PAGES, "ads", "product_list", "");
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

}
