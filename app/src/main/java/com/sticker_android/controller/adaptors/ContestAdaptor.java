package com.sticker_android.controller.adaptors;

/**
 * Created by user on 12/4/18.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.sticker_android.R;

import java.util.ArrayList;

/**
 * Class is used for showing the contest adaptor
 */
public class ContestAdaptor extends RecyclerView.Adapter<ContestAdaptor.ContestViewHolder> {

    private final Context context;
    private ArrayList<String> notificationList = new ArrayList<>();

    public ContestAdaptor(Context context,ArrayList<String> notificationList) {
        this.notificationList = notificationList;
        this.context=context;
    }

    @Override
    public ContestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return new ContestViewHolder(inflater.inflate(R.layout.contest_item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(ContestViewHolder holder, int position) {

    }


    @Override
    public int getItemCount() {
        return notificationList.size();
    }


    class ContestViewHolder extends RecyclerView.ViewHolder {
        ImageView imvOfContest;
        ProgressBar pgrImage;
        ImageView imvProductImage;
        ImageView imvSelected;

        public ContestViewHolder(View view) {
            super(view);
            imvOfContest = (ImageView) view.findViewById(R.id.imvOfContest);
            imvProductImage = (ImageView) view.findViewById(R.id.imvProductImage);
            pgrImage = (ProgressBar) view.findViewById(R.id.pgrImage);
            imvSelected = (ImageView) view.findViewById(R.id.imvSelected);
        }
    }


}