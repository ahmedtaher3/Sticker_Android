package com.sticker_android.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sticker_android.R;
import com.sticker_android.model.corporateproduct.CorporateCategory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 6/4/18.
 */

public class CategoryAdapter extends ArrayAdapter<CorporateCategory> {

    private Context mContext;
    private List<CorporateCategory> categoryList = new ArrayList<>();

    public CategoryAdapter(@NonNull Context context, ArrayList<CorporateCategory> list) {
        super(context, 0, list);
        mContext = context;
        categoryList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.text_view_category, parent, false);
        TextView name = (TextView) listItem.findViewById(R.id.textView_name);
        name.setText(categoryList.get(position).categoryName);

        return listItem;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View row = View.inflate(mContext, R.layout.text_view_category, null);
        TextView name = (TextView) row.findViewById(R.id.textView_name);
        name.setText(categoryList.get(position).categoryName);

        return row;
    }
}