package com.sticker_android.controller.adaptors;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.sticker_android.R;
import com.sticker_android.model.contest.OngoingContestList;

import java.util.ArrayList;

/**
 * Created by user on 18/4/18.
 */

public class CorporateContentApproval extends RecyclerView.Adapter<CorporateContentApproval.ContentViewHolder> {

        Context context;
        private ArrayList<OngoingContestList> mItems = new ArrayList<>();

        public CorporateContentApproval(Context context) {
            this.context = context;

        }

        @Override
        public ContentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            return new ContentViewHolder(inflater.inflate(R.layout.rec_item_content_approval, parent, false));
        }

        @Override
        public void onBindViewHolder(final ContentViewHolder holder, int position) {

            final OngoingContestList listItem = mItems.get(position);


            /*

            holder.tvStatus.setText("Pending");
            if (listItem.productList.getImagePath() != null && !listItem.productList.getImagePath().isEmpty())
                Glide.with(context)
                        .load(listItem.productList.getImagePath()).fitCenter()
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                holder.pgrImage.setVisibility(View.GONE);

                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                holder.pgrImage.setVisibility(View.GONE);

                                return false;
                            }
                        })
                        .into(holder.imvContainer);

*/

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


        public class ContentViewHolder extends RecyclerView.ViewHolder {
            public ImageView imvContainer;
            public CardView cardItem;
            public ProgressBar pgrImage;
            public TextView tvStatus;

            public ContentViewHolder(View view) {

                super(view);
                cardItem = (CardView) view.findViewById(R.id.card_view);
                imvContainer = (ImageView) view.findViewById(R.id.imvContainer);
                pgrImage = (ProgressBar) view.findViewById(R.id.pgrImage);
                tvStatus = (TextView) view.findViewById(R.id.tv_add_product_status);

            }



}}
