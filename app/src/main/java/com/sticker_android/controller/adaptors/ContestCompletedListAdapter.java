package com.sticker_android.controller.adaptors;

import android.content.Context;
import android.graphics.Color;
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
import com.sticker_android.model.contest.ContestCompleted;
import com.sticker_android.model.contest.OngoingContestList;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.helper.TimeUtility;

import java.util.ArrayList;

/**
 * Created by user on 17/4/18.
 */


public class ContestCompletedListAdapter extends RecyclerView.Adapter<ContestCompletedListAdapter.CompletedListViewHolder> {


    Context context;
    private ArrayList<ContestCompleted> mItems = new ArrayList<>();
    private TimeUtility timeUtility = new TimeUtility();

    public ContestCompletedListAdapter(Context context) {
        this.context = context;

    }

    @Override
    public CompletedListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return new CompletedListViewHolder(inflater.inflate(R.layout.contest_completed_list, parent, false));
    }

    @Override
    public void onBindViewHolder(final CompletedListViewHolder holder, int position) {

        final ContestCompleted listItem = mItems.get(position);

        if(listItem.isWinner>0) {
            holder.tvContestStatus.setText("Winner");
        holder.tvContestStatus.setTextColor(context.getResources().getColor(R.color.colorHomeGreen));
        }else {
            holder.tvContestStatus.setTextColor(Color.RED);
            holder.tvContestStatus.setText("Loose");
        }
        holder.totalNumberOfCount.setText(Utils.format(listItem.totalLike));
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

    public void setData(ArrayList<ContestCompleted> data) {
        mItems = new ArrayList<>();
        mItems.addAll(data);
        notifyDataSetChanged();

    }


    public class CompletedListViewHolder extends RecyclerView.ViewHolder {
        ImageView imvSelected, imvOfContest, imvProductImage;
        CardView cardItem;
        ProgressBar pgrImage;
        TextView totalNumberOfCount,tvContestStatus;

        public CompletedListViewHolder(View view) {

            super(view);
            cardItem           =     (CardView) itemView.findViewById(R.id.card_view);
            imvSelected        =     (ImageView) itemView.findViewById(R.id.imvSelected);
            imvOfContest       =     (ImageView) itemView.findViewById(R.id.imvOfContest);
            imvProductImage    =     (ImageView) itemView.findViewById(R.id.imvProductImage);
            pgrImage           =     (ProgressBar) itemView.findViewById(R.id.pgrImage);
            totalNumberOfCount =    (TextView) itemView.findViewById(R.id.tv_total_count_number);
            tvContestStatus    =    (TextView)itemView.findViewById(R.id.tv_contest_status);
        }


    }

}
