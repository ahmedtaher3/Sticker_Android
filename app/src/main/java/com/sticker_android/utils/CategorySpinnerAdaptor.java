package com.sticker_android.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sticker_android.R;
import com.sticker_android.model.corporateproduct.CorporateCategory;

import java.util.List;

/**
 * Created by user on 10/4/18.
 */

public class CategorySpinnerAdaptor extends ArrayAdapter {

        private List<CorporateCategory> mStringDataList;
        private Context mContext;

        public CategorySpinnerAdaptor(Context context, List<CorporateCategory> stringList) {
            super(context, 0);
            this.mContext = context;
            this.mStringDataList = stringList;
            stringList.add(0,new CorporateCategory(-1,context.getString(R.string.select_category_txt)));



       /* if (isCountry) {
            stringList.add(0, context.getString(R.string.hint_country));
        } else {
            stringList.add(0, context.getString(R.string.hint_state));
        }*/
        }

        @Override
        public int getCount() {
            return mStringDataList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(mContext)
                        .inflate(R.layout.spinner_item, parent, false);
            }

            TextView txtName = (TextView) convertView.findViewById(R.id.tvSpinnerMainItem);
            if (position == 0)
                txtName.setHint(mStringDataList.get(position).categoryName);
            else
                txtName.setText(mStringDataList.get(position).categoryName);
            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext)
                        .inflate(R.layout.spinner_without_selection
                                , parent, false);
                holder = new ViewHolder();
                holder.mNameView = (TextView) convertView.findViewById(R.id.text1);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (position != 0) {
                holder.mNameView.setVisibility(View.VISIBLE);
                holder.mNameView.setText(mStringDataList.get(position).categoryName);
            } else {
                holder.mNameView.setVisibility(View.GONE);
            }
            return convertView;
        }

        private class ViewHolder {
            TextView mNameView;
        }

}
