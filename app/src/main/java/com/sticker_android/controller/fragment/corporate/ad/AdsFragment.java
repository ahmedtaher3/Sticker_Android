package com.sticker_android.controller.fragment.corporate.ad;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.sticker_android.R;
import com.sticker_android.controller.fragment.base.BaseFragment;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdsFragment extends BaseFragment {


    private RecyclerView recAd;

    ArrayList<String>stringsDummy=new ArrayList<>();

    public AdsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_ads, container, false);
        setViewReferences(view);
        setViewListeners();
        recyclerViewLayout();
         addDummydata();
        setAdaptor();
        return  view;
    }

    private void setAdaptor() {
    AdRecAdaptor adRecAdaptor=new AdRecAdaptor();
        recAd.setAdapter(adRecAdaptor);

    }

    private void addDummydata() {
        for (int i = 0; i <20 ; i++) {

            stringsDummy.add("testData");
        }

    }


    private void recyclerViewLayout() {
        recAd.hasFixedSize();

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());

        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recAd.setLayoutManager(mLayoutManager);

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recAd.getContext(),
                mLayoutManager.getOrientation());
        recAd.addItemDecoration(mDividerItemDecoration);
    }
    @Override
    protected void setViewListeners() {

    }

    @Override
    protected void setViewReferences(View view) {

       recAd=(RecyclerView)view.findViewById(R.id.recAds);
    }

    @Override
    protected boolean isValidData() {
        return false;
    }

    public class AdRecAdaptor extends RecyclerView.Adapter<AdRecAdaptor.ViewHolder> {


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rec_item_add_product, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {

        }

        @Override
        public int getItemCount() {
            return stringsDummy.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {

           ImageView imvOfAds;
            TextView tvProductTitle,tvStatus,tvDesciption;
            public ViewHolder(View view) {
                super(view);
                imvOfAds=(ImageView) view.findViewById(R.id.imvOfAds);
                tvProductTitle=(TextView) view.findViewById(R.id.tv_add_product_title);
                tvStatus=(TextView) view.findViewById(R.id.tv_add_product_status);
                tvDesciption=(TextView) view.findViewById(R.id.tv_add_product_item_description);
                   }
        }
    }


}
