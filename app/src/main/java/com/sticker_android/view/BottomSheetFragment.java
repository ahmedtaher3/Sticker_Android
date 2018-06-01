package com.sticker_android.view;

/**
 * Created by user on 26/4/18.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sticker_android.R;
import com.sticker_android.model.corporateproduct.Category;
import com.sticker_android.utils.AppLogger;

import java.util.ArrayList;
import java.util.List;

public class BottomSheetFragment extends BottomSheetDialogFragment {

    private Context context = null;
    private ArrayList<Category> categoryArrayList = new ArrayList<>();
    private View contentView;
    private ImageView imageClose;
    private ImageView imvSave;
    private RadioButton chkMostDownload, chkRecentUpload;
    private ListView listFilter;
    IFilter iFilter;
    private ArrayAdapter<String> adapter;
    private RadioGroup radioGroup;
    private CheckBox chkSelectAll;

    public BottomSheetFragment(){}

    public BottomSheetFragment(ArrayList<Category> categoryArrayList, IFilter iFilter, Context context) {
        // Required empty public constructor

        this.categoryArrayList = categoryArrayList;
        this.iFilter = iFilter;
        this.context = context;
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

        }
    };

    /*@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.view_filter, container, false);
    }
*/

    @Override
    public void setupDialog(Dialog dialog, int style) {

        super.setupDialog(dialog, style);
        contentView = View.inflate(getActivity(), R.layout.view_filter, null);
        dialog.setContentView(contentView);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }

        View parent = (View) contentView.getParent();
        parent.setFitsSystemWindows(true);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(parent);
        contentView.measure(0, 0);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenHeight = displaymetrics.heightPixels;
        bottomSheetBehavior.setPeekHeight(screenHeight);

        if (params.getBehavior() instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) params.getBehavior()).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }

        params.height = screenHeight;
        parent.setLayoutParams(params);
        references();
        setListener();
        setListViewAdaptor();
    }

    private void setListViewAdaptor() {

        CustomListViewAdapter customListViewAdapter = new CustomListViewAdapter(context, R.layout.item_list_filter, categoryArrayList);
        listFilter.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        listFilter.setAdapter(customListViewAdapter);

    }

    private void setListener() {

        imageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
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
                AppLogger.debug("data is :",jsonNames.toString());

              /*  int selectedId = radioGroup.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                chkMostDownload = (RadioButton) contentView.findViewById(selectedId);
             */   String filterDataName="most_download";

                if(chkRecentUpload.isChecked()){
                    filterDataName="recent_upload";
                }else if(chkMostDownload.isChecked()){
                    filterDataName="most_download";
                }
                iFilter.selectedCategory(jsonNames,filterDataName);
                dismiss();
               /* SparseBooleanArray checked = listFilter.getCheckedItemPositions();

                ArrayList<String> selectedItems = new ArrayList<String>();
                for (int i = 0; i < checked.size(); i++) {
                    // Item position in adapter
                    int position2 = checked.keyAt(i);
                    // Add sport if it is checked i.e.) == TRUE!
                    if (checked.valueAt(i)){
                        selectedItems.add(adapter.getItem(position2));
                        AppLogger.debug("vfdjvnjf", "nvd,fv" +adapter.getItem(position2));

                    }
                }

                String[] outputStrArr = new String[selectedItems.size()];

                for (int i = 0; i < selectedItems.size(); i++) {
                    outputStrArr[i] = selectedItems.get(i);
                    AppLogger.debug("vfdjvnjf", "nvd,fv" + outputStrArr[i]);

                }
*/                // iFilter.selectedCategory();
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

                if(buttonView.isPressed()){
                    if(isChecked){

                    }
                }
            }
        });
    }


    public void references() {


        imageClose = (ImageView) contentView.findViewById(R.id.imageClose);
        imvSave = (ImageView) contentView.findViewById(R.id.imvSave);
        chkMostDownload = (RadioButton) contentView.findViewById(R.id.chkMostDownload);
        chkRecentUpload = (RadioButton) contentView.findViewById(R.id.chkRecentUpload);
        listFilter = (ListView) contentView.findViewById(R.id.listFilter);
        radioGroup = (RadioGroup) contentView.findViewById(R.id.radio_group);
        chkSelectAll = (CheckBox) contentView.findViewById(R.id.chkSelectAll);
    }


    public interface IFilter {
        void selectedCategory(String categories,String selectedData);
    }


    public class CustomListViewAdapter extends BaseAdapter {


        private final List<Category> items = new ArrayList<>();
        Context context;

        public CustomListViewAdapter(Context context, int resourceId, //resourceId=your layout
                                     List<Category> items) {
            this.context = context;
            categoryArrayList = (ArrayList<Category>) items;
        }

        /*private view holder class*/
        private class ViewHolder {
            CheckBox checkBoxMultipleSelect;
            TextView txtTitle;

        }

        @Override
        public int getCount() {
            return categoryArrayList.size();
        }

        @Override
        public Category getItem(int position) {

            return categoryArrayList.get(position);
        }


        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            final Category rowItem = categoryArrayList.get(position);

            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_list_filter, null);
                holder = new ViewHolder();
                holder.txtTitle = (TextView) convertView.findViewById(R.id.tvTitle);

                holder.checkBoxMultipleSelect = (CheckBox) convertView.findViewById(R.id.chkSelectItem);
                convertView.setTag(holder);
            } else
                holder = (ViewHolder) convertView.getTag();

           // holder.checkBoxMultipleSelect.setText(rowItem.categoryName);
            holder.txtTitle.setText(rowItem.categoryName);


            final ViewHolder finalHolder = holder;

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


    }
}