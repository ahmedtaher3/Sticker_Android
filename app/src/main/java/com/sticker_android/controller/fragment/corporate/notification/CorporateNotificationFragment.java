package com.sticker_android.controller.fragment.corporate.notification;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
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
import com.sticker_android.controller.activities.common.contest.ApplyCorporateContestActivity;
import com.sticker_android.controller.activities.corporate.home.CorporateHomeActivity;
import com.sticker_android.controller.fragment.base.BaseFragment;
import com.sticker_android.model.User;
import com.sticker_android.model.interfaces.NetworkPopupEventListener;
import com.sticker_android.model.notification.NotificationApp;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.sharedpref.AppPref;

import java.util.ArrayList;

import retrofit2.Call;


public class CorporateNotificationFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recNotification;
    ArrayList<String> strings = new ArrayList<>();
    private AppPref appPref;
    private User user;
    private ArrayList<NotificationApp> mNotificationList = new ArrayList<>();
    private NotificationAdaptor notificationAdaptor;
    private SwipeRefreshLayout swipeRefreshNotification;
    private CorporateHomeActivity mHostActivity;
    private LinearLayout llLoaderView;
    private RelativeLayout rlConnectionContainer;

    public CorporateNotificationFragment() {
    }

    public static CorporateNotificationFragment newInstance() {
        CorporateNotificationFragment fragment = new CorporateNotificationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

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
        getNotificationApi();
        appPref.saveNewMessagesCount(0);
        if (mHostActivity != null)
            mHostActivity.updateCallbackMessage();
        return view;
    }

    private void init() {
        appPref = new AppPref(getActivity());
        user = appPref.getUserInfo();
    }

    private void getNotificationApi() {
        if (swipeRefreshNotification != null)
            swipeRefreshNotification.setRefreshing(true);
        Call<ApiResponse> apiResponseCall = RestClient.getService().apiNotificationList(user.getLanguageId(), user.getAuthrizedKey(), user.getId(), "notification_list");
        apiResponseCall.enqueue(new ApiCall(getActivity()) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                swipeRefreshNotification.setRefreshing(false);
                if (apiResponse.status) {

                    mNotificationList = apiResponse.paylpad.notificationArrayList;
                    if (mNotificationList != null)
                        notificationAdaptor.setData(mNotificationList);
                }
            }

            @Override
            public void onFail(Call<ApiResponse> call, Throwable t) {
                swipeRefreshNotification.setRefreshing(false);

                if (mHostActivity != null)
                    mHostActivity.manageNoInternetConnectionLayout(getActivity(), rlConnectionContainer, new NetworkPopupEventListener() {
                        @Override
                        public void onOkClickListener(int reqCode) {

                        }

                        @Override
                        public void onRetryClickListener(int reqCode) {
                            getNotificationApi();
                        }
                    }, 0);
            }

        });
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
        getNotificationApi();
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
        public void onBindViewHolder(NotificationHolder holder, int position) {
            final NotificationApp notification = mNotificationItem.get(position);
            final int contestId = notification.contestObj.status;

            if (position % 3 == 0) {
                holder.imvNotification.setImageResource(R.drawable.ic_side_image_pink);
            }
            if (position % 3 == 1) {
                holder.imvNotification.setImageResource(R.drawable.ic_side_image_green);
            }
            if (position % 3 == 2) {
                holder.imvNotification.setImageResource(R.drawable.ic_side_image_blue);

            }
            holder.tvNotification.setText(notification.contestObj.msg);
            showData(holder, contestId);
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (contestId == 5) {
                        Intent intent = new Intent(getActivity(), ApplyCorporateContestActivity.class);
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
            public CardView cardView;

            public NotificationHolder(View view) {
                super(view);
                imvNotification = (ImageView) view.findViewById(R.id.imvNotification);
                tvNotification = (TextView) view.findViewById(R.id.tvNotification);
                tvTimeNotification = (TextView) view.findViewById(R.id.tvTimeNotification);
                imvtype = (ImageView) view.findViewById(R.id.imvtype);
                cardView = (CardView) view.findViewById(R.id.card_view);
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
        mHostActivity = (CorporateHomeActivity) context;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mHostActivity != null)
            mHostActivity.updateCallbackMessage();
    }
}
