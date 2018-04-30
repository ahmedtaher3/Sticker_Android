package com.sticker_android.controller.adaptors;

import android.content.Context;
import android.os.Handler;
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
import com.sticker_android.model.corporateproduct.Product;
import com.sticker_android.model.interfaces.DesignerActionListener;
import com.sticker_android.utils.AppLogger;
import com.sticker_android.utils.helper.TimeUtility;

import java.util.ArrayList;

/**
 * Created by user on 17/4/18.
 */


public class CorporateContestListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String TAG = com.sticker_android.controller.adaptors.DesignListAdapter.class.getSimpleName();
    private ArrayList<Product> mItems;
    private Context context;
    public boolean isLoaderVisible;

    private final int ITEM_FOOTER = 0;
    private final int ITEM_PRODUCT = 1;
    private int selectedPosition = -1;

    private TimeUtility timeUtility = new TimeUtility();
    private OnProductItemClickListener productItemClickListener;
    private DesignerActionListener designerActionListener;

    public interface OnProductItemClickListener {
        void onProductItemClick(Product product);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imvSelected, imvOfContest, imvProductImage;
        CardView cardItem;
        ProgressBar pgrImage;
        TextView tvFeatured;

        public ViewHolder(View view) {
            super(view);
            cardItem = (CardView) itemView.findViewById(R.id.card_view);
            imvSelected = (ImageView) itemView.findViewById(R.id.imvSelected);
            imvOfContest = (ImageView) itemView.findViewById(R.id.imvOfContest);
            imvProductImage = (ImageView) itemView.findViewById(R.id.imvProductImage);
            pgrImage = (ProgressBar) itemView.findViewById(R.id.pgrImage);
            tvFeatured = (TextView) itemView.findViewById(R.id.tvFeatured);
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
    public CorporateContestListAdapter(Context cnxt) {
        mItems = new ArrayList<>();
        context = cnxt;
    }

    public void setDesignerActionListener(DesignerActionListener actionListener) {
        this.designerActionListener = actionListener;
    }

    public void setProductItemClickListener(OnProductItemClickListener actionListener) {
        this.productItemClickListener = actionListener;
    }

    public void setData(ArrayList<Product> data) {
        mItems = new ArrayList<>();
        mItems.addAll(data);
        notifyDataSetChanged();
        isLoaderVisible = false;
    }

    public void updateAdapterData(ArrayList<Product> data) {
        mItems = new ArrayList<>();
        mItems.addAll(data);
    }

    public void addLoader() {
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

    public void removeLoader() {
        AppLogger.error(TAG, "Remove loader... from adapter");
        Product postItem = new Product();
        postItem.setProductid(-1);
        int index = mItems.indexOf(postItem);
        AppLogger.error(TAG, "Loader index => " + index);
        if (index != -1) {
            mItems.remove(index);
            //notifyDataSetChanged();
            notifyItemRemoved(index);
            notifyItemRangeChanged(index, mItems.size());
            isLoaderVisible = false;
        }
    }

    public void removeProductData(int index) {
        if (index != -1) {
            mItems.remove(index);
            notifyItemRemoved(index);
            notifyItemRangeChanged(index, mItems.size());
        }
    }

    public void removeProductData(Product product) {
        int index = mItems.indexOf(product);
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
            final CorporateContestListAdapter.LoaderViewHolder vh = new CorporateContestListAdapter.LoaderViewHolder(v);
            return vh;
        } else {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contest_item_view, parent, false);
            // set the view's size, margins, paddings and layout parameters
            final CorporateContestListAdapter.ViewHolder vh = new CorporateContestListAdapter.ViewHolder(v);


            return vh;
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final int itemType = getItemViewType(position);

        if (itemType == ITEM_FOOTER) {

        } else {
            final ViewHolder itemHolder = (ViewHolder) holder;
            final Product productItem = mItems.get(position);
            if (selectedPosition == position) {

                ((ViewHolder) holder).imvSelected.setVisibility(View.VISIBLE);


                ((ViewHolder) holder).imvSelected.setImageResource(R.drawable.right_tick_hdpi);
            } else {

                ((ViewHolder) holder).imvSelected.setVisibility(View.GONE);
            }
            if (productItem.isFeatured > 0)
                ((ViewHolder) holder).tvFeatured.setVisibility(View.VISIBLE);
            else
                ((ViewHolder) holder).tvFeatured.setVisibility(View.GONE);
            Glide.with(context)
                    .load(productItem.getImagePath()).fitCenter()
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            ((ViewHolder) holder).pgrImage.setVisibility(View.GONE);
                            ((ViewHolder) holder).imvProductImage.setVisibility(View.GONE);

                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            ((ViewHolder) holder).pgrImage.setVisibility(View.GONE);
                            ((ViewHolder) holder).imvProductImage.setVisibility(View.GONE);

                            return false;
                        }
                    })
                    .into(((ViewHolder) holder).imvOfContest);

            ((ViewHolder) holder).cardItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedPosition = holder.getAdapterPosition();
                    productItemClickListener.onProductItemClick(productItem);
                    notifyDataSetChanged();
                }
            });

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
