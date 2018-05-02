package com.sticker_android.controller.adaptors;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.sticker_android.R;
import com.sticker_android.model.corporateproduct.Product;
import com.sticker_android.model.filter.FanFilter;
import com.sticker_android.utils.AppLogger;

import java.util.ArrayList;
import java.util.List;

public class GridViewAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater mLayoutInflater;
    ViewHolder viewHolder;
    private List<FanFilter> imageLists = new ArrayList<>();


    public interface OnFilterItemClickListener{
        void onItemClick(FanFilter product);
    }

    private OnFilterItemClickListener itemClickListener;

    public GridViewAdapter(Context context) {
        this.context = context;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    public void setOnItemClickListener(OnFilterItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }


    @Override
    public int getCount() {
        return imageLists.size();
    }

    @Override
    public FanFilter getItem(int position) {
        return imageLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.view_grid_album, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);

        } else
            viewHolder = (ViewHolder) convertView.getTag();
        final FanFilter fanFilter = imageLists.get(position);
        viewHolder.progressImage.setVisibility(View.VISIBLE);
        if (fanFilter.imageUrl != null && !fanFilter.imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(fanFilter.imageUrl)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            viewHolder.progressImage.setVisibility(View.INVISIBLE);

                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            viewHolder.progressImage.setVisibility(View.INVISIBLE);

                            return false;
                        }
                    })
                    .into(viewHolder.image);
        viewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(fanFilter);
            }
        });

        }
        return convertView;
    }


    protected class ViewHolder {
        public ImageView image;

        public ProgressBar progressImage;

        public ViewHolder(View view) {
            image = (ImageView) view.findViewById(R.id.image);
            progressImage = (ProgressBar) view.findViewById(R.id.progressImage);
        }
    }


    public void setData(ArrayList<FanFilter> data) {
        if (data != null) {
            imageLists.clear();
            imageLists.addAll(data);
            notifyDataSetChanged();
            AppLogger.debug("check called", "" + imageLists.size());
        }
    }
}