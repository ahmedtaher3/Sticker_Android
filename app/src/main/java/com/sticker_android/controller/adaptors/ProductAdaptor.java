/*
package com.sticker_android.controller.adaptors;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.sticker_android.R;
import com.sticker_android.controller.fragment.corporate.ad.AdsFragment;
import com.sticker_android.model.corporateproduct.ProductList;
import com.sticker_android.utils.Utils;

import java.util.List;

*/
/**
 * Created by user on 5/4/18.
 *//*


public class ProductAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public final int TYPE_PRODUCT = 0;
        public final int TYPE_LOAD = 1;

        static Context context;
        List<ProductList> productLists;
        OnLoadMoreListener loadMoreListener;
        boolean isLoading = false, isMoreDataAvailable = true;

    */
/*
    * isLoading - to set the remote loading and complete status to fix back to back load more call
    * isMoreDataAvailable - to set whether more data from server available or not.
    * It will prevent useless load more request even after all the server data loaded
    * *//*



        public ProductAdaptor(Context context, List<ProductList> productLists) {
            this.context = context;
            this.productLists = productLists;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            if(viewType== TYPE_PRODUCT){
                return new ProductHolder(inflater.inflate(R.layout.rec_item_add_product,parent,false));
            }else{
                return new LoadHolder(inflater.inflate(R.layout.progress_item,parent,false));
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            if(position>=getItemCount()-1 && isMoreDataAvailable && !isLoading && loadMoreListener!=null){
                isLoading = true;
                loadMoreListener.onLoadMore();
            }

            if(getItemViewType(position)== TYPE_PRODUCT){
                final ProductList product = productLists.get(position);

                ((AdsFragment.AdsDataAdapter.AdsViewHolder) holder).checkboxLike.setText(Utils.format(1000));
                ((AdsFragment.AdsDataAdapter.AdsViewHolder) holder).checkboxShare.setText(Utils.format(1200));
                ((AdsFragment.AdsDataAdapter.AdsViewHolder) holder).imvBtnEditRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPopup(v, position,product);
                    }
                });
                ((ProductHolder) holder).tvProductTitle.setText(product.getProductname());
                ((ProductHolder) holder).tvDesciption.setText(product.getDescription());
                //   ((AdsViewHolder) holder).tvTime.setText(timeUtility.covertTimeToText(product.getExpireDate(), getActivity()));
                ((ProductHolder) holder).cardItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        moveToDetails(product);
                    }
                });
                String status="Ongoing";
                if(product.getIsExpired()>0){
                    ((AdsFragment.AdsDataAdapter.AdsViewHolder) holder).tvStatus.setTextColor(Color.RED);
                    status="Expired";
                }else{
                    ((AdsFragment.AdsDataAdapter.AdsViewHolder) holder).tvStatus.setTextColor(getResources().getColor(R.color.colorHomeGreen));

                }
                ((AdsFragment.AdsDataAdapter.AdsViewHolder) holder).tvStatus.setText(status);

            }
            //No else part needed as load holder doesn't bind any data
        }

        @Override
        public int getItemViewType(int position) {
            if(productLists.get(position).getType().equals("ads")||productLists.get(position).getType().equals("product")){
                return TYPE_PRODUCT;
            }else{
                return TYPE_LOAD;
            }
        }

        @Override
        public int getItemCount() {
            return productLists.size();
        }

    */
/* VIEW HOLDERS *//*


        static class ProductHolder extends RecyclerView.ViewHolder{
            public ImageView imvOfAds;
            public TextView tvProductTitle, tvStatus, tvDesciption, tvTime;
            public CheckBox checkboxLike, checkboxShare;
            public ImageButton imvBtnEditRemove;
            public CardView cardItem;

            public ProductHolder(View view) {
                super(view);
                imvOfAds = (ImageView) view.findViewById(R.id.imvOfAds);
                tvProductTitle = (TextView) view.findViewById(R.id.tv_add_product_title);
                tvStatus = (TextView) view.findViewById(R.id.tv_add_product_status);
                tvDesciption = (TextView) view.findViewById(R.id.tv_add_product_item_description);
                checkboxLike = (CheckBox) view.findViewById(R.id.checkboxLike);
                checkboxShare = (CheckBox) view.findViewById(R.id.checkboxShare);
                imvBtnEditRemove = (ImageButton) view.findViewById(R.id.imvBtnEditRemove);
                tvTime = (TextView) view.findViewById(R.id.tvTime);
                cardItem = (CardView) view.findViewById(R.id.card_view);
            }
        }

        static class LoadHolder extends RecyclerView.ViewHolder{
            public LoadHolder(View itemView) {
                super(itemView);
            }
        }

        public void setMoreDataAvailable(boolean moreDataAvailable) {
            isMoreDataAvailable = moreDataAvailable;
        }

        */
/* notifyDataSetChanged is final method so we can't override it
             call adapter.notifyDataChanged(); after update the list
             *//*

        public void notifyDataChanged(){
            notifyDataSetChanged();
            isLoading = false;
        }


        interface OnLoadMoreListener{
            void onLoadMore();
        }

        public void setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
            this.loadMoreListener = loadMoreListener;
        }
    }

*/
