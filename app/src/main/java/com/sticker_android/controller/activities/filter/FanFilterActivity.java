package com.sticker_android.controller.activities.filter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.util.DisplayMetrics;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sticker_android.R;
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.model.User;
import com.sticker_android.model.corporateproduct.Category;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.AppLogger;
import com.sticker_android.utils.sharedpref.AppPref;
import com.sticker_android.view.BottomSheetFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class FanFilterActivity extends AppBaseActivity {
    private ArrayList<Category> categoryArrayList = new ArrayList<>();
    private View contentView;
    private ImageView imageClose;
    private ImageView imvSave;
    private RadioButton chkMostDownload, chkRecentUpload;
    private ListView listFilter;
    com.sticker_android.view.BottomSheetFragment.IFilter iFilter;
    private ArrayAdapter<String> adapter;
    private RadioGroup radioGroup;
    private User mUserdata;
//    this.categoryArrayList = categoryArrayList;

    private CheckBox chkSelectAll;
    private AppPref appPref;
    private ArrayList<Category> categoryList = new ArrayList<>();
    private CustomListViewAdapter customListViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_filter);
        init();
        getuserInfo();
        setViewReferences();
        setViewListeners();
        fetchCategoryApi();
        setListViewAdaptor();
    }

    private void init() {
        appPref = new AppPref(getActivity());
    }

    private void getuserInfo() {
        mUserdata = appPref.getUserInfo();
    }

    @Override
    protected void setViewListeners() {
        imageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
            }
        });

        imvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<Integer> categoryArray = new ArrayList<>();
                AppLogger.debug("vfdjvnjf", "nvd,fv");

                for (Category category : categoryArrayList
                        ) {
                    if (category.isChecked) {
                        categoryArray.add(category.categoryId);
                    }
                }
                Gson gson = new Gson();
                String jsonNames = gson.toJson(categoryArray);
                AppLogger.debug("data is :", jsonNames.toString());

              /*  int selectedId = radioGroup.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                chkMostDownload = (RadioButton) contentView.findViewById(selectedId);
             */
                String filterDataName = "most_download";

                if (chkRecentUpload.isChecked()) {
                    filterDataName = "recent_upload";
                } else if (chkMostDownload.isChecked()) {
                    filterDataName = "most_download";
                }
                Intent intent=new Intent();
                intent.putExtra("categoryList")
               setResult(RESULT_OK,);

            }
        });
        listFilter.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });

        chkSelectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (buttonView.isPressed()) {
                    if (isChecked) {

                    }
                }
            }
        });
    }

    private void fetchCategoryApi() {

        Call<ApiResponse> apiResponseCall = RestClient.getService().apiCorporateCategoryList(mUserdata.getLanguageId(), mUserdata.getAuthrizedKey()
                , mUserdata.getId(), "corporate_category");

        apiResponseCall.enqueue(new ApiCall(getActivity()) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.status) {
                    categoryList = apiResponse.paylpad.corporateCategories;
                    customListViewAdapter.setData(categoryList);
                }

            }

            @Override
            public void onFail(Call<ApiResponse> call, Throwable t) {

            }
        });

    }

    private void setListViewAdaptor() {

        customListViewAdapter = new CustomListViewAdapter();
        listFilter.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        listFilter.setAdapter(customListViewAdapter);

    }

    @Override
    protected void setViewReferences() {
        imageClose = (ImageView) findViewById(R.id.imageClose);
        imvSave = (ImageView) findViewById(R.id.imvSave);
        chkMostDownload = (RadioButton) findViewById(R.id.chkMostDownload);
        chkRecentUpload = (RadioButton) findViewById(R.id.chkRecentUpload);
        listFilter = (ListView) findViewById(R.id.listFilter);
        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        chkSelectAll = (CheckBox) findViewById(R.id.chkSelectAll);
    }

    @Override
    protected boolean isValidData() {
        return false;
    }

    public class CustomListViewAdapter extends BaseAdapter {


        private final List<Category> items = new ArrayList<>();
        Context context;

        public CustomListViewAdapter() {
        }

        /*private view holder class*/
        private class ViewHolder {
            CheckBox checkBoxMultipleSelect;
            TextView txtTitle;

        }

        @Override
        public int getCount() {
            return categoryList.size();
        }

        @Override
        public Category getItem(int position) {

            return categoryList.get(position);
        }


        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            CustomListViewAdapter.ViewHolder holder = null;
            final Category rowItem = categoryList.get(position);

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_list_filter, null);
                holder = new CustomListViewAdapter.ViewHolder();
                holder.txtTitle = (TextView) convertView.findViewById(R.id.tvTitle);

                holder.checkBoxMultipleSelect = (CheckBox) convertView.findViewById(R.id.chkSelectItem);
                convertView.setTag(holder);
            } else
                holder = (CustomListViewAdapter.ViewHolder) convertView.getTag();

            // holder.checkBoxMultipleSelect.setText(rowItem.categoryName);
            holder.txtTitle.setText(rowItem.categoryName);


            final CustomListViewAdapter.ViewHolder finalHolder = holder;

            holder.checkBoxMultipleSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (buttonView.isPressed()) {
                        if (isChecked) {
                            rowItem.isChecked = true;
                        } else {
                            rowItem.isChecked = false;

                        }
                    }
                }
            });

            return convertView;
        }


        public void setData(ArrayList<Category> categoryArrayList) {

            if (categoryArrayList != null) {
                categoryList = categoryArrayList;
                notifyDataSetChanged();
            }
        }
    }
}
