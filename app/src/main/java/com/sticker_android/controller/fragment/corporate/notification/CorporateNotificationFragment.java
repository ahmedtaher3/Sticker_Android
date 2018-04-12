package com.sticker_android.controller.fragment.corporate.notification;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sticker_android.R;
import com.sticker_android.controller.fragment.base.BaseFragment;
import com.sticker_android.controller.notification.LocalNotification;

import java.util.ArrayList;


public class CorporateNotificationFragment extends BaseFragment {

    private RecyclerView recNotification;
    ArrayList<String> strings = new ArrayList<>();

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
        setViewReferences(view);
        setViewListeners();
        recyclerViewLayout();
        LocalNotification localNotification = new LocalNotification();
        localNotification.setNotification(getActivity(), "sdcdscdc", "dscdscdc");
        strings.add("hello test");
        strings.add("hello test");
        strings.add("hello test");
        setAdaptor();
        return view;
    }

    @Override
    protected void setViewListeners() {

    }

    @Override
    protected void setViewReferences(View view) {
        recNotification = (RecyclerView) view.findViewById(R.id.recNotification);
    }

    @Override
    protected boolean isValidData() {
        return false;
    }


    private void setAdaptor() {
        NotificationAdaptor notificationAdaptor = new NotificationAdaptor(strings);
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

      /*Notification adaptor*/

    public class NotificationAdaptor extends RecyclerView.Adapter<NotificationAdaptor.NotificationHolder> {

        private ArrayList<String> notificationList = new ArrayList<>();

        public NotificationAdaptor(ArrayList<String> notificationList) {
            this.notificationList = notificationList;
        }

        @Override
        public NotificationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new NotificationHolder(inflater.inflate(R.layout.notification_view, parent, false));
        }

        @Override
        public void onBindViewHolder(NotificationHolder holder, int position) {
            if (position == 0) {
                holder.imvNotification.setImageResource(R.drawable.ic_side_image_pink);
             }
            if (position == 1) {
                holder.imvNotification.setImageResource(R.drawable.ic_side_image_green);
                holder.imvtype.setImageResource(R.drawable.ic_like_notification);
            }
            if (position == 2) {
                holder.imvNotification.setImageResource(R.drawable.ic_side_image_blue);
                holder.imvtype.setImageResource(R.drawable.ic_share_notification);

            }
        }


        @Override
        public int getItemCount() {
            return strings.size();
        }


        class NotificationHolder extends RecyclerView.ViewHolder {
            public ImageView imvNotification;
            public TextView tvNotification;
            public TextView tvTimeNotification;
            public ImageView imvtype;

            public NotificationHolder(View view) {
                super(view);
                imvNotification = (ImageView) view.findViewById(R.id.imvNotification);
                tvNotification = (TextView) view.findViewById(R.id.tvNotification);
                tvTimeNotification = (TextView) view.findViewById(R.id.tvTimeNotification);
                imvtype = (ImageView) view.findViewById(R.id.imvtype);
            }
        }


    }
}
