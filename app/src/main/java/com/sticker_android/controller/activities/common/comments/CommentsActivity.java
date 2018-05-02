package com.sticker_android.controller.activities.common.comments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.sticker_android.model.interfaces.NetworkPopupEventListener;
import com.sticker_android.model.notification.NotificationApp;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        init();
        setViewReferences();
        setViewListeners();
        recyclerViewLayout();
        setAdaptor();
        setDummyData();

    }

    private void setDummyData() {

        ArrayList<String> strings = new ArrayList<>();
        strings.add("check");
        strings.add("check");
        strings.add("check");
        strings.add("check");
        strings.add("check");
        commentsAdaptor.setData(strings);
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

    }


   /*CommentsAdaptor */

    public class CommentsAdaptor extends RecyclerView.Adapter<CommentsAdaptor.NotificationHolder> {

        private ArrayList<String> mCommentsItem = new ArrayList<>();

        public CommentsAdaptor() {

        }

        @Override
        public CommentsAdaptor.NotificationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new CommentsAdaptor.NotificationHolder(inflater.inflate(R.layout.view_item_comments, parent, false));
        }

        @Override
        public void onBindViewHolder(CommentsAdaptor.NotificationHolder holder, int position) {
        }

        public void setData(ArrayList<String> data) {
            if(data!=null) {
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
