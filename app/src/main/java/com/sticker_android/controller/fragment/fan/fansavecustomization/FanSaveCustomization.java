package com.sticker_android.controller.fragment.fan.fansavecustomization;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.sticker_android.R;
import com.sticker_android.controller.activities.fan.home.fandownloadmage.FanDownloadedImageActivity;
import com.sticker_android.controller.fragment.base.BaseFragment;
import com.sticker_android.model.User;
import com.sticker_android.model.fandownload.Download;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.AppLogger;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.sharedpref.AppPref;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Created by user on 8/5/18.
 */

public class FanSaveCustomization extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private GridView gridView;
    private GridViewAdapter gridViewAdapter;
    private AppPref appPref;
    private User userdata;
    private Toolbar toolbar;
    private LinearLayout llNoDataFound;
    private SwipeRefreshLayout swipeRefresh;
    private RelativeLayout rlContent;
    private LinearLayout llLoaderView;
    private RelativeLayout rlConnectionContainer;
    private TextView txtNoDataFoundTitle, txtNoDataFoundContent;
    private int mCurrentPage = 0;
    private int PAGE_LIMIT;
    private RecyclerView recMyCustomization;
    private ItemListAdaptor itemListAdaptor;
    private LinearLayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.frag_fan_save_customization, container, false);
        init();
        getuserInfo();
        PAGE_LIMIT = getResources().getInteger(R.integer.designed_item_page_limit);
        setViewReferences(view);
        setViewListeners();
        recyclerViewLayout();
        //  setAdaptor();
        setRecAdaptor();
        getDownloadListApi(false);
        return view;
    }

    private void setRecAdaptor() {

        itemListAdaptor = new ItemListAdaptor(getActivity());
        recMyCustomization.setAdapter(itemListAdaptor);

    }

    private void init() {
        appPref = new AppPref(getActivity());
    }

    private void getuserInfo() {
        userdata = appPref.getUserInfo();
    }


    @Override
    protected void setViewListeners() {
        swipeRefresh.setOnRefreshListener(this);
    }


    @Override
    protected void setViewReferences(View view) {
        gridView = (GridView) view.findViewById(R.id.gridView);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        rlContent = (RelativeLayout) view.findViewById(R.id.rlContent);
        llNoDataFound = (LinearLayout) view.findViewById(R.id.llNoDataFound);
        txtNoDataFoundTitle = (TextView) view.findViewById(R.id.txtNoDataFoundTitle);
        txtNoDataFoundContent = (TextView) view.findViewById(R.id.txtNoDataFoundContent);
        rlConnectionContainer = (RelativeLayout) view.findViewById(R.id.rlConnectionContainer);
        llLoaderView = (LinearLayout) view.findViewById(R.id.llLoader);
        recMyCustomization = (RecyclerView) view.findViewById(R.id.recMyCustomization);

    }

    @Override
    protected boolean isValidData() {
        return false;
    }

    private void setAdaptor() {
        gridViewAdapter = new GridViewAdapter(getActivity());
        gridView.setAdapter(gridViewAdapter);
    }


    private void getDownloadListApi(final boolean isRefresh) {

        if (isRefresh) {
            swipeRefresh.setRefreshing(true);
        } else {
            llLoaderView.setVisibility(View.VISIBLE);
        }
        Call<ApiResponse> apiResponseCall = RestClient.getService().getCustomizeImageList(userdata.getLanguageId(), userdata.getAuthrizedKey(),
                userdata.getId(), 0, 1000, "download_list");

        apiResponseCall.enqueue(new ApiCall(getActivity()) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (isRefresh) {
                    swipeRefresh.setRefreshing(false);
                } else {
                    llLoaderView.setVisibility(View.GONE);
                }
                if (apiResponse.status) {
                    if (apiResponse.paylpad.fanFilterArrayList != null && apiResponse.paylpad.downloadArrayList.size() != 0) {
                        itemListAdaptor.setData(apiResponse.paylpad.downloadArrayList);
                        // gridViewAdapter.setData(apiResponse.paylpad.downloadArrayList);
                        itemListAdaptor.notifyDataSetChanged();

                        recMyCustomization.setVisibility(View.VISIBLE);
                        AppLogger.debug(FanSaveCustomization.class.getSimpleName(), "inside not null");
                    } else {
                        AppLogger.debug(FanSaveCustomization.class.getSimpleName(), "inside null");
                        showNoDataFound();
                        txtNoDataFoundContent.setText(R.string.txt_no_image_found);
                        gridView.setVisibility(View.GONE);
                        recMyCustomization.setVisibility(View.GONE);
                    }
                   /* if (apiResponse.paylpad.downloadArrayList == null) {
                        AppLogger.debug(FanSaveCustomization.class.getSimpleName(), "inside null");
                        txtNoDataFoundContent.setText("No Image Found.");
                        showNoDataFound();
                    }*/
                }

            }

            @Override
            public void onFail(Call<ApiResponse> call, Throwable t) {
                if (isRefresh) {
                    swipeRefresh.setRefreshing(false);
                } else {
                    llLoaderView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void showNoDataFound() {
        llNoDataFound.setVisibility(View.VISIBLE);
        txtNoDataFoundTitle.setText("");
    }

    @Override
    public void onRefresh() {
        if (Utils.isConnectedToInternet(getActivity())) {
            getDownloadListApi(true);
        } else {
            swipeRefresh.setRefreshing(false);
            Utils.showToastMessage(getActivity(), getString(R.string.pls_check_ur_internet_connection));
        }
    }


    public class GridViewAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater mLayoutInflater;
        GridViewAdapter.ViewHolder viewHolder;
        private List<Download> imageLists = new ArrayList<>();


        public GridViewAdapter(Context context) {
            this.context = context;
            this.mLayoutInflater = LayoutInflater.from(context);
        }


        @Override
        public int getCount() {
            return imageLists.size();
        }

        @Override
        public Download getItem(int position) {
            return imageLists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.view_grid_my_customization, null);
                viewHolder = new GridViewAdapter.ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else
                viewHolder = (GridViewAdapter.ViewHolder) convertView.getTag();
            final Download fanFilter = imageLists.get(position);
            viewHolder.progressImage.setVisibility(View.VISIBLE);
            if (fanFilter.imageUrl != null && !fanFilter.imageUrl.isEmpty()) {
                Glide.with(context)
                        .load(fanFilter.imageUrl)
                        .listener(new GridViewAdapter.Request(viewHolder))
                        .into(viewHolder.image);
                viewHolder.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), FanDownloadedImageActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("image", imageLists.get(position));
                        intent.putExtra("image", imageLists.get(position).imageUrl);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, 444);
                        getActivity().overridePendingTransition(R.anim.activity_animation_enter,
                                R.anim.activity_animation_exit);


                    }
                });

            }
            return convertView;
        }

        class Request implements RequestListener {

            private final GridViewAdapter.ViewHolder viewHolder;

            public Request(GridViewAdapter.ViewHolder viewHolder) {
                this.viewHolder = viewHolder;
            }

            @Override
            public boolean onException(Exception e, Object model, Target target, boolean isFirstResource) {
                viewHolder.progressImage.setVisibility(View.GONE);

                return false;
            }

            @Override
            public boolean onResourceReady(Object resource, Object model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
                viewHolder.progressImage.setVisibility(View.GONE);


                return false;
            }
        }

        protected class ViewHolder {
            public ImageView image;

            public ProgressBar progressImage;
            public RequestListener requestListener;

            public ViewHolder(View view) {
                image = (ImageView) view.findViewById(R.id.image);
                progressImage = (ProgressBar) view.findViewById(R.id.progressImage);
            }
        }


        public void setData(ArrayList<Download> data) {
            if (data != null) {
                imageLists.clear();
                imageLists.addAll(data);
                notifyDataSetChanged();
                AppLogger.debug("check called", "" + imageLists.size());
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode) {
                case 444:
                    if(itemListAdaptor!=null)
                        itemListAdaptor.notifyDataSetChanged();
                    onRefresh();
                    break;
            }
        }

    }

    /**
     * Method is used to set the layout on recycler view
     */
    private void recyclerViewLayout() {

        recMyCustomization.hasFixedSize();

        mLayoutManager = new LinearLayoutManager(getContext());

        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recMyCustomization.setLayoutManager(mLayoutManager);
    }
