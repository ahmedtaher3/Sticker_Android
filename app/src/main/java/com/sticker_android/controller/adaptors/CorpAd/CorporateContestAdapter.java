package com.sticker_android.controller.adaptors.CorpAd;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.sticker_android.R;
import com.sticker_android.model.corporateproduct.Product;
import com.sticker_android.utils.AppLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Suleiman on 19/10/16.
 */

public class CorporateContestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // View Types
    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private static final int HERO = 2;

    private List<Product> productList;
    private Context context;

    private boolean isLoadingAdded = false;
    private boolean retryPageLoad = false;


    private OnProductItemClickListener productItemClickListener;
    private String errorMsg;
    private int selectedPosition = -1;
    public CorporateContestAdapter(Context context) {
        this.context = context;
        productList = new ArrayList<>();
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProduct(List<Product> productList) {
        this.productList = productList;
    }



    public void setOnProductClickListener(OnProductItemClickListener productClickListener) {
        this.productItemClickListener = productClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                View viewItem = inflater.inflate(R.layout.contest_item_view, parent, false);
                viewHolder = new ProductVH(viewItem);
                break;
            case LOADING:
                View viewLoading = inflater.inflate(R.layout.progress_item, parent, false);
                viewHolder = new LoadingVH(viewLoading);
                break;

        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final Product product = productList.get(position); // Movie

        switch (getItemViewType(position)) {

            case ITEM:
                final ProductVH productVH = (ProductVH) holder;
                if(selectedPosition == position){
                    ((ProductVH) holder).imvSelected.setVisibility(View.VISIBLE);

                    ((ProductVH) holder).imvSelected.setImageResource(R.drawable.ic_share);
                }else {

                    ((ProductVH) holder).imvSelected.setVisibility(View.GONE);
                }
                Glide.with(context)
                        .load(product.getImagePath()).fitCenter()
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                ((ProductVH) holder).pgrImage.setVisibility(View.GONE);
                                ((ProductVH) holder).imvProductImage.setVisibility(View.GONE);

                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                ((ProductVH) holder).pgrImage.setVisibility(View.GONE);
                                ((ProductVH) holder).imvProductImage.setVisibility(View.GONE);

                                return false;
                            }
                        })
                        .into(((ProductVH) holder).imvOfContest);
                ((ProductVH) holder).cardItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedPosition = holder.getAdapterPosition();
                        notifyDataSetChanged();
                        AppLogger.debug("Corporate contest adapter",""+selectedPosition);
                        productItemClickListener.onProductItemClick(product);
                    }
                });
                break;

            case LOADING:
                LoadingVH loadingVH = (LoadingVH) holder;
                if (retryPageLoad)
                    loadingVH.mProgressBar.setVisibility(View.GONE);
                else
                    loadingVH.mProgressBar.setVisibility(View.VISIBLE);
           /*    if (retryPageLoad) {
                    loadingVH.mErrorLayout.setVisibility(View.VISIBLE);
                    loadingVH.mProgressBar.setVisibility(View.GONE);

                    loadingVH.mErrorTxt.setText(
                            errorMsg != null ?
                                    errorMsg :
                                    context.getString(R.string.no_ads_uploaded_yet));

                } else {
                    loadingVH.mErrorLayout.setVisibility(View.GONE);
                    loadingVH.mProgressBar.setVisibility(View.VISIBLE);
                }*/
                break;
        }
    }

    @Override
    public int getItemCount() {
        return productList == null ? 0 : productList.size();
    }

    @Override
    public int getItemViewType(int position) {

        return (position == productList.size() - 1 && isLoadingAdded) ? LOADING : ITEM;

    }


    public void add(Product r) {
        productList.add(r);
        notifyItemInserted(productList.size() - 1);
    }

    public void addAll(List<Product> moveResults) {
        for (Product result : moveResults) {
            add(result);
        }
        notifyDataSetChanged();
    }

    public void remove(Product r) {
        int position = productList.indexOf(r);
        if (position > -1) {
            productList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Product());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = productList.size() - 1;
        Product result = getItem(position);

        if (result != null) {
            productList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Product getItem(int position) {
        return productList.get(position);
    }

    /**
     * Displays Pagination retry footer view along with appropriate errorMsg
     *
     * @param show
     * @param errorMsg to display if page load fails
     */
    public void showRetry(boolean show, @Nullable String errorMsg) {
        retryPageLoad = show;
        notifyItemChanged(productList.size() - 1);

        if (errorMsg != null) this.errorMsg = errorMsg;
    }


    /**
     * Main list's content ViewHolder
     */
    protected class ProductVH extends RecyclerView.ViewHolder {
        ImageView imvSelected, imvOfContest, imvProductImage;
        CardView cardItem;
        ProgressBar pgrImage;

        public ProductVH(View itemView) {
            super(itemView);
            cardItem = (CardView) itemView.findViewById(R.id.card_view);
            imvSelected = (ImageView) itemView.findViewById(R.id.imvSelected);
            imvOfContest = (ImageView) itemView.findViewById(R.id.imvOfContest);
            imvProductImage = (ImageView) itemView.findViewById(R.id.imvProductImage);
            pgrImage = (ProgressBar) itemView.findViewById(R.id.pgrImage);
        }
    }


    protected class LoadingVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ProgressBar mProgressBar;


        public LoadingVH(View itemView) {
            super(itemView);

            mProgressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.loadmore_retry:
                case R.id.loadmore_errorlayout:

                    showRetry(false, null);
                    break;
            }
        }
    }

    public interface OnProductItemClickListener {
        void onProductItemClick(Product product);
    }
}
