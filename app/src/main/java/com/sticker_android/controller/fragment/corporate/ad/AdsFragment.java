package com.sticker_android.controller.fragment.corporate.ad;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
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

import com.bumptech.glide.Glide;
import com.sticker_android.R;
import com.sticker_android.constant.AppConstant;
import com.sticker_android.controller.activities.corporate.RenewAdandProductActivity;
import com.sticker_android.controller.activities.corporate.productdetails.ProductDetailsActivity;
import com.sticker_android.controller.fragment.base.BaseFragment;
import com.sticker_android.model.User;
import com.sticker_android.model.corporateproduct.ProductList;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.LogUtils;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.helper.TimeUtility;
import com.sticker_android.utils.sharedpref.AppPref;
import com.sticker_android.view.OnVerticalScrollListener;

import java.util.ArrayList;

import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdsFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {


    private static final int PAGE_SIZE = 2;
    private RecyclerView recAd;
    private ProgressBar progressBarLoadMore;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayoutManager mLayoutManager;
    private OnVerticalScrollListener scrollListener;
    private boolean loading = true;
    private boolean isLastPage = true;
    private ProductAdaptor productAdaptor;
    protected Handler handler;
    private AppPref appPref;
    private User mUserdata;
    ArrayList<ProductList> productList = new ArrayList<>();
    private TimeUtility timeUtility = new TimeUtility();
    private int index = 0;
    private boolean isLoading;
    private int currentPageNo;
    private String search="";
    private TextView tvNoAdsUploaded;

    public AdsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ads, container, false);
        init();
        getuserInfo();
        setViewReferences(view);
        setViewListeners();
        recyclerViewLayout();
        handler = new Handler();
        setAdaptor();
        productListApi(currentPageNo,search);
        adaptorScrollListener();
        return view;
    }

    private void adaptorScrollListener() {

        recAd.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (swipeRefreshLayout.isRefreshing())
                    return;

                int visibleItemCount = mLayoutManager.getChildCount();
                int totalItemCount = mLayoutManager.getItemCount();
                int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();

                LogUtils.printLog(1, "scroll called", "visible count " + visibleItemCount + " " + totalItemCount + " " + firstVisibleItemPosition);
                if (!isLoading && !isLastPage) {
                    LogUtils.printLog(1, "scroll called", "inside is loading ");
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= PAGE_SIZE) {
                        // loadMoreItems();
                        LogUtils.printLog(1, "scroll called", "inside is loadmore ");
                        if (productList != null && productList.size() > 0) {
                            currentPageNo++;
                            productListApi(currentPageNo * 2,search);
                            LogUtils.printLog(1, "scroll called", " loading called " + currentPageNo);

                        }
                    }
                }
            }
        });

    }


    private void init() {

        appPref = new AppPref(getActivity());
    }

    private void getuserInfo() {
        mUserdata = appPref.getUserInfo();
    }

    private void setAdaptor() {

        productAdaptor = new ProductAdaptor(getActivity(), productList);
        recAd.setAdapter(productAdaptor);
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

        recAd                 =   (RecyclerView) view.findViewById(R.id.recAds);
        progressBarLoadMore   =   (ProgressBar) view.findViewById(R.id.progressBarLoadMore);
        swipeRefreshLayout    =   (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshAds);
        tvNoAdsUploaded       =  (TextView)view.findViewById(R.id.tvNoAdsUploaded);
    }

    @Override
    protected boolean isValidData() {
        return false;
    }

    @Override
    public void onRefresh() {
           search="";
          currentPageNo=0;
        swipeRefreshLayout.setRefreshing(false);

        if (productList != null)
            productList.clear();
        productListApi(0,search);
    }

    /**
     * Method is used for fetching the ads or product api
     */
    private void productListApi(int index, final String search) {
        isLoading = true;
        swipeRefreshLayout.setRefreshing(true);
        Call<ApiResponse> apiResponseCall = RestClient.getService().apiGetProductList(mUserdata.getLanguageId(), "", mUserdata.getId(),
                index, 20, "ads", "product_list",search);
        apiResponseCall.enqueue(new ApiCall(getActivity()) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
                if (apiResponse.status) {
                    ArrayList<ProductList> tempList = new ArrayList<ProductList>();
                    tempList = apiResponse.paylpad.productList;
                    if (tempList != null) {
                        isLastPage = false;
                        productList.addAll(tempList);
                        productAdaptor.notifyDataChanged();
                    } else {
                        isLastPage = true;
                    }
                    if(tempList==null&&productList==null&&search.isEmpty()) {
                        tvNoAdsUploaded.setText(R.string.no_ads_uploaded_yet);
                        tvNoAdsUploaded.setVisibility(View.VISIBLE);
                    }else if(tempList==null&&!search.isEmpty()) {
                        tvNoAdsUploaded.setText(R.string.no_search_found);
                        tvNoAdsUploaded.setVisibility(View.VISIBLE);
                    }else {
                        tvNoAdsUploaded.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFail(Call<ApiResponse> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                isLoading = false;
            }
        });
    }

    /**
     * Method is used to show the popup with edit and delete option
     *
     * @param v        view on which click is perfomed
     * @param position position of item
     * @param product
     */
    public void showPopup(View v, final int position, ProductList product) {
        PopupMenu popup = new PopupMenu(getActivity(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.edit_remove_product, popup.getMenu());
        popup.show();
        showHideEdit(popup, product);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Utils.hideKeyboard(getActivity());
                switch (item.getItemId()) {
                    case R.id.edit:
                        moveToActivity(position, "Edit");
                        break;
                    case R.id.remove:
                        removeProductApi(position);
                        break;
                    case R.id.repost:
                        moveToActivity(position, "Repost");
                        break;
                }
                return false;
            }
        });
    }

    private void moveToRepost(int position) {
    }

    private void showHideEdit(PopupMenu popup, ProductList product) {

        Menu popupMenu = popup.getMenu();
        if (product.getIsExpired() > 0) {
            popupMenu.findItem(R.id.repost).setVisible(true);
            popupMenu.findItem(R.id.edit).setVisible(false);
        } else {
            popupMenu.findItem(R.id.edit).setVisible(true);
            popupMenu.findItem(R.id.repost).setVisible(false);
        }
    }

    /**
     * Method is used to remove the product
     *
     * @param position
     */
    private void removeProductApi(final int position) {

        swipeRefreshLayout.setRefreshing(true);
        Call<ApiResponse> apiResponseCall = RestClient.getService().apiDeleteProduct(mUserdata.getLanguageId(), mUserdata.getAuthrizedKey(), mUserdata.getId(),
                String.valueOf(productList.get(position).getProductid()));

        apiResponseCall.enqueue(new ApiCall(getActivity()) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                swipeRefreshLayout.setRefreshing(false);
                if (apiResponse.status) {
                    Utils.showToast(getActivity(), "Deleted successfully");
                    onRefresh();
                }
            }

            @Override
            public void onFail(Call<ApiResponse> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void moveToActivity(int position, String type) {

        Bundle bundle = new Bundle();

        bundle.putParcelable(AppConstant.PRODUCT_OBJ_KEY, productList.get(position));
        bundle.putString("edit", type);
        Intent intent = new Intent(getActivity(), RenewAdandProductActivity.class);
        intent.putExtras(bundle);

        startActivityForResult(intent, AppConstant.INTENT_RENEW_CODE);

        getActivity().overridePendingTransition(R.anim.activity_animation_enter,
                R.anim.activity_animation_exit);
    }

    public void searchProduct(ArrayList<ProductList> productList) {

        if (this.productList != null) {
            this.productList.clear();
            productAdaptor.notifyDataChanged();
        }
        if (productAdaptor != null) {

            productAdaptor.updateProductList(productList);
        }

    }


    /**
     * @param query
     */
    public void searchProduct(String query) {

        if(productList!=null)
            productList.clear();
            currentPageNo=0;
        productListApi(0,query);

    }


    /**
     * Method is used to refresh the list
     */
    public void refreshList() {
        if (productList != null)
            productList.clear();
        onRefresh();
    }

    private void moveToDetails(ProductList product) {
        Bundle bundle = new Bundle();

        bundle.putParcelable(AppConstant.PRODUCT_OBJ_KEY, product);

        Intent intent = new Intent(getActivity(), ProductDetailsActivity.class);

        intent.putExtras(bundle);

        startActivityForResult(intent,AppConstant.INTENT_PRODUCT_DETAILS);

        getActivity().overridePendingTransition(R.anim.activity_animation_enter,
                R.anim.activity_animation_exit);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case AppConstant.INTENT_RENEW_CODE:
                    onRefresh();
                    break;
                case AppConstant.INTENT_PRODUCT_DETAILS:
                    onRefresh();
                    break;
            }

        }

    }


    /*New Code*/

    public class ProductAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

         private ArrayList<ProductList> productList = new ArrayList<>();

        Context context;
        boolean isLoading = false, isMoreDataAvailable = true;

    /*
    * isLoading - to set the remote loading and complete status to fix back to back load more call
    * isMoreDataAvailable - to set whether more data from server available or not.
    * It will prevent useless load more request even after all the server data loaded
    * */


        public ProductAdaptor(Context context, ArrayList<ProductList> productList) {
            this.context = context;
            this.productList = productList;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            return new ProductHolder(inflater.inflate(R.layout.rec_item_add_product, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            final ProductList product = productList.get(position);

            ((ProductHolder) holder).checkboxLike.setText(Utils.format(1000));
            ((ProductHolder) holder).checkboxShare.setText(Utils.format(1200));
            ((ProductHolder) holder).imvBtnEditRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopup(v, position, product);
                }
            });
            ((ProductHolder) holder).tvProductTitle.setText(Utils.capitlizeText(product.getProductname()));
            ((ProductHolder) holder).tvDesciption.setText(Utils.capitlizeText(product.getDescription()));
               ((ProductHolder) holder).tvTime.setText(timeUtility.covertTimeToText(Utils.convertToCurrentTimeZone(product.getCreatedTime()), getActivity()));
            ((ProductHolder) holder).cardItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moveToDetails(product);
                }
            });
            String status = "Ongoing";
            if (product.getIsExpired() > 0) {
                ((ProductHolder) holder).tvStatus.setTextColor(Color.RED);
                status = "Expired";
            } else {
                ((ProductHolder) holder).tvStatus.setTextColor(getResources().getColor(R.color.colorHomeGreen));

            }
            ((ProductHolder) holder).tvStatus.setText(status);

            if(product.getImagePath()!=null&& !product.getImagePath().isEmpty())
            Glide.with(context)
                    .load(product.getImagePath())
                    .into(((ProductHolder) holder).imvOfAds);
            //No else part needed as load holder doesn't bind any data
        }


        @Override
        public int getItemCount() {
            return productList.size();
        }

        public void delete(int position) { //removes the row
            productList.remove(position);
            notifyItemRemoved(position);
        }

        public void updateProductList(ArrayList<ProductList> productLists) {
            if (productLists != null) {
                productList = productLists;
                productAdaptor.notifyDataSetChanged();
            }
        }

        class ProductHolder extends RecyclerView.ViewHolder {
            public ImageView imvOfAds;
            public TextView tvProductTitle, tvStatus, tvDesciption, tvTime;
            public CheckBox checkboxLike, checkboxShare;
            public ImageButton imvBtnEditRemove;
            public CardView cardItem;

            public ProductHolder(View view) {
                super(view);

                imvOfAds = (ImageView) view.findViewById(R.id.imvOfAds);
                tvProductTitle = (TextView) view.findViewById(R.id.tv_add_product_title);
                tvStatus = (TextView) view.findViewById(R.id.tv_add_product_status);
                tvDesciption = (TextView) view.findViewById(R.id.tv_add_product_item_description);
                checkboxLike = (CheckBox) view.findViewById(R.id.checkboxLike);
                checkboxShare = (CheckBox) view.findViewById(R.id.checkboxShare);
                imvBtnEditRemove = (ImageButton) view.findViewById(R.id.imvBtnEditRemove);
                tvTime = (TextView) view.findViewById(R.id.tvTime);
                cardItem = (CardView) view.findViewById(R.id.card_view);
            }
        }

        public void notifyDataChanged() {
            notifyDataSetChanged();
            isLoading = false;
        }

    }

}
