package com.sticker_android.controller.adaptors;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.sticker_android.R;
import com.sticker_android.model.ImageAlbum;
import com.sticker_android.model.User;
import com.sticker_android.model.filter.FanFilter;
import com.sticker_android.utils.AppLogger;
import com.sticker_android.utils.helper.TimeUtility;
import com.sticker_android.utils.sharedpref.AppPref;

import java.util.ArrayList;

public class FanCommonFilterList extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String TAG = FanCommonFilterList.class.getSimpleName();
    private  String type="";
    private ArrayList<FanFilter> mItems;
    private Context context;
    public boolean isLoaderVisible;

    private final int ITEM_FOOTER = 0;
    private final int ITEM_PRODUCT = 1;
    public static final String FILTER_IMAGE_TYPE = "filter_image_type";
    public static final String SELECTED_FILTER = "selected_filter";

    private TimeUtility timeUtility = new TimeUtility();
    AppPref appPref;

    User mUserdata;

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        ProgressBar progressImage;

        public ViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.image);
            progressImage = (ProgressBar) view.findViewById(R.id.progressImage);
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
    public FanCommonFilterList(Context cnxt,String type) {
        mItems = new ArrayList<>();
        context = cnxt;
        appPref = new AppPref(context);
        mUserdata = appPref.getUserInfo();
        this.type=type;
    }

    public void setData(ArrayList<FanFilter> data) {
        AppLogger.error(TAG, "else callled");
        AppLogger.error(TAG, "Data called... in adapter");
        if (data != null) {
            mItems = new ArrayList<>();
            mItems.addAll(data);
            notifyDataSetChanged();
            isLoaderVisible = false;
        }
    }

    public void updateAdapterData(ArrayList<FanFilter> data) {
        mItems = new ArrayList<>();
        mItems.addAll(data);
    }

    public void addLoader() {
        AppLogger.error(TAG, "Add loader... in adapter");
        FanFilter postItem = new FanFilter();
        postItem.dummyId = -1;
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
        FanFilter postItem = new FanFilter();
        postItem.dummyId = -1;
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

    public void removeProductData(ImageAlbum product) {
        int index = mItems.indexOf(product);
        if (index != -1) {
            mItems.remove(index);
            notifyItemRemoved(index);
            notifyItemRangeChanged(index, mItems.size());
        }
    }

    public void updateModifiedItem(FanFilter postItem) {
        int index = mItems.indexOf(postItem);
        if (index != -1) {
            mItems.set(index, postItem);
            notifyDataSetChanged();
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        AppLogger.error(TAG, "else callled on Bind viewholder");
        if (viewType == ITEM_FOOTER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_loader_view, parent, false);
            // set the view's size, margins, paddings and layout parameters
            final FanCommonFilterList.LoaderViewHolder vh = new FanCommonFilterList.LoaderViewHolder(v);
            return vh;
        } else {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_grid_item, parent, false);
            // set the view's size, margins, paddings and layout parameters
            final FanCommonFilterList.ViewHolder vh = new FanCommonFilterList.ViewHolder(v);

            /*vh.cardItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = vh.getAdapterPosition();
                    Product product = mItems.get(position);
                    if (productItemClickListener != null)
                        productItemClickListener.onProductItemClick(product);
                }
            });*/

            return vh;
        }
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        AppLogger.error(TAG, "else callled on Bind viewholder");
        final int itemType = getItemViewType(position);

        if (itemType == ITEM_FOOTER) {

        } else {
            final FanCommonFilterList.ViewHolder itemHolder = (FanCommonFilterList.ViewHolder) holder;
            // final FanFilter productItem = mItems.get(position);

            final FanFilter fanFilter = mItems.get(position);
            AppLogger.error(TAG, "else callled on Bind viewholder");
            itemHolder.progressImage.setVisibility(View.VISIBLE);
            if (fanFilter.imageUrl != null && !fanFilter.imageUrl.isEmpty()) {
                Glide.with(context)
                        .load(fanFilter.imageUrl)
                        .listener(new Request(itemHolder))
                        .into(itemHolder.image);
                itemHolder.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fanFilter.type=type;
                        Intent intent = new Intent();
                        intent.putExtra(SELECTED_FILTER, fanFilter);
                        ((Activity)context).setResult(Activity.RESULT_OK, intent);
                        ((Activity)context).finish();
                    }
                });

            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mItems.get(position).dummyId == -1) {
            return ITEM_FOOTER;
        } else {
            return ITEM_PRODUCT;
        }
    }
}
class Request implements RequestListener {

    private final FanCommonFilterList.ViewHolder viewHolder;

    public Request(FanCommonFilterList.ViewHolder viewHolder) {
        this.viewHolder = viewHolder;
    }

    @Override
    public boolean onException(Exception e, Object model, Target target, boolean isFirstResource) {
        viewHolder.progressImage.setVisibility(View.GONE);

        e.printStackTrace();
        return false;
    }

    @Override
    public boolean onResourceReady(Object resource, Object model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
        viewHolder.progressImage.setVisibility(View.GONE);


        return false;
    }
}

