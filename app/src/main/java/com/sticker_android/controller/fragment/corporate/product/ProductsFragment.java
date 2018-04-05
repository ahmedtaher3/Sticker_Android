package com.sticker_android.controller.fragment.corporate.product;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Toast;

import com.sticker_android.R;
import com.sticker_android.constant.AppConstant;
import com.sticker_android.controller.activities.corporate.RenewAdandProductActivity;
import com.sticker_android.controller.activities.corporate.productdetails.ProductDetailsActivity;
import com.sticker_android.controller.fragment.base.BaseFragment;
import com.sticker_android.controller.fragment.corporate.ad.AdsFragment;
import com.sticker_android.model.User;
import com.sticker_android.model.corporateproduct.ProductList;
import com.sticker_android.model.interfaces.OnLoadMoreListener;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.helper.TimeUtility;
import com.sticker_android.utils.sharedpref.AppPref;
import com.sticker_android.view.OnVerticalScrollListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;


public class ProductsFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recAd;
    private ProgressBar progressBarLoadMore;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayoutManager mLayoutManager;
    private OnVerticalScrollListener scrollListener;
    private boolean loading = true;
    private boolean isLastPage = true;
    protected Handler handler;
    private AppPref appPref;
    private User mUserdata;
    ArrayList<ProductList> productList = new ArrayList<>();
    private TimeUtility timeUtility=new TimeUtility();
    private ProductAdaptor productAdaptor;

    private int index=0;
    private boolean isLoading;
    private int currentPageNo;
    private static final int PAGE_SIZE = 2;

    private int indexIs;
    private int limitIs;
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
        setAdaptor();

        productListApi(0,2);
        adaptorScrollListener();
        return view;
    }

    private void init() {

        appPref = new AppPref(getActivity());
    }

    private void getuserInfo() {
        mUserdata = appPref.getUserInfo();
    }

    private void setAdaptor() {

        productAdaptor     =    new ProductAdaptor(getActivity(),productList);
        recAd.setAdapter(productAdaptor);
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

                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= PAGE_SIZE) {
                        // loadMoreItems();
                        if (productList != null && productList.size() > 0) {
                            currentPageNo++;
                            productListApi(currentPageNo,currentPageNo*2);
                        }
                    }
                }
            }
        });

    }



    /**
     * Method is used to set the layout on recycler view
     */
    private void recyclerViewLayout() {
        recAd.hasFixedSize();

        mLayoutManager   =   new LinearLayoutManager(getContext());

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

        productListApi(0,2);
    }


    /**
     * Method is used for fetching the ads or product api
     */
    private void productListApi(int index,int limit) {
        isLoading=true;
        swipeRefreshLayout.setRefreshing(true);
        Call<ApiResponse> apiResponseCall = RestClient.getService().apiGetProductList(mUserdata.getLanguageId(), "", mUserdata.getId(),
                index, limit, "ads", "product_list");
        apiResponseCall.enqueue(new ApiCall(getActivity()) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                isLoading=false;
                swipeRefreshLayout.setRefreshing(false);
                if (apiResponse.status) {
                    ArrayList<ProductList> tempList=new ArrayList<ProductList>();
                    tempList = apiResponse.paylpad.productList;
                    if(tempList!=null) {
                        isLastPage=false;
                        productList.addAll(tempList);
                        productAdaptor.notifyDataChanged();
                    }else {
                        isLastPage=true;
                    }

                }
            }

            @Override
            public void onFail(Call<ApiResponse> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                isLoading=false;
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
                        moveToActivity(position,"Edit");
                        break;
                    case R.id.remove:
                        removeProductApi(position);
                        break;
                    case R.id.repost:
                        moveToActivity(position,"Repost");
                        break;
                }
                return false;
            }
        });
    }

    private void moveToActivity(int position,String type) {

        Bundle bundle = new Bundle();

        bundle.putParcelable(AppConstant.PRODUCT_OBJ_KEY, productList.get(position));
        bundle.putString("edit",type);
        Intent intent = new Intent(getActivity(), RenewAdandProductActivity.class);
        intent.putExtras(bundle);

        startActivityForResult(intent,AppConstant.INTENT_RENEW_CODE);

        getActivity().overridePendingTransition(R.anim.activity_animation_enter,
                R.anim.activity_animation_exit);
    }


    public void searchProduct(ArrayList<ProductList> productList) {


        if(productAdaptor!=null){

            productAdaptor.updateProductList(productList);
        }
    }

    public void refreshList() {
        onRefresh();
    }

    /** Method is used to remove the product
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
                    Utils.showToast(getActivity(),"Deleted successfully.");
                    if(productAdaptor!=null)
                        productAdaptor.delete(position);
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
            popupMenu.findItem(R.id.repost).setVisible(true);
            popupMenu.findItem(R.id.edit).setVisible(false);
        }else {
            popupMenu.findItem(R.id.edit).setVisible(true);
            popupMenu.findItem(R.id.repost).setVisible(false);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case AppConstant.INTENT_RENEW_CODE:
                    onRefresh();
                    break;
            }

        }
    }
    private void moveToDetails(ProductList product) {
        Bundle bundle = new Bundle();

        bundle.putParcelable(AppConstant.PRODUCT_OBJ_KEY, product);

        Intent intent = new Intent(getActivity(), ProductDetailsActivity.class);

        intent.putExtras(bundle);

        startActivity(intent);

        getActivity().overridePendingTransition(R.anim.activity_animation_enter,
                R.anim.activity_animation_exit);
    }


    /**
     * Class is used to show the product data
     */
    public class ProductAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public final int TYPE_PRODUCT = 0;
        public final int TYPE_LOAD = 1;

        Context context;
        List<ProductList> productLists;
        OnLoadMoreListener loadMoreListener;
        boolean isLoading = false, isMoreDataAvailable = true;

    /*
    * isLoading - to set the remote loading and complete status to fix back to back load more call
    * isMoreDataAvailable - to set whether more data from server available or not.
    * It will prevent useless load more request even after all the server data loaded
    * */


        public ProductAdaptor(Context context, List<ProductList> productLists) {
            this.context = context;
            this.productLists = productLists;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            if(viewType== TYPE_PRODUCT){
                return new ProductAdaptor.ProductHolder(inflater.inflate(R.layout.rec_item_add_product,parent,false));
            }else{
                return new ProductAdaptor.LoadHolder(inflater.inflate(R.layout.progress_item,parent,false));
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            if(position>=getItemCount()-1 && isMoreDataAvailable && !isLoading && loadMoreListener!=null){
                isLoading = true;
                loadMoreListener.onLoadMore();
            }

            if(getItemViewType(position)== TYPE_PRODUCT){
                final ProductList product = productLists.get(position);

                ((ProductAdaptor.ProductHolder) holder).checkboxLike.setText(Utils.format(1000));
                ((ProductAdaptor.ProductHolder) holder).checkboxShare.setText(Utils.format(1200));
                ((ProductAdaptor.ProductHolder) holder).imvBtnEditRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPopup(v, position,product);
                    }
                });
                ((ProductAdaptor.ProductHolder) holder).tvProductTitle.setText(product.getProductname());
                ((ProductAdaptor.ProductHolder) holder).tvDesciption.setText(product.getDescription());
                //   ((AdsViewHolder) holder).tvTime.setText(timeUtility.covertTimeToText(product.getExpireDate(), getActivity()));
                ((ProductAdaptor.ProductHolder) holder).cardItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        moveToDetails(product);
                    }
                });
                String status="Ongoing";
                if(product.getIsExpired()>0){
                    ((ProductAdaptor.ProductHolder) holder).tvStatus.setTextColor(Color.RED);
                    status="Expired";
                }else{
                    ((ProductAdaptor.ProductHolder) holder).tvStatus.setTextColor(getResources().getColor(R.color.colorHomeGreen));

                }
                ((ProductAdaptor.ProductHolder) holder).tvStatus.setText(status);

            }
            //No else part needed as load holder doesn't bind any data
        }

        @Override
        public int getItemViewType(int position) {
            if(productLists.get(position).getType().equals("ads")||productLists.get(position).getType().equals("product")){
                return TYPE_PRODUCT;
            }else{
                return TYPE_LOAD;
            }
        }

        @Override
        public int getItemCount() {
            return productLists.size();
        }

        public void delete(int position) { //removes the row
            productList.remove(position);
            notifyItemRemoved(position);
        }
        public void updateProductList(ArrayList<ProductList> productLists) {
            if (productLists != null) {
                Utils.showToast(getActivity(), "update call" + productLists.size());
                this.productLists = productLists;
                productAdaptor.notifyDataSetChanged();
            }
        }

    /* VIEW HOLDERS */

        class ProductHolder extends RecyclerView.ViewHolder{
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

        class LoadHolder extends RecyclerView.ViewHolder{
            public LoadHolder(View itemView) {
                super(itemView);
            }
        }

        public void setMoreDataAvailable(boolean moreDataAvailable) {
            isMoreDataAvailable = moreDataAvailable;
        }

        /* notifyDataSetChanged is final method so we can't override it
             call adapter.notifyDataChanged(); after update the list
             */
        public void notifyDataChanged(){
            notifyDataSetChanged();
            isLoading = false;
        }



        public void setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
            this.loadMoreListener = loadMoreListener;
        }


    }

    interface OnLoadMoreListener{
        void onLoadMore();
    }


}
