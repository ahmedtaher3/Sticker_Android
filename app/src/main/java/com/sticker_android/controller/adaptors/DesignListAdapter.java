package com.sticker_android.controller.adaptors;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.sticker_android.R;
import com.sticker_android.controller.fragment.corporate.ad.AdsFragment;
import com.sticker_android.model.corporateproduct.Product;
import com.sticker_android.utils.AppLogger;
import com.sticker_android.utils.Utils;

import java.util.ArrayList;

/**
 * Created by satyendra on 4/10/18.
 */

public class DesignListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String TAG = DesignListAdapter.class.getSimpleName();
    private ArrayList<Product> mItems;
    private Context context;
    private boolean isLoaderVisible;

    private final int ITEM_FOOTER = 0;
    private final int ITEM_PRODUCT = 1;

    public interface OnProductItemClickListener{
        void onProductItemClick(Product product);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imvOfAds;
        public TextView tvProductTitle, tvStatus, tvDesciption, tvTime;
        public CheckBox checkboxLike, checkboxShare;
        public ImageButton imvBtnEditRemove;
        public CardView cardItem;

        public ViewHolder(View view) {
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

    public class LoaderViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private ProgressBar progressBar;

        public LoaderViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.pbMore);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public DesignListAdapter(Context cnxt) {
        mItems = new ArrayList<>();
        context = cnxt;
    }

    public void setData(ArrayList<Product> data) {
        mItems = new ArrayList<>();
        mItems.addAll(data);
        notifyDataSetChanged();
        isLoaderVisible = false;
    }

    private void updateAdapterData(ArrayList<Product> data){
        mItems = new ArrayList<>();
        mItems.addAll(data);
    }

    private void addLoader() {
        AppLogger.error(TAG, "Add loader... in adapter");
        Product postItem = new Product();
        postItem.setProductid(-1);
        mItems.add(postItem);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                notifyItemInserted(mItems.size() - 1);
            }
        });
        isLoaderVisible = true;
    }

    private void removeLoader() {
        AppLogger.error(TAG, "Remove loader... from adapter");
        Product postItem = new Product();
        postItem.setProductid(-1);
        int index = mItems.indexOf(postItem);
        AppLogger.error(TAG, "Loader index => "+ index);
        if (index != -1) {
            mItems.remove(index);
            //notifyDataSetChanged();
            notifyItemRemoved(index);
            notifyItemRangeChanged(index, mItems.size());
            isLoaderVisible = false;
        }
    }

    private void removeProductData(int index){
        if (index != -1) {
            mItems.remove(index);
            notifyItemRemoved(index);
            notifyItemRangeChanged(index, mItems.size());
        }
    }

    private void removeProductData(Product post){
        int index = mItems.indexOf(post);
        if (index != -1) {
            mItems.remove(index);
            notifyItemRemoved(index);
            notifyItemRangeChanged(index, mItems.size());
        }
    }

    public void updateModifiedItem(Product postItem) {
        int index = mItems.indexOf(postItem);
        if (index != -1) {
            mItems.set(index, postItem);
            notifyDataSetChanged();
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == ITEM_FOOTER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_loader_view, parent, false);
            // set the view's size, margins, paddings and layout parameters
            final LoaderViewHolder vh = new LoaderViewHolder(v);
            return vh;
        } else {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rec_item_add_product, parent, false);
            // set the view's size, margins, paddings and layout parameters
            final ViewHolder vh = new ViewHolder(v);

            vh.cardItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = vh.getAdapterPosition();
                    Product product = mItems.get(position);
                    /*Intent intent = new Intent(mHostActivity, ViewProductActivity.class);
                    intent.putExtra(ViewProductActivity.VIEW_PAGER_SWIPE_ENABLED, false);
                    intent.putExtra(Constant.USER, product.userInfo);
                    intent.putExtra("post", post);
                    startActivityForResult(intent, VIEW_POST);*/
                }
            });
            return vh;
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final int itemType = getItemViewType(position);

        if (itemType == ITEM_FOOTER) {

        } else {
            ViewHolder itemHolder = (ViewHolder) holder;
            Product productItem = mItems.get(position);

            itemHolder.checkboxLike.setText(Utils.format(1000));
            itemHolder.checkboxShare.setText(Utils.format(1200));
            itemHolder.imvBtnEditRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopup(v, position, productItem);
                }
            });
            itemHolder.tvProductTitle.setText(Utils.capitlizeText(productItem.getProductname()));
            itemHolder.tvDesciption.setText(Utils.capitlizeText(productItem.getDescription()));
            itemHolder.tvTime.setText(timeUtility.covertTimeToText(Utils.convertToCurrentTimeZone(productItem.getCreatedTime()), getActivity()));

            String status = "Ongoing";
            if (productItem.getIsExpired() > 0) {
                itemHolder.tvStatus.setTextColor(Color.RED);
                status = "Expired";
            } else {
                itemHolder.tvStatus.setTextColor(getResources().getColor(R.color.colorHomeGreen));
            }
            itemHolder.tvStatus.setText(status);

            if(productItem.getImagePath()!=null&& !productItem.getImagePath().isEmpty())
                Glide.with(context)
                        .load(productItem.getImagePath())
                        .into(itemHolder.imvOfAds);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mItems.get(position).getProductid() == -1) {
            return ITEM_FOOTER;
        } else {
            return ITEM_PRODUCT;
        }
    }
}
