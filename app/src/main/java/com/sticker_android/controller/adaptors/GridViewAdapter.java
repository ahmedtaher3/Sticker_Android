package com.sticker_android.controller.adaptors;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.sticker_android.R;
import com.sticker_android.model.filter.FanFilter;

import java.util.ArrayList;
import java.util.List;

public class GridViewAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater mLayoutInflater;
    ViewHolder viewHolder;
    private List<FanFilter> imageLists = new ArrayList<>();

    public GridViewAdapter(Context context) {
        this.context = context;
        this.mLayoutInflater = LayoutInflater.from(context);
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
        FanFilter fanFilter = imageLists.get(position);

        if (fanFilter.imageUrl != null && !fanFilter.imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(fanFilter.imageUrl)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {

                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {

                            return false;
                        }
                    })
                    .into(viewHolder.image);
        }
        return convertView;
    }


    protected class ViewHolder {
        public ImageView image;


        public ViewHolder(View view) {
            image = (ImageView) view.findViewById(R.id.image);
        }
    }


    public void setData(ArrayList<FanFilter> data) {
        if (data != null) {
            imageLists.clear();
            imageLists.addAll(imageLists);
            notifyDataSetChanged();
        }
    }
}