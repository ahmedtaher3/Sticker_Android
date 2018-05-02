package com.sticker_android.controller.fragment.corporate.ad;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.sticker_android.R;
import com.sticker_android.constant.AppConstant;
import com.sticker_android.controller.activities.corporate.RenewAdandProductActivity;
import com.sticker_android.controller.activities.corporate.home.CorporateHomeActivity;
import com.sticker_android.controller.activities.corporate.productdetails.ProductDetailsActivity;
import com.sticker_android.controller.adaptors.CorporateListAdaptor;
import com.sticker_android.controller.adaptors.DesignListAdapter;
import com.sticker_android.controller.fragment.base.BaseFragment;
import com.sticker_android.model.User;
import com.sticker_android.model.corporateproduct.Product;
import com.sticker_android.model.enums.DesignType;
import com.sticker_android.model.enums.ProductStatus;
import com.sticker_android.model.interfaces.DesignerActionListener;
import com.sticker_android.model.interfaces.MessageEventListener;
import com.sticker_android.model.interfaces.NetworkPopupEventListener;
import com.sticker_android.model.payload.Payload;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.LogUtils;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.AppLogger;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.helper.PaginationScrollListener;
import com.sticker_android.utils.helper.TimeUtility;
import com.sticker_android.utils.sharedpref.AppPref;
import com.sticker_android.view.OnVerticalScrollListener;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdsFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {


    private static final int PAGE_SIZE = 2;
    private static final String TAG = AdsFragment.class.getSimpleName();
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
    ArrayList<Product> productList = new ArrayList<>();
    private TimeUtility timeUtility = new TimeUtility();
    private int index = 0;
    private boolean isLoading;
    private int currentPageNo;
    private String search = "";
    private TextView tvNoAdsUploaded;
    private int scroll;
    CorporateListAdaptor corporateListAdaptor;

    private int mCurrentPage = 0;
    private int PAGE_LIMIT;
    private LinearLayout llNoDataFound;
    private RelativeLayout rlContent;
    private LinearLayout llLoaderView;
    private RelativeLayout rlConnectionContainer;
    private TextView txtNoDataFoundTitle, txtNoDataFoundContent;
    private CorporateHomeActivity mHostActivity;


    public AdsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ads, container, false);
        PAGE_LIMIT = mHostActivity.getResources().getInteger(R.integer.designed_item_page_limit);
        init();
        getuserInfo();
        setViewReferences(view);
        setViewListeners();
        recyclerViewLayout();
        handler = new Handler();
        setAdaptor();

        llNoDataFound.setVisibility(View.GONE);

        corporateListAdaptor = new CorporateListAdaptor(getActivity());
        productList.clear();

        recAd.setAdapter(corporateListAdaptor);

        llNoDataFound.setVisibility(View.GONE);
        productList = new ArrayList<>();
        mCurrentPage = 0;
        getProductFromServer(false, "");

        //   productListApi(currentPageNo, search);
        adaptorScrollListener();
        return view;
    }

    private void adaptorScrollListener() {


        recAd.addOnScrollListener(new PaginationScrollListener(mLayoutManager) {
            @Override
            protected void loadMoreItems() {
                AppLogger.debug(TAG, "Load more items");

                if (productList.size() >= PAGE_LIMIT) {
                    AppLogger.debug(TAG, "page limit" + PAGE_LIMIT + " list size" + productList.size());
                    getProductFromServer(false, "");
                    corporateListAdaptor.addLoader();
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
                return corporateListAdaptor.isLoaderVisible;
            }
        });

    }

    private void getProductFromServer(final boolean isRefreshing, final String searchKeyword) {

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

        Call<ApiResponse> apiResponseCall = RestClient.getService().apiGetProductList(mUserdata.getLanguageId(), "", mUserdata.getId(),
                index, limit, DesignType.ads.getType().toLowerCase(Locale.ENGLISH), "product_list", searchKeyword);
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
                                        productList.clear();
                                        productList.addAll(payload.productList);

                                        llNoDataFound.setVisibility(View.GONE);
                                        recAd.setVisibility(View.VISIBLE);
                                        corporateListAdaptor.setData(productList);

                                        mCurrentPage = 0;
                                        mCurrentPage++;
                                    } else {
                                        productList.clear();
                                        corporateListAdaptor.setData(productList);
                                        if (searchKeyword.length() != 0) {
                                            txtNoDataFoundContent.setText(getString(R.string.no_search_found));
                                        } else {
                                            txtNoDataFoundContent.setText(R.string.no_ads_uploaded_yet);
                                        }
                                        showNoDataFound();
                                    }
                                } else {

                                    if (mCurrentPage == 0) {
                                        productList.clear();
                                        if (payload.productList != null) {
                                            productList.addAll(payload.productList);
                                        }

                                        if (productList.size() != 0) {
                                            llNoDataFound.setVisibility(View.GONE);
                                            recAd.setVisibility(View.VISIBLE);
                                            corporateListAdaptor.setData(productList);
                                        } else {
                                            showNoDataFound();
                                            if (searchKeyword.length() != 0) {
                                                txtNoDataFoundContent.setText(getString(R.string.no_search_found));
                                            } else {
                                                txtNoDataFoundContent.setText(R.string.no_ads_uploaded_yet);
                                            }
                                            recAd.setVisibility(View.GONE);
                                        }
                                    } else {
                                        AppLogger.error(TAG, "Remove loader...");
                                        corporateListAdaptor.removeLoader();
                                        if (payload.productList != null && payload.productList.size() != 0) {
                                            productList.addAll(payload.productList);
                                            corporateListAdaptor.setData(productList);
                                        }
                                    }

                                    if (payload.productList != null && payload.productList.size() != 0) {
                                        mCurrentPage++;
                                    }
                                }
                                AppLogger.error(TAG, "item list size => " + productList.size());

                            } else if (productList == null || (productList != null && productList.size() == 0)) {
                                if (searchKeyword.length() != 0) {
                                    txtNoDataFoundContent.setText(getString(R.string.no_search_found));
                                } else {
                                    txtNoDataFoundContent.setText(R.string.no_ads_uploaded_yet);
                                }
                                showNoDataFound();
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Utils.showAlertMessage(getActivity(), new MessageEventListener() {
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
                    corporateListAdaptor.removeLoader();
                    swipeRefreshLayout.setRefreshing(false);

                    if (mCurrentPage == 0) {
                        rlContent.setVisibility(View.GONE);
                    } else {
                        rlContent.setVisibility(View.VISIBLE);
                    }
                    if (!call.isCanceled() && (t instanceof ConnectException ||
                            t instanceof SocketTimeoutException ||
                            t instanceof SocketException ||
                            t instanceof UnknownHostException)) {

                        if (mCurrentPage == 0) {
                            mHostActivity.manageNoInternetConnectionLayout(getActivity(), rlConnectionContainer, new NetworkPopupEventListener() {
                                @Override
                                public void onOkClickListener(int reqCode) {
                                    rlContent.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onRetryClickListener(int reqCode) {
                                    getProductFromServer(isRefreshing, searchKeyword);
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
        mCurrentPage = 0;
        if (productList != null)
            productList.clear();
        corporateListAdaptor.setData(productList);
        if (Utils.isConnectedToInternet(mHostActivity)) {
            getProductFromServer(true, "");
        } else {
            swipeRefreshLayout.setRefreshing(false);
            Utils.showToastMessage(mHostActivity, getString(R.string.pls_check_ur_internet_connection));
        }
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

        recAd = (RecyclerView) view.findViewById(R.id.recAds);
        progressBarLoadMore = (ProgressBar) view.findViewById(R.id.progressBarLoadMore);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshAds);
        //  tvNoAdsUploaded = (TextView) view.findViewById(R.id.tvNoAdsUploaded);
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

  /*  @Override
    public void onRefresh() {
        scroll = 0;
        search = "";
        currentPageNo = 0;

        if (productList != null)
            productList.clear();
        productListApi(0, search);
        CorporateHomeFragment parentFrag = ((CorporateHomeFragment) AdsFragment.this.getParentFragment());
        parentFrag.clearSearch();
    }*/

    public void refreshApi() {
       /* scroll = 0;
        search = "";
        currentPageNo = 0;

        if (productList != null)
            productList.clear();
        productListApi(0, search);
   */
        search = "";
        if (productList != null)
            productList.clear();
        mCurrentPage = 0;
        getProductFromServer(false, "");
    }

    /**
     * Method is used for fetching the ads or product api
     */
    private void productListApi(int index, final String search) {
        isLoading = true;
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(true);
        Call<ApiResponse> apiResponseCall = RestClient.getService().apiGetProductList(mUserdata.getLanguageId(), "", mUserdata.getId(),
                index, 50, "ads", "product_list", search);
        apiResponseCall.enqueue(new ApiCall(getActivity()) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                isLoading = false;
                if (swipeRefreshLayout != null)
                    swipeRefreshLayout.setRefreshing(false);
                if (apiResponse.status) {
                    if (productList != null)
                        productList.clear();
                    ArrayList<Product> tempList = new ArrayList<Product>();
                    tempList = apiResponse.paylpad.productList;
                    if (tempList != null) {
                        isLastPage = false;
                        productList.addAll(tempList);
                        productAdaptor.notifyDataChanged();
                    } else {
                        isLastPage = true;
                    }
                    if (tempList == null && search.isEmpty()) {
                        tvNoAdsUploaded.setText(R.string.no_ads_uploaded_yet);
                        tvNoAdsUploaded.setVisibility(View.VISIBLE);
                    } else if (tempList == null && !search.isEmpty()) {
                        tvNoAdsUploaded.setText(R.string.no_search_found);
                        tvNoAdsUploaded.setVisibility(View.VISIBLE);
                    } else {
                        tvNoAdsUploaded.setVisibility(View.GONE);
                    }
              /*  if(scroll>0){
                    tvNoAdsUploaded.setVisibility(View.GONE);
                    tvNoAdsUploaded.setVisibility(View.GONE);
                }*/
                    if (!search.isEmpty()) {
                        if (tempList == null && currentPageNo == 0) {
                            tvNoAdsUploaded.setText(R.string.no_search_found);
                            tvNoAdsUploaded.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onFail(Call<ApiResponse> call, Throwable t) {
                if (swipeRefreshLayout != null)
                    swipeRefreshLayout.setRefreshing(false);
                isLoading = false;
            }
        });
        this.search = "";
    }

    /**
     * Method is used to show the popup with edit and delete option
     *
     * @param v        view on which click is perfomed
     * @param position position of item
     * @param product
     */
    public void showPopup(View v, final int position, Product product) {
        PopupMenu popup = new PopupMenu(getActivity(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.edit_remove_product, popup.getMenu());
        popup.show();
        popup.setGravity(Gravity.CENTER);
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
                        search = "";
                        Utils.deleteDialog(getString(R.string.txt_are_you_sure), getActivity(), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeProductApi(position);
                            }
                        });
                        //   removeProductApi(position);
                        break;
                    case R.id.repost:
                        moveToActivity(position, "Repost");
                        break;

                    case R.id.reSubmit:
                     moveToActivity(position,"Edit");
                        break;
                }
                return false;
            }
        });
    }

    private void moveToRepost(int position) {
    }

    private void showHideEdit(PopupMenu popup, Product product) {

        Menu popupMenu = popup.getMenu();
        if (product.getIsExpired() > 0) {
            popupMenu.findItem(R.id.repost).setVisible(true);
            popupMenu.findItem(R.id.edit).setVisible(false);
        } else {
            popupMenu.findItem(R.id.edit).setVisible(true);
            popupMenu.findItem(R.id.repost).setVisible(false);
        }

        if(product.productStatus==3){
            popupMenu.findItem(R.id.repost).setVisible(false);
            popupMenu.findItem(R.id.edit).setVisible(false);
            popupMenu.findItem(R.id.reSubmit).setVisible(true);
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
                  //  productAdaptor.delete(position);
                   refreshApi();

                }
            }

            @Override
            public void onFail(Call<ApiResponse> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        scroll = 0;
        search = "";
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

    public void searchProduct(ArrayList<Product> productList) {

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
      /*  scroll = 0;
        if (productList != null)
            productList.clear();
        currentPageNo = 0;
        productListApi(0, query);
*/
        mCurrentPage = 0;
        if (productList != null)
            productList.clear();
        corporateListAdaptor.setData(productList);
        getProductFromServer(false, query);
    }


    /**
     * Method is used to refresh the list
     */
    public void refreshList() {
        mCurrentPage = 0;
        if (productList != null)
            productList.clear();
        corporateListAdaptor.setData(productList);
        onRefresh();

    }

    private void moveToDetails(Product product) {
        Bundle bundle = new Bundle();

        bundle.putParcelable(AppConstant.PRODUCT_OBJ_KEY, product);

        Intent intent = new Intent(getActivity(), ProductDetailsActivity.class);

        intent.putExtras(bundle);

        startActivityForResult(intent, AppConstant.INTENT_PRODUCT_DETAILS);

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
                    if (productAdaptor != null)
                        productAdaptor.notifyDataChanged();
                    onRefresh();
                    break;
            }

        }

    }



    /*New Code*/

    public class ProductAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private ArrayList<Product> productList = new ArrayList<>();

        Context context;
        boolean isLoading = false, isMoreDataAvailable = true;

    /*
    * isLoading - to set the remote loading and complete status to fix back to back load more call
    * isMoreDataAvailable - to set whether more data from server available or not.
    * It will prevent useless load more request even after all the server data loaded
    * */


        public ProductAdaptor(Context context, ArrayList<Product> productList) {
            this.context = context;
            this.productList = productList;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            return new ProductHolder(inflater.inflate(R.layout.rec_item_add_product, parent, false));
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            final Product product = productList.get(position);

            ((ProductHolder) holder).checkboxLike.setText(Utils.format(product.statics.likeCount));
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

            if (product.getImagePath() != null && !product.getImagePath().isEmpty())
          /*  Glide.with(context)
                    .load(product.getImagePath())
                    .into(((ProductHolder) holder).imvOfAds);*/
                ((ProductHolder) holder).pgrImage.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(product.getImagePath()).fitCenter()
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {

                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            ((ProductHolder) holder).pgrImage.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(((ProductHolder) holder).imvOfAds);

            if (product.isLike > 0) {
                ((ProductHolder) holder).checkboxLike.setChecked(true);
                ((ProductHolder) holder).checkboxLike.setButtonDrawable(context.getResources().getDrawable(R.drawable.ic_hand));
            } else {
                ((ProductHolder) holder).checkboxLike.setChecked(false);
                ((ProductHolder) holder).checkboxLike.setButtonDrawable(context.getResources().getDrawable(R.drawable.ic_like));

            }
            if (product.isFeatured > 0) {
                ((ProductHolder) holder).tvFeatured.setVisibility(View.VISIBLE);
            } else
                ((ProductHolder) holder).tvFeatured.setVisibility(View.GONE);

        }


        @Override
        public int getItemCount() {
            return productList.size();
        }

        public void delete(int position) { //removes the row
            productList.remove(position);
            productAdaptor.notifyDataSetChanged();
            if (productList != null)
                if (productList.size() == 0)
                    tvNoAdsUploaded.setVisibility(View.VISIBLE);
            //   notifyItemRemoved(position);
        }

        public void updateProductList(ArrayList<Product> productLists) {
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
            public ProgressBar pgrImage;
            public TextView tvFeatured;

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
                pgrImage = (ProgressBar) view.findViewById(R.id.pgrImage);
                tvFeatured = (TextView) view.findViewById(R.id.tvFeatured);
            }
        }

        public void notifyDataChanged() {
            notifyDataSetChanged();
            isLoading = false;
        }

    }

    public void deleteDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you want to delete this item?");
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });

        builder.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(alertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorCorporateText));
        alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorCorporateText));

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mHostActivity = (CorporateHomeActivity) context;
    }


    public class CorporateListAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final String TAG = DesignListAdapter.class.getSimpleName();
        private ArrayList<Product> mItems;
        private Context context;
        public boolean isLoaderVisible;

        private final int ITEM_FOOTER = 0;
        private final int ITEM_PRODUCT = 1;

        private TimeUtility timeUtility = new TimeUtility();


        private com.sticker_android.controller.adaptors.CorporateListAdaptor.OnProductItemClickListener productItemClickListener;
        private DesignerActionListener designerActionListener;

        public class ViewHolder extends RecyclerView.ViewHolder {

            public ImageView imvOfAds;
            public TextView tvProductTitle, tvStatus, tvDesciption, tvTime, tvDownloads;
            public CheckBox checkboxLike, checkboxShare;
            public ImageButton imvBtnEditRemove;
            public CardView cardItem;
            public ProgressBar pbLoader;
            TextView tvFeatured;

            public ViewHolder(View view) {
                super(view);
                imvOfAds = (ImageView) view.findViewById(R.id.imvOfAds);
                tvProductTitle = (TextView) view.findViewById(R.id.tv_add_product_title);
                tvStatus = (TextView) view.findViewById(R.id.tv_add_product_status);
                tvDesciption = (TextView) view.findViewById(R.id.tv_add_product_item_description);
                checkboxLike = (CheckBox) view.findViewById(R.id.checkboxLike);
                checkboxShare = (CheckBox) view.findViewById(R.id.checkboxShare);
                imvBtnEditRemove = (ImageButton) view.findViewById(R.id.imvBtnEditRemove);
                tvTime = (TextView) view.findViewById(R.id.tvTime);
                tvDownloads = (TextView) view.findViewById(R.id.tvDownloads);
                cardItem = (CardView) view.findViewById(R.id.card_view);
                pbLoader = (ProgressBar) view.findViewById(R.id.pgrImage);
                tvFeatured = (TextView) view.findViewById(R.id.tvFeatured);
            }
        }

        public class LoaderViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            private ProgressBar progressBar;

            public LoaderViewHolder(View v) {
                super(v);
                progressBar = (ProgressBar) v.findViewById(R.id.pbMore);
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public CorporateListAdaptor(Context cnxt) {
            mItems = new ArrayList<>();
            context = cnxt;
        }

        public void setDesignerActionListener(DesignerActionListener actionListener) {
            this.designerActionListener = actionListener;
        }

        public void setOnProductClickListener(com.sticker_android.controller.adaptors.CorporateListAdaptor.OnProductItemClickListener productClickListener) {
            this.productItemClickListener = productClickListener;
        }

        public void setData(ArrayList<Product> data) {
            if (data != null) {
                mItems = new ArrayList<>();
                mItems.addAll(data);
                notifyDataSetChanged();
                isLoaderVisible = false;
            }
        }

        public void updateAdapterData(ArrayList<Product> data) {
            mItems = new ArrayList<>();
            mItems.addAll(data);
        }

        public void addLoader() {
            AppLogger.error(TAG, "Add loader... in adapter");
            Product postItem = new Product();
            postItem.setProductid(-1);
            mItems.add(postItem);
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    notifyItemInserted(mItems.size() - 1);
                }
            });
            isLoaderVisible = true;
        }

        public void removeLoader() {
            AppLogger.error(TAG, "Remove loader... from adapter");
            Product postItem = new Product();
            postItem.setProductid(-1);
            int index = mItems.indexOf(postItem);
            AppLogger.error(TAG, "Loader index => " + index);
            if (index != -1) {
                mItems.remove(index);
                //notifyDataSetChanged();
                notifyItemRemoved(index);
                notifyItemRangeChanged(index, mItems.size());
                isLoaderVisible = false;
            }
        }

        public void removeProductData(int index) {
            if (index != -1) {
                mItems.remove(index);
                notifyItemRemoved(index);
                notifyItemRangeChanged(index, mItems.size());
            }
        }

        public void removeProductData(Product product) {
            int index = mItems.indexOf(product);
            if (index != -1) {
                mItems.remove(index);
                notifyItemRemoved(index);
                notifyItemRangeChanged(index, mItems.size());
            }
        }

        public void updateModifiedItem(Product postItem) {
            int index = mItems.indexOf(postItem);
            if (index != -1) {
                mItems.set(index, postItem);
                notifyDataSetChanged();
            }
        }

        // Create new views (invoked by the layout manager)
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            if (viewType == ITEM_FOOTER) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_loader_view, parent, false);
                // set the view's size, margins, paddings and layout parameters
                final CorporateListAdaptor.LoaderViewHolder vh = new CorporateListAdaptor.LoaderViewHolder(v);
                return vh;
            } else {
                // create a new view
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_design_items, parent, false);
                // set the view's size, margins, paddings and layout parameters
                final CorporateListAdaptor.ViewHolder vh = new CorporateListAdaptor.ViewHolder(v);

                vh.cardItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = vh.getAdapterPosition();
                        Product product = mItems.get(position);
                        Bundle bundle = new Bundle();

                        bundle.putParcelable(AppConstant.PRODUCT_OBJ_KEY, product);

                        Intent intent = new Intent(context, ProductDetailsActivity.class);

                        intent.putExtras(bundle);

                        ((Activity) context).startActivityForResult(intent, AppConstant.INTENT_PRODUCT_DETAILS);

                        ((Activity) context).overridePendingTransition(R.anim.activity_animation_enter,
                                R.anim.activity_animation_exit);

                    }
                });
                return vh;
            }
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            final int itemType = getItemViewType(position);

            if (itemType == ITEM_FOOTER) {

            } else {
                final CorporateListAdaptor.ViewHolder itemHolder = (CorporateListAdaptor.ViewHolder) holder;
                final Product productItem = mItems.get(position);
                if (productItem.isLike > 0) {
                    itemHolder.checkboxLike.setChecked(true);
                    itemHolder.checkboxLike.setButtonDrawable(context.getResources().getDrawable(R.drawable.ic_hand));
                } else {
                    itemHolder.checkboxLike.setChecked(false);
                    itemHolder.checkboxLike.setButtonDrawable(context.getResources().getDrawable(R.drawable.ic_like));

                }
                if (productItem.statics.likeCount > 0) {
                    itemHolder.checkboxLike.setChecked(true);
                    itemHolder.checkboxLike.setButtonDrawable(context.getResources().getDrawable(R.drawable.ic_hand));
                } else {
                    itemHolder.checkboxLike.setChecked(false);
                    itemHolder.checkboxLike.setButtonDrawable(context.getResources().getDrawable(R.drawable.ic_like));

                }
                itemHolder.checkboxLike.setText(Utils.format(productItem.statics.likeCount));
                itemHolder.tvDownloads.setText(Utils.format(productItem.statics.downloadCount));
                itemHolder.imvBtnEditRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // showPopup(v, position, "pending", productItem);
                        showPopup(v, position, productItem);
                    }
                });
                itemHolder.tvProductTitle.setText(Utils.capitlizeText(productItem.getProductname()));
                itemHolder.tvDownloads.setVisibility(View.GONE);
                if (productItem.getDescription() != null && productItem.getDescription().trim().length() != 0) {
                    itemHolder.tvDesciption.setVisibility(View.VISIBLE);
                    itemHolder.tvDesciption.setText(Utils.capitlizeText(productItem.getDescription()));
                } else {
                    itemHolder.tvDesciption.setVisibility(View.GONE);
                }
                itemHolder.tvTime.setText(timeUtility.covertTimeToText(Utils.convertToCurrentTimeZone(productItem.getCreatedTime()), context).replaceAll("about", "").trim());

                if (productItem.isFeatured > 0) {
                    itemHolder.tvFeatured.setVisibility(View.VISIBLE);
                } else
                    itemHolder.tvFeatured.setVisibility(View.GONE);

                int status = productItem.productStatus;
                AppLogger.error(TAG, "Status => " + status);
                if (status == ProductStatus.REJECTED.getStatus()) {
                    itemHolder.tvStatus.setTextColor(Color.RED);
                    itemHolder.tvStatus.setText(R.string.rejected);
                } else if (status == ProductStatus.EXPIRED.getStatus()) {
                    itemHolder.tvStatus.setTextColor(Color.RED);
                    itemHolder.tvStatus.setText(R.string.expired);
                } else if (status == ProductStatus.APPROVED.getStatus()) {
                    itemHolder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.colorHomeGreen));
                    itemHolder.tvStatus.setText(R.string.approved);
                } else {
                    itemHolder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.colorCorporateText));
                    itemHolder.tvStatus.setText(R.string.pending);
                }

                if (productItem.getImagePath() != null && !productItem.getImagePath().isEmpty()) {
                    itemHolder.pbLoader.setVisibility(View.VISIBLE);
                    Glide.with(context)
                            .load(productItem.getImagePath())
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    itemHolder.pbLoader.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    itemHolder.pbLoader.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .into(itemHolder.imvOfAds);
                } else {
                    itemHolder.imvOfAds.setBackgroundColor(ContextCompat.getColor(context, R.color.image_background_color));
                }
            }
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mItems.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (mItems.get(position).getProductid() == -1) {
                return ITEM_FOOTER;
            } else {
                return ITEM_PRODUCT;
            }
        }

        private void removeProductApi(final Product product) {
            AppPref appPref = new AppPref(context);
            User mUserdata = appPref.getUserInfo();
            Call<ApiResponse> apiResponseCall = RestClient.getService().apiDeleteProduct(mUserdata.getLanguageId(), mUserdata.getAuthrizedKey(), mUserdata.getId(),
                    String.valueOf(product.getProductid()));

            apiResponseCall.enqueue(new ApiCall((Activity) context) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    if (apiResponse.status) {
                        Utils.showToast(context, context.getString(R.string.deleted_successfully));
                    }
                }

                @Override
                public void onFail(Call<ApiResponse> call, Throwable t) {

                }
            });
        }
    }


}