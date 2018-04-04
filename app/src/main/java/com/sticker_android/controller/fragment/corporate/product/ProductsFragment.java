package com.sticker_android.controller.fragment.corporate.product;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sticker_android.R;
import com.sticker_android.constant.AppConstant;
import com.sticker_android.controller.activities.corporate.RenewAdandProductActivity;
import com.sticker_android.controller.fragment.base.BaseFragment;
import com.sticker_android.controller.fragment.corporate.ad.AdsFragment;
import com.sticker_android.model.User;
import com.sticker_android.model.corporateproduct.ProductList;
import com.sticker_android.model.interfaces.OnLoadMoreListener;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.ProgressDialogHandler;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.helper.TimeUtility;
import com.sticker_android.utils.sharedpref.AppPref;
import com.sticker_android.view.OnVerticalScrollListener;

import java.util.ArrayList;

import retrofit2.Call;


public class ProductsFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recAd;
    private ProgressBar progressBarLoadMore;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayoutManager mLayoutManager;
    private OnVerticalScrollListener scrollListener;
    private boolean loading = true;
    private boolean isLastPage = true;
    private ProductDataAdapter mAdapter;
    protected Handler handler;
    private AppPref appPref;
    private User mUserdata;
    ArrayList<ProductList> productList = new ArrayList<>();
    private TimeUtility timeUtility=new TimeUtility();


    public ProductsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_products, container, false);
        init();
        getuserInfo();
        setViewReferences(view);
        setViewListeners();
        recyclerViewLayout();
        handler = new Handler();
        productListApi(0);
        setAdaptor();
        adaptorScrollListener();
        return view;
    }

    private void init() {

        appPref = new AppPref(getActivity());
    }

    private void getuserInfo() {
        mUserdata = appPref.getUserInfo();
    }

    private void adaptorScrollListener() {

        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                //add null , so the adapter will check view_type and show progress bar at bottom
                // productList.add(null);
                progressBarLoadMore.setVisibility(View.VISIBLE);
                if(productList.size()>5)
                    mAdapter.notifyItemInserted(productList.size() - 1);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressBarLoadMore.setVisibility(View.GONE);
                        //   remove progress item
                    /*    userList.remove(userList.size() - 1);
                        mAdapter.notifyItemRemoved(userList.size());
                        //add items one by one
                        int start = userList.size();
                        int end = start + 20;

                        for (int i = start + 1; i <= end; i++) {
                            userList.add(new User());
                            mAdapter.notifyItemInserted(userList.size());
                        }
                        mAdapter.setLoaded();
                   */     //or you can add all at once but do not forget to call mAdapter.notifyDataSetChanged();
                    }
                }, 2000);

            }
        });
    }

    private void setAdaptor() {
        mAdapter = new ProductDataAdapter(recAd);
        // set the adapter object to the Recyclerview
        recAd.setAdapter(mAdapter);
    }

    /**
     * Method is used to set the layout on recycler view
     */
    private void recyclerViewLayout() {
        recAd.hasFixedSize();

        mLayoutManager = new LinearLayoutManager(getContext());

        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recAd.setLayoutManager(mLayoutManager);
    }

    @Override
    protected void setViewListeners() {

        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    protected void setViewReferences(View view) {

        recAd = (RecyclerView) view.findViewById(R.id.recAds);
        progressBarLoadMore = (ProgressBar) view.findViewById(R.id.progressBarLoadMore);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshAds);
    }

    @Override
    protected boolean isValidData() {
        return false;
    }

    @Override
    public void onRefresh() {

        swipeRefreshLayout.setRefreshing(false);

        productListApi(0);
    }

    /**
     * Method is used for fetching the ads or product api
     */
    private void productListApi(int index) {
        swipeRefreshLayout.setRefreshing(true);
        Call<ApiResponse> apiResponseCall = RestClient.getService().apiGetProductList(mUserdata.getLanguageId(), "", mUserdata.getId(),
                index, 10, "product", "product_list");
        apiResponseCall.enqueue(new ApiCall(getActivity()) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                swipeRefreshLayout.setRefreshing(false);
                if (apiResponse.status) {
                    productList = apiResponse.paylpad.productList;
                    if(productList!=null)
                    mAdapter.updateProductList(productList);
                }
            }

            @Override
            public void onFail(Call<ApiResponse> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    /**
     * Method is used to show the popup with edit and delete option
     *  @param v        view on which click is perfomed
     * @param position position of item
     * @param product
     */
    public void showPopup(View v, final int position, ProductList product) {
        PopupMenu popup = new PopupMenu(getActivity(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.edit_remove_product, popup.getMenu());
        popup.show();
        showHideEdit(popup,product);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Utils.hideKeyboard(getActivity());
                switch (item.getItemId()) {
                    case R.id.edit:
                        moveToActivity(position);
                        break;
                    case R.id.remove:
                        removeProductApi(position);
                        break;
                }
                return false;
            }
        });
    }

    private void moveToActivity(int position) {

        Bundle bundle     =     new Bundle();

        bundle.putParcelable(AppConstant.PRODUCT_OBJ_KEY,productList.get(position));

        Intent intent     =     new Intent(getActivity(), RenewAdandProductActivity.class);

        intent.putExtras(bundle);

        startActivity(intent);

        getActivity().overridePendingTransition(R.anim.activity_animation_enter,
                R.anim.activity_animation_exit);
    }

    public class ProductDataAdapter extends RecyclerView.Adapter {

        private int visibleThreshold = 5;
        private int lastVisibleItem, totalItemCount;
        private boolean loading;
        private OnLoadMoreListener onLoadMoreListener;


        public ProductDataAdapter(RecyclerView recyclerView) {

            if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

                final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                        .getLayoutManager();


                recyclerView
                        .addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrolled(RecyclerView recyclerView,
                                                   int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);

                                totalItemCount = linearLayoutManager.getItemCount();
                                lastVisibleItem = linearLayoutManager
                                        .findLastVisibleItemPosition();
                                if (!loading
                                        && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                    // End has been reached
                                    // Do something
                                    progressBarLoadMore.setVisibility(View.VISIBLE);
                                    if (onLoadMoreListener != null) {
                                        onLoadMoreListener.onLoadMore();
                                    }
                                    loading = true;
                                }

                            }
                        });
            }
        }

        /*  @Override
          public int getItemViewType(int position) {
              return userList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
          }
  */
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
            RecyclerView.ViewHolder vh;
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.rec_item_add_product, parent, false);

            vh = new ProductDataAdapter.StudentViewHolder(v);

            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            final ProductList product = productList.get(position);

            ((StudentViewHolder) holder).checkboxLike.setText(Utils.format(1000));
            ((StudentViewHolder) holder).checkboxShare.setText(Utils.format(1200));
            ((StudentViewHolder) holder).imvBtnEditRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopup(v, position,product);
                }
            });
            ((StudentViewHolder) holder).tvProductTitle.setText(product.getProductname());
            ((StudentViewHolder) holder).tvDesciption.setText(product.getDescription());
            ((StudentViewHolder) holder).tvTime.setText(timeUtility.covertTimeToText(product.getExpireDate(),getActivity()));
            String status="Ongoing";
            if(product.getIsExpired()>0){
                ((StudentViewHolder) holder).tvStatus.setTextColor(Color.RED);
                status="Expired";
            }else{
                ((StudentViewHolder) holder).tvStatus.setTextColor(getResources().getColor(R.color.colorHomeGreen));

            }
            ((StudentViewHolder) holder).tvStatus.setText(status);
        }

        public void setLoaded() {
            loading = false;
        }

        @Override
        public int getItemCount() {
            return productList.size();
        }

        public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
            this.onLoadMoreListener = onLoadMoreListener;
        }

        /**
         * Method is used to notify the list
         */
        public void notifyAdaptor() {

            mAdapter.notifyDataSetChanged();
        }

        public void updateProductList(ArrayList<ProductList> productLists) {
            if(productLists!=null)
            productList = productLists;
            mAdapter.notifyDataSetChanged();
        }

        //
        public class StudentViewHolder extends RecyclerView.ViewHolder {
            public ImageView imvOfAds;
            public TextView tvProductTitle, tvStatus, tvDesciption,tvTime;
            public CheckBox checkboxLike, checkboxShare;
            public ImageButton imvBtnEditRemove;

            public StudentViewHolder(View view) {
                super(view);
                imvOfAds = (ImageView) view.findViewById(R.id.imvOfAds);
                tvProductTitle = (TextView) view.findViewById(R.id.tv_add_product_title);
                tvStatus = (TextView) view.findViewById(R.id.tv_add_product_status);
                tvDesciption = (TextView) view.findViewById(R.id.tv_add_product_item_description);
                checkboxLike = (CheckBox) view.findViewById(R.id.checkboxLike);
                checkboxShare = (CheckBox) view.findViewById(R.id.checkboxShare);
                imvBtnEditRemove = (ImageButton) view.findViewById(R.id.imvBtnEditRemove);
                tvTime=(TextView)view.findViewById(R.id.tvTime);

            }
        }

    }
    /** Method is used to remove the product
     * @param position
     */
    private void removeProductApi(int position) {

        swipeRefreshLayout.setRefreshing(true);
        Call<ApiResponse> apiResponseCall = RestClient.getService().apiDeleteProduct(mUserdata.getLanguageId(), mUserdata.getAuthrizedKey(), mUserdata.getId(),
                String.valueOf(productList.get(position).getProductid()));

        apiResponseCall.enqueue(new ApiCall(getActivity()) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                swipeRefreshLayout.setRefreshing(false);
                if (apiResponse.status) {
                    Utils.showToast(getActivity(),"Deleted successfully.");
                    onRefresh();
                }
            }

            @Override
            public void onFail(Call<ApiResponse> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
    private void showHideEdit(PopupMenu popup, ProductList product) {

        Menu popupMenu = popup.getMenu();
        if(product.getIsExpired()>0) {
            popupMenu.findItem(R.id.repost).setEnabled(true);
            popupMenu.findItem(R.id.edit).setEnabled(false);
        }else {
            popupMenu.findItem(R.id.edit).setEnabled(true);
            popupMenu.findItem(R.id.repost).setEnabled(false);
        }
    }


}
