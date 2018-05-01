package com.sticker_android.controller.adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zippycity.app.R;
import com.zippycity.app.model.categories.servicelist.ServicesList;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by user on 21/7/17.
 */

public class GridViewAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater mLayoutInflater;
    ViewHolder viewHolder;
    private List<String> imageLists = new ArrayList<>();

    private ArrayList<String> searchList;

    public GridViewAdapter(Context context) {
        this.context = context;
        this.mLayoutInflater = LayoutInflater.from(context);
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.view_grid_album, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);

        } else
            viewHolder = (ViewHolder) convertView.getTag();

       // Picasso.with(context).load(imageLists.get(position).serviceImage).into(viewHolder.card_Image);
       // viewHolder.card_item.setText(String.valueOf(imageLists.get(position).serviceName));
        return convertView;

    }

    protected class ViewHolder {
        private ImageView image;


        public ViewHolder(View view) {
            image = (ImageView) view.findViewById(R.id.image);
        }
    }


    @Override
    public int getCount() {
        return imageLists.size();
    }

    @Override
    public Object getItem(int position) {
        return imageLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

   public  void setData(ArrayList<String> data){
       if(data!=null){
           imageLists.clear();
           imageLists.addAll(imageLists);
           notifyDataSetChanged();
       }
   }
}
