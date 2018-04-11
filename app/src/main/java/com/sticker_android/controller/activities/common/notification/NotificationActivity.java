package com.sticker_android.controller.activities.common.notification;

import android.app.Notification;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.controller.fragment.corporate.ad.AdsFragment;
import com.sticker_android.controller.notification.LocalNotification;
import com.sticker_android.model.corporateproduct.Product;
import com.sticker_android.utils.Utils;

import java.util.ArrayList;

/**
 * Class is used for the notification
 */
public class NotificationActivity extends AppBaseActivity {
    private RecyclerView recNotification;
    ArrayList<String> strings = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        setViewReferences();
        setViewListeners();
        recyclerViewLayout();
        LocalNotification localNotification = new LocalNotification();
        localNotification.setNotification(this, "sdcdscdc", "dscdscdc");
        strings.add("hello test");
        setAdaptor();

    }

    private void setAdaptor() {
        NotificationAdaptor notificationAdaptor = new NotificationAdaptor( strings);
        recNotification.setAdapter(notificationAdaptor);
    }

    /**
     * Method is used to set the layout on recycler view
     */
    private void recyclerViewLayout() {
        recNotification.hasFixedSize();

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);

        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recNotification.setLayoutManager(mLayoutManager);
    }


    @Override
    protected void setViewListeners() {

    }

    @Override
    protected void setViewReferences() {
        recNotification=findViewById(R.id.recNotification);
    }

    @Override
    protected boolean isValidData() {
        return false;
    }




      /*Notification adaptor*/

    public class NotificationAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private ArrayList<String> notificationList = new ArrayList<>();

        public NotificationAdaptor( ArrayList<String> notificationList) {
            this.notificationList = notificationList;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(NotificationActivity.this);
            return new NotificationHolder(inflater.inflate(R.layout.notification_view, parent, false));
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        }


        @Override
        public int getItemCount() {
            return notificationList.size();
        }


        class NotificationHolder extends RecyclerView.ViewHolder {
            ImageView imvNotification;
            TextView tvNotification;
            TextView tvTimeNotification;

            public NotificationHolder(View view) {
                super(view);
                imvNotification = (ImageView) view.findViewById(R.id.imvNotification);
                tvNotification = (TextView) view.findViewById(R.id.tvNotification);
                tvTimeNotification = (TextView) view.findViewById(R.id.tvTimeNotification);
            }
        }


    }

}