/*Recycler view*/


    public class ItemListAdaptor extends RecyclerView.Adapter<ItemListAdaptor.MyViewHolder> {


        private Context context;
        private LayoutInflater mLayoutInflater;
        MyViewHolder viewHolder;
        private List<Download> imageLists = new ArrayList<>();


        ItemListAdaptor(Context context) {
            this.context = context;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public ImageView image, imvOfCustomization, imvProductImage;

            public ProgressBar progressImage;
            public RequestListener requestListener;


            public MyViewHolder(View view) {
                super(view);
                image = (ImageView) view.findViewById(R.id.imvOfCustomization);
                progressImage = (ProgressBar) view.findViewById(R.id.pgrImage);
                imvProductImage = (ImageView) view.findViewById(R.id.imvProductImage);
            }
        }


        public void setData(List<Download> download) {
            if (download != null) {
                imageLists.clear();
                imageLists = download;
                AppLogger.debug(FanSaveCustomization.class.getSimpleName(), "FanSaveCustomization called" + download.size());
                notifyDataSetChanged();
            }
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_view_my_customization, parent, false);

            return new MyViewHolder(itemView);
        }

        class Request implements RequestListener {

            private final MyViewHolder viewHolder;

            public Request(MyViewHolder viewHolder) {
                this.viewHolder = viewHolder;
            }

            @Override
            public boolean onException(Exception e, Object model, Target target, boolean isFirstResource) {
                viewHolder.progressImage.setVisibility(View.GONE);
                viewHolder.imvProductImage.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Object resource, Object model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
                viewHolder.progressImage.setVisibility(View.GONE);
                viewHolder.imvProductImage.setVisibility(View.GONE);

                return false;
            }
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            AppLogger.debug(FanSaveCustomization.class.getSimpleName(), "FanSaveCustomization called");
            final Download fanFilter = imageLists.get(position);
            holder.progressImage.setVisibility(View.VISIBLE);
            if (fanFilter.imageUrl != null && !fanFilter.imageUrl.isEmpty()) {
                Glide.with(context)
                        .load(fanFilter.imageUrl)
                        .listener(new Request(holder))
                        .into(holder.image);
                holder.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), FanDownloadedImageActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("image", imageLists.get(position));
                        intent.putExtra("image", imageLists.get(position).imageUrl);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, 444);
                        getActivity().overridePendingTransition(R.anim.activity_animation_enter,
                                R.anim.activity_animation_exit);


                    }
                });
            }

        }

        @Override
        public int getItemCount() {
            return imageLists.size();
        }
    }

}
