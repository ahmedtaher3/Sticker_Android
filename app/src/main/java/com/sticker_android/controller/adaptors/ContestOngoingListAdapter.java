package com.sticker_android.controller.adaptors;/*
package com.sticker_android.controller.adaptors;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.sticker_android.R;
import com.sticker_android.model.contest.OngoingContestList;
import com.sticker_android.utils.helper.TimeUtility;

import java.util.ArrayList;

*/

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.sticker_android.R;
import com.sticker_android.model.contest.OngoingContestList;
import com.sticker_android.utils.helper.TimeUtility;

import java.util.ArrayList;

/**
 * Created by user on 17/4/18.
 */

public class ContestOngoingListAdapter extends RecyclerView.Adapter<ContestOngoingListAdapter.OngoingListViewHolder> {

    Context context;
    private ArrayList<OngoingContestList> mItems = new ArrayList<>();
    private TimeUtility timeUtility = new TimeUtility();

    public ContestOngoingListAdapter(Context context) {
        this.context = context;

    }

    @Override
    public OngoingListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return new OngoingListViewHolder(inflater.inflate(R.layout.contest_view_ongoing, parent, false));
    }

    @Override
    public void onBindViewHolder(final OngoingListViewHolder holder, int position) {

        final OngoingContestList listItem = mItems.get(position);

        holder.tvEndDate.setText(listItem.contestInfo.expireDate);
        holder.checkboxLike.setText("0");
        if (listItem.productList.getImagePath() != null && !listItem.productList.getImagePath().isEmpty())
            Glide.with(context)
                    .load(listItem.productList.getImagePath()).fitCenter()
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            holder.pgrImage.setVisibility(View.GONE);
                            holder.imvProductImage.setVisibility(View.GONE);

                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            holder.pgrImage.setVisibility(View.GONE);
                            holder.imvProductImage.setVisibility(View.GONE);

                            return false;
                        }
                    })
                    .into(holder.imvOfContest);


    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setData(ArrayList<OngoingContestList> data) {
        mItems = new ArrayList<>();
        mItems.addAll(data);
        notifyDataSetChanged();

    }


    public class OngoingListViewHolder extends RecyclerView.ViewHolder {
        public ImageView imvSelected, imvOfContest, imvProductImage;
        public CardView cardItem;
        public ProgressBar pgrImage;
        public TextView tvEndDate;
        public CheckBox checkboxLike;

        public OngoingListViewHolder(View view) {

            super(view);
            cardItem = (CardView) view.findViewById(R.id.card_view);
            imvSelected = (ImageView) view.findViewById(R.id.imvSelected);
            imvOfContest = (ImageView) view.findViewById(R.id.imvOfContest);
            imvProductImage = (ImageView) view.findViewById(R.id.imvProductImage);
            pgrImage = (ProgressBar) view.findViewById(R.id.pgrImage);
            tvEndDate = (TextView) view.findViewById(R.id.tv_end_date);
            checkboxLike = (CheckBox) view.findViewById(R.id.checkboxLike);
        }


    }
}