package com.sticker_android.controller.activities.common.contestlist;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.controller.adaptors.DesignListAdapter;
import com.sticker_android.model.User;
import com.sticker_android.model.contest.FanContestAll;
import com.sticker_android.model.corporateproduct.Product;
import com.sticker_android.model.interfaces.DesignerActionListener;
import com.sticker_android.model.interfaces.MessageEventListener;
import com.sticker_android.model.interfaces.NetworkPopupEventListener;
import com.sticker_android.model.payload.Payload;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.AppLogger;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.helper.TimeUtility;
import com.sticker_android.utils.sharedpref.AppPref;
import com.sticker_android.view.EndlessRecyclerViewScrollListener;

import java.util.ArrayList;

import retrofit2.Call;

public class ContestAllItemListActivity extends AppBaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private Toolbar toolbar;

    private Product productObj;
    private RecyclerView recOngoingContestCorp;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout llNoDataFound;
    private AppPref appPref;
    private User mUserdata;
    private TextView tvNoAdsUploaded;
    private LinearLayoutManager mLayoutManager;

    private Context mContext;

    private RelativeLayout rlContent;
    private LinearLayout llLoaderView;
    private RelativeLayout rlConnectionContainer;
    private TextView txtNoDataFoundTitle, txtNoDataFoundContent;
    private ArrayList<Product> mProductList;
    private static final String TAG = ContestAllItemListActivity.class.getSimpleName();
    private View view;
    private int mCurrentPage = 0;
    private int PAGE_LIMIT;
    private ProgressBar progressBarLoadMore;
    private EndlessRecyclerViewScrollListener scrollListener2;
    private SwipeRefreshLayout swiperefresh;
    private ArrayList<FanContestAll> mContestList;
    private FanAllContestListAdaptor mAdapter;
    private String userContestId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest_all_item_list);
        PAGE_LIMIT = getResources().getInteger(R.integer.designed_item_page_limit);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        init();
        getProductData();
        setSupportActionBar(toolbar);
        setToolbarBackground();
        setViewReferences();
        setViewListeners();
        toolbar.setNavigationIcon(R.drawable.back_arrow_small);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        changeStatusBarColor(getResources().getColor(R.color.colorstatusBarCorporate));

        llNoDataFound.setVisibility(View.GONE);
        mContestList = new ArrayList<>();
        recyclerViewLayout();
        mAdapter=new FanAllContestListAdaptor(this);
        recOngoingContestCorp.setAdapter(mAdapter);
        getContestApi(false);
        newListeneradded();

    }

    private void getContestApi(final boolean isRefreshing) {
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

        if (userContestId != null) {
            Call<ApiResponse> apiResponseCall = RestClient.getService().getUserContestProductList(mUserdata.getLanguageId(), mUserdata.getAuthrizedKey(), mUserdata.getId(),
                    "" + userContestId, "fan_contest_list_all", index, limit);
            apiResponseCall.enqueue(new ApiCall(getActivity(), 1) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {

                    llLoaderView.setVisibility(View.GONE);
                    rlContent.setVisibility(View.VISIBLE);
                    swiperefresh.setRefreshing(false);

                    //remove wi-fi symbol when response got
                    if (rlConnectionContainer != null && rlConnectionContainer.getChildCount() > 0) {
                        rlConnectionContainer.removeAllViews();
                    }

                    try {
                        if (apiResponse.status) {
                            Payload payload = apiResponse.paylpad;

                            if (payload != null) {

                                if (isRefreshing) {

                                    if (payload.fanContestAllArrayList != null && payload.fanContestAllArrayList.size() != 0) {
                                        mContestList.clear();
                                        mContestList.addAll(payload.fanContestAllArrayList);
                                        llNoDataFound.setVisibility(View.GONE);
                                        recOngoingContestCorp.setVisibility(View.VISIBLE);
                                        mAdapter.setData(mContestList);
                                        mCurrentPage = 0;
                                        mCurrentPage++;
                                        AppLogger.debug(TAG,"ContestAllItemList called if ");

                                    } else {
                                        mContestList.clear();
                                        mAdapter.setData(mContestList);
                                        txtNoDataFoundContent.setText(R.string.txt_no_contest_found);
                                        AppLogger.debug(TAG,"ContestAllItemList called else ");

                                        showNoDataFound();
                                    }
                                } else {
                                    AppLogger.debug(TAG,"ContestAllItemList called else else ");

                                    if (mCurrentPage == 0) {
                                        mContestList.clear();
                                        if (payload.fanContestAllArrayList != null) {
                                            mContestList.addAll(payload.fanContestAllArrayList);
                                        }

                                        if (mContestList.size() != 0) {
                                            llNoDataFound.setVisibility(View.GONE);
                                            recOngoingContestCorp.setVisibility(View.VISIBLE);
                                            mAdapter.setData(mContestList);
                                        } else {
                                            showNoDataFound();

                                            txtNoDataFoundContent.setText(R.string.txt_no_contest_found);

                                            recOngoingContestCorp.setVisibility(View.GONE);
                                        }
                                    } else {
                                        AppLogger.error(TAG, "Remove loader...");
                                        mAdapter.removeLoader();
                                        if (payload.fanContestAllArrayList != null && payload.fanContestAllArrayList.size() != 0) {
                                            mContestList.addAll(payload.fanContestAllArrayList);
                                            mAdapter.setData(mContestList);
                                        }
                                    }

                                    if (payload.fanContestAllArrayList != null && payload.fanContestAllArrayList.size() != 0) {
                                        mCurrentPage++;
                                    }
                                }
                                AppLogger.error(TAG, "item list size => " + mContestList.size());

                            } else if (mContestList == null || (mContestList != null && mContestList.size() == 0)) {
                                txtNoDataFoundContent.setText(R.string.txt_no_contest_found);

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


                @Override
                public void onFail(final Call<ApiResponse> call, Throwable t) {

                    if (getActivity() != null) {
                        llLoaderView.setVisibility(View.GONE);
                        mAdapter.removeLoader();
                        swiperefresh.setRefreshing(false);

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
                                manageNoInternetConnectionLayout(getActivity(), rlConnectionContainer, new NetworkPopupEventListener() {
                                    @Override
                                    public void onOkClickListener(int reqCode) {
                                        rlContent.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onRetryClickListener(int reqCode) {
                                        getContestApi(isRefreshing);
                                    }
                                }, 0);
                            } else {
                                Utils.showToastMessage(ContestAllItemListActivity.this, getString(R.string.pls_check_ur_internet_connection));
                            }
                        }
                    }
                }
            });
        }
    }



    /**
     * Method is used to set the layout on recycler view
     */
    private void recyclerViewLayout() {
        recOngoingContestCorp.hasFixedSize();

        mLayoutManager = new LinearLayoutManager(this);

        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recOngoingContestCorp.setLayoutManager(mLayoutManager);
    }



    /**
     * Method is used to set the toolbar background
     */
    private void setToolbarBackground() {
        toolbar.setBackground(getResources().getDrawable(R.drawable.corporate_header_hdpi));
    }


    private void init() {
        appPref = new AppPref(this);
        mUserdata = appPref.getUserInfo();
    }

    private void setToolBarTitle(String type) {
        TextView textView = (TextView) toolbar.findViewById(R.id.tvToolbar);
        textView.setText(R.string.txt_all_ongoing_contest);

        toolbar.setTitle("");
    }


    private void getProductData() {

        if (getIntent().getExtras() != null) {

            productObj = getIntent().getExtras().getParcelable(AppConstant.PRODUCT_OBJ_KEY);
            if (productObj != null)
                setToolBarTitle(productObj.getType());
        }
        userContestId =getIntent().getExtras().getString("userContestId");
        AppLogger.debug(ContestAllItemListActivity.class.getSimpleName(),"userContestId"+userContestId);
    }

    @Override
    protected void setViewListeners() {
        swiperefresh.setOnRefreshListener(this);
    }

    @Override
    protected void setViewReferences() {

        swiperefresh =(SwipeRefreshLayout)findViewById(R.id.swiperefresh);
        recOngoingContestCorp = (RecyclerView) findViewById(R.id.recOngoingContestCorp);
        progressBarLoadMore = (ProgressBar) findViewById(R.id.progressBarLoadMore);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshAds);
        //  tvNoAdsUploaded = (TextView) view.findViewById(R.id.tvNoAdsUploaded);
        rlContent = (RelativeLayout) findViewById(R.id.rlContent);
        llNoDataFound = (LinearLayout) findViewById(R.id.llNoDataFound);
        txtNoDataFoundTitle = (TextView) findViewById(R.id.txtNoDataFoundTitle);
        txtNoDataFoundContent = (TextView) findViewById(R.id.txtNoDataFoundContent);
        rlConnectionContainer = (RelativeLayout) findViewById(R.id.rlConnectionContainer);
        llLoaderView = (LinearLayout) findViewById(R.id.llLoader);

    }

    @Override
    protected boolean isValidData() {
        return false;
    }


    private void newListeneradded() {

        scrollListener2 = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public int getFooterViewType(int defaultNoFooterViewType) {

                return 0;
            }

            @Override
            public void onLoadMore(int page, int totalItemsCount) {
            /*    getProductFromServer(false, "");
                corporateListAdaptor.addLoader();
     */
            }
        };
        // Adds the scroll listener to RecyclerView
        recOngoingContestCorp.addOnScrollListener(scrollListener2);

    }


    private void showNoDataFound() {
        llNoDataFound.setVisibility(View.VISIBLE);
        txtNoDataFoundTitle.setText("");
    }

    @Override
    public void onRefresh() {

    }

    /*new adaptor*/
    public class FanAllContestListAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final String TAG = FanAllContestListAdaptor.class.getSimpleName();
        private ArrayList<FanContestAll> mItems;
        private Context context;
        public boolean isLoaderVisible;

        private final int ITEM_FOOTER = 0;
        private final int ITEM_PRODUCT = 1;

        private TimeUtility timeUtility = new TimeUtility();
        AppPref appPref;

        User mUserdata;


        private DesignListAdapter.OnProductItemClickListener productItemClickListener;
        private DesignerActionListener designerActionListener;

        public class ViewHolder extends RecyclerView.ViewHolder {

            public ImageView imvSelected, imvOfContest, imvProductImage;
            public CardView cardItem;
            public ProgressBar pgrImage;
            public TextView tvEndDate;
            public CheckBox checkboxLike;
            public TextView tvFeatured;
            public ViewHolder(View view) {
                super(view);
                cardItem = (CardView) view.findViewById(R.id.card_view);
                imvSelected = (ImageView) view.findViewById(R.id.imvSelected);
                imvOfContest = (ImageView) view.findViewById(R.id.imvOfContest);
                imvProductImage = (ImageView) view.findViewById(R.id.imvProductImage);
                pgrImage = (ProgressBar) view.findViewById(R.id.pgrImage);
                tvEndDate = (TextView) view.findViewById(R.id.tv_name);
                checkboxLike = (CheckBox) view.findViewById(R.id.checkboxLike);
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
        public FanAllContestListAdaptor(Context cnxt) {
            mItems = new ArrayList<>();
            context = cnxt;
            appPref = new AppPref(context);
            mUserdata = appPref.getUserInfo();
        }

        public void setDesignerActionListener(DesignerActionListener actionListener) {
            this.designerActionListener = actionListener;
        }

        public void setOnProductClickListener(DesignListAdapter.OnProductItemClickListener productClickListener) {
            this.productItemClickListener = productClickListener;
        }

        public void setData(ArrayList<FanContestAll> data) {
            AppLogger.debug(TAG,"ContestAllItemList called"+data.size());

            if (data != null) {
                mItems = new ArrayList<>();
                mItems.addAll(data);
                notifyDataSetChanged();
                isLoaderVisible = false;
                AppLogger.debug(TAG,"ContestAllItemList called"+data.size());

            }
        }

        public void updateAdapterData(ArrayList<FanContestAll> data) {
            mItems = new ArrayList<>();
            mItems.addAll(data);
        }

        public void addLoader() {
            AppLogger.error(TAG, "Add loader... in adapter");
            FanContestAll postItem = new FanContestAll();
            postItem.dummyId = -1;
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
            FanContestAll postItem = new FanContestAll();
            postItem.dummyId = -1;
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

        public void updateModifiedItem(FanContestAll postItem) {
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
                final FanAllContestListAdaptor.LoaderViewHolder vh = new FanAllContestListAdaptor.LoaderViewHolder(v);
                return vh;
            } else {
                // create a new view
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contest_view_ongoing, parent, false);
                // set the view's size, margins, paddings and layout parameters
                final FanAllContestListAdaptor.ViewHolder vh = new FanAllContestListAdaptor.ViewHolder(v);

                vh.cardItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = vh.getAdapterPosition();
                        FanContestAll product = mItems.get(position);
                        // productItemClickListener.onProductItemClick(product);
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
                AppLogger.debug(TAG,"ContestAllItemList called");
                final FanAllContestListAdaptor.ViewHolder itemHolder = (FanAllContestListAdaptor.ViewHolder) holder;
                final Product listItem = mItems.get(position).product;


                itemHolder.tvEndDate.setText(Utils.dateModify(listItem.getExpireDate()));
                itemHolder.checkboxLike.setText(Utils.format(listItem.statics.likeCount));
                if (listItem.statics.likeCount > 0) {
                    itemHolder.checkboxLike.setChecked(true);
                    itemHolder.checkboxLike.setButtonDrawable(context.getResources().getDrawable(R.drawable.ic_hand));
                } else {
                    itemHolder.checkboxLike.setChecked(false);
                    itemHolder.checkboxLike.setButtonDrawable(context.getResources().getDrawable(R.drawable.ic_like));

                }
                itemHolder.checkboxLike.setText(""+listItem.statics.likeCount);
                if (listItem.getImagePath() != null && !listItem.getImagePath().isEmpty())
                    Glide.with(context)
                            .load(listItem.getImagePath()).fitCenter()
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    itemHolder.pgrImage.setVisibility(View.GONE);
                                    itemHolder.imvProductImage.setVisibility(View.GONE);

                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    itemHolder.pgrImage.setVisibility(View.GONE);
                                    itemHolder.imvProductImage.setVisibility(View.GONE);

                                    return false;
                                }
                            })
                            .into(itemHolder.imvOfContest);
                if (listItem.isFeatured > 0)
                    itemHolder.tvFeatured.setVisibility(View.VISIBLE);
                else
                    itemHolder.tvFeatured.setVisibility(View.GONE);


            }
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mItems.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (mItems.get(position).dummyId == -1) {
                return ITEM_FOOTER;
            } else {
                return ITEM_PRODUCT;
            }
        }
    }


}
