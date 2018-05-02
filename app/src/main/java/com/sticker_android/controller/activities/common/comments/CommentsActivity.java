package com.sticker_android.controller.activities.common.comments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sticker_android.R;
import com.sticker_android.constant.AppConstant;
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.model.User;
import com.sticker_android.model.corporateproduct.Product;
import com.sticker_android.model.interfaces.NetworkPopupEventListener;
import com.sticker_android.model.rejection.Reject;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.sharedpref.AppPref;

import java.util.ArrayList;

import retrofit2.Call;

public class CommentsActivity extends AppBaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private AppPref appPref;
    private User user;
    private RecyclerView recComments;
    private CommentsAdaptor commentsAdaptor;
    private LinearLayout llLoaderView;
    private RelativeLayout rlConnectionContainer;
    private RelativeLayout rlContent;
    private LinearLayout llNoDataFound;
    private TextView txtNoDataFoundTitle;
    private TextView txtNoDataFoundContent;
    private SwipeRefreshLayout swiperefresh;
    private Product productObj;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        init();
        getProductData();
        setToolbar();
        toolbar.setNavigationIcon(R.drawable.back_arrow_small);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setViewReferences();
        setViewListeners();
        recyclerViewLayout();
        setAdaptor();
        // setDummyData();
        setDataIntoAdaptor();

    }

    private void setDataIntoAdaptor() {

        commentsAdaptor.setData((ArrayList<Reject>) productObj.rejectionList);
    }


    private void getProductData() {

        if (getIntent().getExtras() != null) {

            productObj = getIntent().getExtras().getParcelable(AppConstant.PRODUCT_OBJ_COMMENTS);
        }
    }

    /**
     * Method is used to set the toolbar
     */
    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setToolbarBackground();
        setToolBarTitle();
        setSupportActionBar(toolbar);
    }

    /**
     * Method is used to set the toolbar title
     */
    private void setToolBarTitle() {
        TextView textView = (TextView) toolbar.findViewById(R.id.tvToolbar);
        textView.setText(getString(R.string.txt_comments));
        toolbar.setTitle(" ");
    }

    /**
     * Method is used to set the toolbar background
     */
    private void setToolbarBackground() {
        if (productObj != null) {
            if (productObj.getType().equalsIgnoreCase("ads") || productObj.getType().equalsIgnoreCase("product")) {
                toolbar.setBackground(getResources().getDrawable(R.drawable.corporate_header_hdpi));
                changeStatusBarColor(getResources().getColor(R.color.colorstatusBarCorporate));
            } else {
                toolbar.setBackground(getResources().getDrawable(R.drawable.designer_header_hdpi));
                changeStatusBarColor(getResources().getColor(R.color.colorstatusBarDesigner));

            }
        }
    }

    private void init() {
        appPref = new AppPref(getActivity());
        user = appPref.getUserInfo();
    }


    private void setAdaptor() {
        commentsAdaptor = new CommentsAdaptor();
        recComments.setAdapter(commentsAdaptor);
    }

    /**
     * Method is used to set the layout on recycler view
     */
    private void recyclerViewLayout() {
        recComments.hasFixedSize();

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());

        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recComments.setLayoutManager(mLayoutManager);
    }


    private void commentsApiCall(final boolean isRefresh) {
        if (isRefresh)
            swiperefresh.setRefreshing(true);
        else
            llLoaderView.setVisibility(View.VISIBLE);

        Call<ApiResponse> apiResponseCall = RestClient.getService().apiNotificationList(user.getLanguageId(), user.getAuthrizedKey(), user.getId(), "notification_list");
        apiResponseCall.enqueue(new ApiCall(getActivity()) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (isRefresh)
                    swiperefresh.setRefreshing(false);
                else
                    llLoaderView.setVisibility(View.GONE);
                if (apiResponse.status) {

                 /*   mcommentsList = apiResponse.paylpad.notificationArrayList;
                    if (mcommentsList != null) {
                        notificationAdaptor.setData(mcommentsList);
                        txtNoDataFoundContent.setVisibility(View.GONE);
                    }
                    if (mcommentsList == null) {
                        txtNoDataFoundContent.setText(R.string.txt_no_notification_found);
                        showNoDataFound();
                    }
                    if (mcommentsList != null && mcommentsList.size() == 0) {
                        txtNoDataFoundContent.setText(R.string.txt_no_notification_found);
                        showNoDataFound();
                    }*/

                }
            }

            @Override
            public void onFail(Call<ApiResponse> call, Throwable t) {
                if (isRefresh)
                    swiperefresh.setRefreshing(false);
                else
                    llLoaderView.setVisibility(View.GONE);

                manageNoInternetConnectionLayout(getActivity(), rlConnectionContainer, new NetworkPopupEventListener() {
                    @Override
                    public void onOkClickListener(int reqCode) {

                    }

                    @Override
                    public void onRetryClickListener(int reqCode) {
                        commentsApiCall(false);
                    }
                }, 0);
            }

        });
    }

    private void showNoDataFound() {
        llNoDataFound.setVisibility(View.VISIBLE);
        txtNoDataFoundTitle.setText("");
    }


    @Override
    protected void setViewListeners() {
        swiperefresh.setOnRefreshListener(this);

    }

    @Override
    protected void setViewReferences() {
        recComments = (RecyclerView) findViewById(R.id.recComments);
        swiperefresh = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        rlConnectionContainer = (RelativeLayout) findViewById(R.id.rlConnectionContainer);
        llLoaderView = (LinearLayout) findViewById(R.id.llLoader);
        rlContent = (RelativeLayout) findViewById(R.id.rlContent);
        llNoDataFound = (LinearLayout) findViewById(R.id.llNoDataFound);
        txtNoDataFoundTitle = (TextView) findViewById(R.id.txtNoDataFoundTitle);
        txtNoDataFoundContent = (TextView) findViewById(R.id.txtNoDataFoundContent);

    }

    @Override
    protected boolean isValidData() {
        return false;
    }

    @Override
    public void onRefresh() {
        commentsApiCall(true);
    }


   /*CommentsAdaptor */

    public class CommentsAdaptor extends RecyclerView.Adapter<CommentsAdaptor.NotificationHolder> {

        private ArrayList<Reject> mCommentsItem = new ArrayList<>();

        public CommentsAdaptor() {

        }

        @Override
        public CommentsAdaptor.NotificationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new CommentsAdaptor.NotificationHolder(inflater.inflate(R.layout.view_item_comments, parent, false));
        }

        @Override
        public void onBindViewHolder(CommentsAdaptor.NotificationHolder holder, int position) {

            Reject reject = mCommentsItem.get(position);
            if (reject.actionBy.equals("admin")) {
                holder.tvCommentHeader.setText("Admin's Comment");
                holder.tvDescription.setText(reject.description);
                holder.tvCommentHeader.setTextColor(Color.GRAY);

            } else {
                if (productObj.getType().equalsIgnoreCase("ads") || productObj.getType().equalsIgnoreCase("product"))
                    holder.tvCommentHeader.setTextColor(getResources().getColor(R.color.colorCorporateText));
                else
                    holder.tvCommentHeader.setTextColor(getResources().getColor(R.color.colorDesignerText));

                holder.tvCommentHeader.setText("User Comment");
                holder.tvDescription.setText(reject.description);

            }
           /* if (reject.description.equals("") && reject.description.isEmpty()) {
                holder.tvCommentHeader.setVisibility(View.GONE);
                holder.tvDescription.setVisibility(View.GONE);

            } else {
                holder.tvCommentHeader.setVisibility(View.VISIBLE);
                holder.tvDescription.setVisibility(View.VISIBLE);

            }*/
        }

        public void setData(ArrayList<Reject> data) {
            if (data != null) {
                mCommentsItem = new ArrayList<>();
                mCommentsItem.addAll(data);
                notifyDataSetChanged();
            }
        }

        @Override
        public int getItemCount() {
            return mCommentsItem.size();
        }


        class NotificationHolder extends RecyclerView.ViewHolder {
            TextView tvCommentHeader, tvDescription;

            public NotificationHolder(View view) {

                super(view);

                tvCommentHeader = (TextView) view.findViewById(R.id.tvCommentHeader);
                tvDescription = (TextView) view.findViewById(R.id.tvDescription);

            }
        }


    }


}
