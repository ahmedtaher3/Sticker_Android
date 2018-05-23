package com.sticker_android.controller.activities.common.contestlist;

import android.content.Context;
import android.graphics.Color;
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
import com.sticker_android.controller.fragment.corporate.contest.CorporateContestOngoingFragment;
import com.sticker_android.model.User;
import com.sticker_android.model.contest.ContestCompleted;
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

public class ContestCompletedAllUserActivity extends AppBaseActivity  implements SwipeRefreshLayout.OnRefreshListener{


    private RecyclerView recOngoingContestCorp;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout llNoDataFound;
    private AppPref appPref;
    private User mUserdata;
    private TextView tvNoAdsUploaded;
    private LinearLayoutManager mLayoutManager;

    private Context mContext;
    private int mCurrentPage = 0;
    private int PAGE_LIMIT;
    private CompleteAllContestList mAdapter;
    private RelativeLayout rlContent;
    private LinearLayout llLoaderView;
    private RelativeLayout rlConnectionContainer;
    private TextView txtNoDataFoundTitle, txtNoDataFoundContent;
    private ArrayList<Product> mProductList;
    private static final String TAG = CorporateContestOngoingFragment.class.getSimpleName();
    private View view;
    private Product productObj;
    private Toolbar toolbar;
    private String userContestId;
    private ArrayList<ContestCompleted> mContestList;
    private EndlessRecyclerViewScrollListener scrollListener2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest_completed_all_user);

        PAGE_LIMIT = getActivity().getResources().getInteger(R.integer.designed_item_page_limit);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        init();
        getuserInfo();
        getProductData();
        setViewReferences();
        setViewListeners();
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


        mAdapter=new CompleteAllContestList(this);
        llNoDataFound.setVisibility(View.GONE);
        mContestList = new ArrayList<>();
        mCurrentPage = 0;
        getContestApi(false);
        recOngoingContestCorp.setAdapter(mAdapter);
        recyclerViewLayout();


        newListeneradded();
    }

    private void init() {

        appPref = new AppPref(getActivity());
    }

    /**
     * Method is used to set the toolbar background
     */
    private void setToolbarBackground() {
        toolbar.setBackground(getResources().getDrawable(R.drawable.corporate_header_hdpi));
    }


    private void getuserInfo() {
        mUserdata = appPref.getUserInfo();
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


    private void setToolBarTitle(String type) {
        TextView textView = (TextView) toolbar.findViewById(R.id.tvToolbar);
        textView.setText(R.string.txt_all_completed);

        toolbar.setTitle("");
    }


    private void newListeneradded() {

        scrollListener2 = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public int getFooterViewType(int defaultNoFooterViewType) {

                return 0;
            }

            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                getContestApi(false);
               // mAdapter.addLoader();
            /*    getProductFromServer(false, "");
                corporateListAdaptor.addLoader();
     */
            }
        };
        // Adds the scroll listener to RecyclerView
        recOngoingContestCorp.addOnScrollListener(scrollListener2);

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


    @Override
    protected void setViewListeners() {
        swipeRefreshLayout.setOnRefreshListener(this);

    }



    @Override
    protected boolean isValidData() {
        return false;
    }



    @Override
    public void onRefresh() {
        if (Utils.isConnectedToInternet(this)) {
            mAdapter.setData(new ArrayList<ContestCompleted>());
            scrollListener2.resetState();
            mCurrentPage=0;
            getContestApi(true);
        } else {
            swipeRefreshLayout.setRefreshing(false);
            Utils.showToastMessage(this, getString(R.string.pls_check_ur_internet_connection));
        }

    }

    private void showNoDataFound() {
        llNoDataFound.setVisibility(View.VISIBLE);
        txtNoDataFoundTitle.setText("");
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
                    "" + userContestId, "completed_contest_list", index, limit);
            apiResponseCall.enqueue(new ApiCall(getActivity(), 1) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {

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

                                    if (payload.completedArrayList != null && payload.completedArrayList.size() != 0) {
                                        mContestList.clear();
                                        mContestList.addAll(payload.completedArrayList);
                                        llNoDataFound.setVisibility(View.GONE);
                                        recOngoingContestCorp.setVisibility(View.VISIBLE);
                                        mAdapter.setData(mContestList);
                                        mCurrentPage = 0;
                                        mCurrentPage++;
                                    } else {
                                        mContestList.clear();
                                        mAdapter.setData(mContestList);
                                        txtNoDataFoundContent.setText(R.string.txt_no_contest_found);

                                        showNoDataFound();
                                    }
                                } else {
                                    if (mCurrentPage == 0) {
                                        mContestList.clear();
                                        if (payload.completedArrayList != null) {
                                            mContestList.addAll(payload.completedArrayList);
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
                                        if (payload.completedArrayList != null && payload.completedArrayList.size() != 0) {
                                            mContestList.addAll(payload.completedArrayList);
                                            mAdapter.setData(mContestList);
                                        }
                                    }

                                    if (payload.completedArrayList != null && payload.completedArrayList.size() != 0) {
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

                    t.printStackTrace();
                    if (getActivity() != null) {
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
                                Utils.showToastMessage(ContestCompletedAllUserActivity.this, getString(R.string.pls_check_ur_internet_connection));
                            }
                        }
                    }
                }
            });
        }
    }



    @Override
    protected void setViewReferences() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        recOngoingContestCorp = (RecyclerView) findViewById(R.id.recOngoingContestCorp);
        tvNoAdsUploaded = (TextView) findViewById(R.id.tvNoAdsUploaded);
        rlContent = (RelativeLayout) findViewById(R.id.rlContent);
        llNoDataFound = (LinearLayout) findViewById(R.id.llNoDataFound);
        txtNoDataFoundTitle = (TextView) findViewById(R.id.txtNoDataFoundTitle);
        txtNoDataFoundContent = (TextView) findViewById(R.id.txtNoDataFoundContent);
        rlConnectionContainer = (RelativeLayout) findViewById(R.id.rlConnectionContainer);
        llLoaderView = (LinearLayout) findViewById(R.id.llLoader);
    }



    /*new adaptor*/
    public class CompleteAllContestList extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final String TAG = CompleteAllContestList.class.getSimpleName();
        private ArrayList<ContestCompleted> mItems;
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

            ImageView imvSelected, imvOfContest, imvProductImage;
            CardView cardItem;
            ProgressBar pgrImage;
            TextView totalNumberOfCount, tvContestStatus;
            TextView tvFeatured;

            public ViewHolder(View view) {
                super(view);
                cardItem = (CardView) itemView.findViewById(R.id.card_view);
                imvSelected = (ImageView) itemView.findViewById(R.id.imvSelected);
                imvOfContest = (ImageView) itemView.findViewById(R.id.imvOfContest);
                imvProductImage = (ImageView) itemView.findViewById(R.id.imvProductImage);
                pgrImage = (ProgressBar) itemView.findViewById(R.id.pgrImage);
                totalNumberOfCount = (TextView) itemView.findViewById(R.id.tv_total_count_number);
                tvContestStatus = (TextView) itemView.findViewById(R.id.tv_contest_status);
                tvFeatured = (TextView) itemView.findViewById(R.id.tvFeatured);
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
        public CompleteAllContestList(Context cnxt) {
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

        public void setData(ArrayList<ContestCompleted> data) {
            if (data != null) {
                mItems = new ArrayList<>();
                mItems.addAll(data);
                notifyDataSetChanged();
                isLoaderVisible = false;
            }
        }

        public void updateAdapterData(ArrayList<ContestCompleted> data) {
            mItems = new ArrayList<>();
            mItems.addAll(data);
        }

        public void addLoader() {
            AppLogger.error(TAG, "Add loader... in adapter");
            ContestCompleted postItem = new ContestCompleted();
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
            ContestCompleted postItem = new ContestCompleted();
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

        public void updateModifiedItem(ContestCompleted postItem) {
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
                final CompleteAllContestList.LoaderViewHolder vh = new CompleteAllContestList.LoaderViewHolder(v);
                return vh;
            } else {
                // create a new view
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contest_completed_list, parent, false);
                // set the view's size, margins, paddings and layout parameters
                final CompleteAllContestList.ViewHolder vh = new CompleteAllContestList.ViewHolder(v);


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
                final CompleteAllContestList.ViewHolder itemHolder = (CompleteAllContestList.ViewHolder) holder;
             //   final Product listItem = mItems.get(position).product;


                final Product listItem = mItems.get(position).productList;

                if (position == 0) {
                    itemHolder.tvContestStatus.setText(R.string.txt_winner);
                    itemHolder.tvContestStatus.setTextColor(context.getResources().getColor(R.color.colorHomeGreen));
                } else {
                    itemHolder.tvContestStatus.setTextColor(Color.RED);
                    itemHolder.tvContestStatus.setText(R.string.txt_looser);
                }
                itemHolder.totalNumberOfCount.setText(Utils.format(listItem.statics.likeCount));
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
                if (productObj.isFeatured > 0)
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
