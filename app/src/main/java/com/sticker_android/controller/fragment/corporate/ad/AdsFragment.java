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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.sticker_android.R;
import com.sticker_android.constant.AppConstant;
import com.sticker_android.controller.activities.corporate.RenewAdandProductActivity;
import com.sticker_android.controller.activities.corporate.productdetails.ProductDetailsActivity;
import com.sticker_android.controller.fragment.base.BaseFragment;
import com.sticker_android.controller.fragment.corporate.CorporateHomeFragment;
import com.sticker_android.model.User;
import com.sticker_android.model.corporateproduct.Product;
import com.sticker_android.model.interfaces.CloseSearch;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.LogUtils;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.fragmentinterface.UpdateToolbarTitle;
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
    ArrayList<Product> productList = new ArrayList<>();
    private TimeUtility timeUtility = new TimeUtility();
    private int index = 0;
    private boolean isLoading;
    private int currentPageNo;
    private String search = "";
    private TextView tvNoAdsUploaded;
    private int scroll;

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
        productList.clear();
        productListApi(currentPageNo, search);
        //adaptorScrollListener();
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
                scroll = 1;
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
                            productListApi(currentPageNo * 2, search);
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

        recAd = (RecyclerView) view.findViewById(R.id.recAds);
        progressBarLoadMore = (ProgressBar) view.findViewById(R.id.progressBarLoadMore);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshAds);
        tvNoAdsUploaded = (TextView) view.findViewById(R.id.tvNoAdsUploaded);
    }

    @Override
    protected boolean isValidData() {
        return false;
    }

    @Override
    public void onRefresh() {
        scroll = 0;
        search = "";
        currentPageNo = 0;

        if (productList != null)
            productList.clear();
        productListApi(0, search);
        CorporateHomeFragment parentFrag = ((CorporateHomeFragment)AdsFragment.this.getParentFragment());
        parentFrag.clearSearch();
    }

    public void refreshApi() {
        scroll = 0;
        search = "";
        currentPageNo = 0;

        if (productList != null)
            productList.clear();
        productListApi(0, search);
    }

    /**
     * Method is used for fetching the ads or product api
     */
    private void productListApi(int index, final String search) {
        isLoading = true;
        if(swipeRefreshLayout!=null)
        swipeRefreshLayout.setRefreshing(true);
        Call<ApiResponse> apiResponseCall = RestClient.getService().apiGetProductList(mUserdata.getLanguageId(), "", mUserdata.getId(),
                index, 50, "ads", "product_list", search);
        apiResponseCall.enqueue(new ApiCall(getActivity()) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                isLoading = false;
                if(swipeRefreshLayout!=null)
                swipeRefreshLayout.setRefreshing(false);
                if (apiResponse.status) {
                    if(productList!=null)
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
                if(swipeRefreshLayout!=null)
                swipeRefreshLayout.setRefreshing(false);
                isLoading = false;
            }
        });
        this.search="";
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
                    productAdaptor.delete(position);
                   /* refreshApi();
               */
                }
            }

            @Override
            public void onFail(Call<ApiResponse> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        scroll = 0;
        search="";
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
        scroll = 0;
        if (productList != null)
            productList.clear();
        currentPageNo = 0;
        productListApi(0, query);

    }


    /**
     * Method is used to refresh the list
     */
    public void refreshList() {
        if (productList != null)
            productList.clear();
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

            if (product.getImagePath() != null && !product.getImagePath().isEmpty())
          /*  Glide.with(context)
                    .load(product.getImagePath())
                    .into(((ProductHolder) holder).imvOfAds);*/
                ((ProductHolder) holder).pgrImage.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(product.getImagePath()).placeholder(R.drawable.ic_upload_image).fitCenter()
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
}
