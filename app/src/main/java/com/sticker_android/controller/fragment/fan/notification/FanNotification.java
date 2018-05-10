package com.sticker_android.controller.fragment.fan.notification;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sticker_android.R;
import com.sticker_android.constant.AppConstant;
import com.sticker_android.controller.activities.fan.home.FanHomeActivity;
import com.sticker_android.controller.activities.fan.home.contest.FanContestListActivity;
import com.sticker_android.controller.fragment.base.BaseFragment;
import com.sticker_android.controller.notification.LocalNotification;
import com.sticker_android.model.User;
import com.sticker_android.model.interfaces.NetworkPopupEventListener;
import com.sticker_android.model.notification.NotificationApp;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.helper.TimeUtility;
import com.sticker_android.utils.sharedpref.AppPref;
import com.sticker_android.view.BadgeUtils;

import java.util.ArrayList;

import retrofit2.Call;

/**
 * Created by user on 24/4/18.
 */

public class FanNotification extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recNotification;
    ArrayList<String> strings = new ArrayList<>();
    private AppPref appPref;
    private User user;
    private ArrayList<NotificationApp> mNotificationList = new ArrayList<>();
    private NotificationAdaptor notificationAdaptor;
    private SwipeRefreshLayout swipeRefreshNotification;
    private FanHomeActivity mHostActivity;
    private LinearLayout llLoaderView;
    private RelativeLayout rlConnectionContainer;
    private RelativeLayout rlContent;
    private LinearLayout llNoDataFound;
    private TextView txtNoDataFoundTitle;
    private TextView txtNoDataFoundContent;
    TimeUtility timeUtility = new TimeUtility();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification_corporate, container, false);
        init();
        setViewReferences(view);
        setViewListeners();
        recyclerViewLayout();
        setAdaptor();
        getNotificationApi(false);
        llNoDataFound.setVisibility(View.GONE);
        appPref.saveNewMessagesCount(0);
        if (mHostActivity != null)
            mHostActivity.updateCallbackMessage();
        LocalNotification.clearNotifications(getActivity());
        BadgeUtils.clearBadge(getActivity());
        return view;
    }

    private void init() {
        appPref = new AppPref(getActivity());
        user = appPref.getUserInfo();
    }

    private void getNotificationApi(final boolean isRefresh) {
        if (isRefresh)
            swipeRefreshNotification.setRefreshing(true);
        else
            llLoaderView.setVisibility(View.VISIBLE);

        Call<ApiResponse> apiResponseCall = RestClient.getService().apiNotificationList(user.getLanguageId(), user.getAuthrizedKey(), user.getId(), "notification_list");
        apiResponseCall.enqueue(new ApiCall(getActivity()) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (isRefresh)
                    swipeRefreshNotification.setRefreshing(false);
                else
                    llLoaderView.setVisibility(View.GONE);

                if (apiResponse.status) {

                    mNotificationList = apiResponse.paylpad.notificationArrayList;
                    if (mNotificationList != null) {
                        notificationAdaptor.setData(filterData(mNotificationList));
                        llNoDataFound.setVisibility(View.GONE);
                    }
                    if(mNotificationList!=null&&mNotificationList.size()==0){
                        txtNoDataFoundContent.setText(R.string.txt_no_notification_found);
                        showNoDataFound();
                    }
                }
            }

            @Override
            public void onFail(Call<ApiResponse> call, Throwable t) {
                if (isRefresh)
                    swipeRefreshNotification.setRefreshing(false);
                else
                    llLoaderView.setVisibility(View.GONE);

                if (mHostActivity != null)
                    mHostActivity.manageNoInternetConnectionLayout(getActivity(), rlConnectionContainer, new NetworkPopupEventListener() {
                        @Override
                        public void onOkClickListener(int reqCode) {

                        }

                        @Override
                        public void onRetryClickListener(int reqCode) {
                            getNotificationApi(false);
                        }
                    }, 0);
            }

        });
    }
    private void showNoDataFound() {
        llNoDataFound.setVisibility(View.VISIBLE);
        txtNoDataFoundTitle.setText("");
    }
    private ArrayList<NotificationApp> filterData(ArrayList<NotificationApp> mNotificationList) {

        ArrayList<NotificationApp> tempList = new ArrayList<>();
        for (NotificationApp notificationApp :
                mNotificationList) {
            if (notificationApp.acme.contestObj.status == 5) {

                tempList.add(notificationApp);
            }
        }
        return tempList;
    }

    @Override
    protected void setViewListeners() {
        swipeRefreshNotification.setOnRefreshListener(this);
    }

    @Override
    protected void setViewReferences(View view) {

        recNotification = (RecyclerView) view.findViewById(R.id.recNotification);
        swipeRefreshNotification = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshNotification);
        rlConnectionContainer = (RelativeLayout) view.findViewById(R.id.rlConnectionContainer);
        llLoaderView = (LinearLayout) view.findViewById(R.id.llLoader);
        rlContent = (RelativeLayout) view.findViewById(R.id.rlContent);
        llNoDataFound = (LinearLayout) view.findViewById(R.id.llNoDataFound);
        txtNoDataFoundTitle = (TextView) view.findViewById(R.id.txtNoDataFoundTitle);
        txtNoDataFoundContent = (TextView) view.findViewById(R.id.txtNoDataFoundContent);


    }

    @Override
    protected boolean isValidData() {
        return false;
    }


    private void setAdaptor() {
        notificationAdaptor = new NotificationAdaptor();
        recNotification.setAdapter(notificationAdaptor);
    }

    /**
     * Method is used to set the layout on recycler view
     */
    private void recyclerViewLayout() {
        recNotification.hasFixedSize();

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());

        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recNotification.setLayoutManager(mLayoutManager);
    }

    @Override
    public void onRefresh() {
        getNotificationApi(true);
    }

      /*NotificationApp adaptor*/

    public class NotificationAdaptor extends RecyclerView.Adapter<NotificationAdaptor.NotificationHolder> {

        private ArrayList<NotificationApp> mNotificationItem = new ArrayList<>();

        public NotificationAdaptor() {

        }

        @Override
        public NotificationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new NotificationHolder(inflater.inflate(R.layout.notification_view, parent, false));
        }

        @Override
        public void onBindViewHolder(NotificationAdaptor.NotificationHolder holder, int position) {
            final NotificationApp notification = mNotificationItem.get(position);
            final int contestId = notification.acme.contestObj.status;

            if (position % 3 == 0) {
                holder.imvNotification.setImageResource(R.drawable.ic_side_image_pink);
            }
            if (position % 3 == 1) {
                holder.imvNotification.setImageResource(R.drawable.ic_side_image_green);
            }
            if (position % 3 == 2) {
                holder.imvNotification.setImageResource(R.drawable.ic_side_image_blue);

            }
            if(notification.acme.contestObj.status==8){
                holder.imvtype.setImageResource(R.drawable.ic_like_notification);
            }
            holder.tvNotification.setText(notification.acme.contestObj.msg);
            holder.tvTimeNotification.setText(timeUtility.covertTimeToText(Utils.convertToCurrentTimeZone(notification.cratedDate),getActivity()));

            showData(holder, contestId);
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (contestId == 5) {
                        Intent intent = new Intent(getActivity(), FanContestListActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(AppConstant.NOTIFICATION_OBJ, notification);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, AppConstant.INTENT_NOTIFICATION_CODE);
                    }
                }
            });
            /*  startActivity(new Intent(getActivity(), ApplyCorporateContestActivity.class));*/
        }

        private void showData(NotificationHolder holder, int contestId) {
            switch (contestId) {
                case 5:
                    holder.imvtype.setImageResource(R.drawable.ic_trophy_notification);
                    break;
                case 2:
                    holder.imvtype.setImageResource(R.drawable.ic_share_notification);
                    break;
                case 6:
                    holder.imvtype.setImageResource(R.drawable.ic_like_notification);
                    break;
            }
        }


        public void setData(ArrayList<NotificationApp> data) {
            mNotificationItem = new ArrayList<>();
            mNotificationItem.addAll(data);
            notifyDataSetChanged();

        }

        @Override
        public int getItemCount() {
            return mNotificationItem.size();
        }


        class NotificationHolder extends RecyclerView.ViewHolder {
            public ImageView imvNotification;
            public TextView tvNotification;
            public TextView tvTimeNotification;
            public ImageView imvtype;
            public RelativeLayout cardView;

            public NotificationHolder(View view) {
                super(view);
                imvNotification = (ImageView) view.findViewById(R.id.imvNotification);
                tvNotification = (TextView) view.findViewById(R.id.tvNotification);
                tvTimeNotification = (TextView) view.findViewById(R.id.tvTimeNotification);
                imvtype = (ImageView) view.findViewById(R.id.imvtype);
                cardView = (RelativeLayout) view.findViewById(R.id.card_view);
            }
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                case AppConstant.INTENT_NOTIFICATION_CODE:
                    getActivity().onBackPressed();
                    break;
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mHostActivity = (FanHomeActivity) context;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mHostActivity != null)
            mHostActivity.updateCallbackMessage();
    }
}
