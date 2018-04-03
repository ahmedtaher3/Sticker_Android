package com.sticker_android.controller.fragment.corporate.product;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sticker_android.R;
import com.sticker_android.controller.activities.corporate.RenewAdandProductActivity;
import com.sticker_android.controller.fragment.base.BaseFragment;
import com.sticker_android.model.User;
import com.sticker_android.model.interfaces.OnLoadMoreListener;
import com.sticker_android.utils.Utils;
import com.sticker_android.view.OnVerticalScrollListener;

import java.util.ArrayList;
import java.util.List;


public class ProductsFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {


    private RecyclerView recAd;
    private ProgressBar progressBarLoadMore;
    ArrayList<String> stringsDummy = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayoutManager mLayoutManager;
    private OnVerticalScrollListener scrollListener;
    private boolean loading = true;
    private boolean isLastPage = true;
    private AdsDataAdapter mAdapter;
    private List<User> userList = new ArrayList<>();
    protected Handler handler;
    public ProductsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_products, container, false);
        setViewReferences(view);
        setViewListeners();
        recyclerViewLayout();
        loadData();
        setAdaptor();
        handler = new Handler();
        adaptorScrollListener();
        return view;
    }

    private void adaptorScrollListener() {

        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                //add null , so the adapter will check view_type and show progress bar at bottom
                userList.add(null);
                progressBarLoadMore.setVisibility(View.VISIBLE);
                mAdapter.notifyItemInserted(userList.size() - 1);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressBarLoadMore.setVisibility(View.GONE);
                        //   remove progress item
                        userList.remove(userList.size() - 1);
                        mAdapter.notifyItemRemoved(userList.size());
                        //add items one by one
                        int start = userList.size();
                        int end = start + 20;

                        for (int i = start + 1; i <= end; i++) {
                            userList.add(new User());
                            mAdapter.notifyItemInserted(userList.size());
                        }
                        mAdapter.setLoaded();
                        //or you can add all at once but do not forget to call mAdapter.notifyDataSetChanged();
                    }
                }, 2000);

            }
        });
    }

    private void setAdaptor() {
        mAdapter = new AdsDataAdapter(userList, recAd);
        // set the adapter object to the Recyclerview
        recAd.setAdapter(mAdapter);
    }

    // load initial data
    private void loadData() {

        for (int i = 1; i <= 20; i++) {
            userList.add(new User());

        }


    }

    /**
     * Method is used to set the layout on recycler view
     */
    private void recyclerViewLayout() {
        recAd.hasFixedSize();

        mLayoutManager = new LinearLayoutManager(getContext());

        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recAd.setLayoutManager(mLayoutManager);
    }

    @Override
    protected void setViewListeners() {

        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    protected void setViewReferences(View view) {

        recAd = (RecyclerView) view.findViewById(R.id.recAds);
        progressBarLoadMore = (ProgressBar) view.findViewById(R.id.progressBarLoadMore);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshAds);
    }

    @Override
    protected boolean isValidData() {
        return false;
    }

    @Override
    public void onRefresh() {

        swipeRefreshLayout.setRefreshing(false);

    }

    /**
     * Method is used to show the popup with edit and delete option
     *
     * @param v        view on which click is perfomed
     * @param position position of item
     */
    public void showPopup(View v, int position) {
        PopupMenu popup = new PopupMenu(getActivity(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.edit_remove_product, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Utils.hideKeyboard(getActivity());
                switch (item.getItemId()) {
                    case R.id.edit:
                        startActivity(new Intent(getActivity(), RenewAdandProductActivity.class));
                        getActivity().overridePendingTransition(R.anim.activity_animation_enter,
                                R.anim.activity_animation_exit);
                        break;
                    case R.id.remove:
                        break;
                }
                return false;
            }
        });
    }

    public class AdsDataAdapter extends RecyclerView.Adapter {

        private List<User> userList;

        // The minimum amount of items to have below your current scroll position
        // before loading more.
        private int visibleThreshold = 5;
        private int lastVisibleItem, totalItemCount;
        private boolean loading;
        private OnLoadMoreListener onLoadMoreListener;


        public AdsDataAdapter(List<User> user, RecyclerView recyclerView) {
            userList = user;

            if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

                final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                        .getLayoutManager();


                recyclerView
                        .addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrolled(RecyclerView recyclerView,
                                                   int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);

                                totalItemCount = linearLayoutManager.getItemCount();
                                lastVisibleItem = linearLayoutManager
                                        .findLastVisibleItemPosition();
                                if (!loading
                                        && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                    // End has been reached
                                    // Do something
                                    progressBarLoadMore.setVisibility(View.VISIBLE);
                                    if (onLoadMoreListener != null) {
                                        onLoadMoreListener.onLoadMore();
                                    }
                                    loading = true;
                                }
                            }
                        });
            }
        }

        /*  @Override
          public int getItemViewType(int position) {
              return userList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
          }
  */
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
            RecyclerView.ViewHolder vh;
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.rec_item_add_product, parent, false);

            vh = new AdsDataAdapter.StudentViewHolder(v);

            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            ((AdsDataAdapter.StudentViewHolder) holder).checkboxLike.setText(Utils.format(2222222));
            ((AdsDataAdapter.StudentViewHolder) holder).checkboxShare.setText(Utils.format(1000000000));
            ((AdsDataAdapter.StudentViewHolder) holder).imvBtnEditRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopup(v, position);
                }
            });
        }

        public void setLoaded() {
            loading = false;
        }

        @Override
        public int getItemCount() {
            return userList.size();
        }

        public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
            this.onLoadMoreListener = onLoadMoreListener;
        }

        /**
         * Method is used to notify the list
         */
        public void notifyAdaptor() {

            mAdapter.notifyDataSetChanged();
        }


        //
        public class StudentViewHolder extends RecyclerView.ViewHolder {
            public ImageView imvOfAds;
            public TextView tvProductTitle, tvStatus, tvDesciption;
            public CheckBox checkboxLike, checkboxShare;
            public ImageButton imvBtnEditRemove;

            public StudentViewHolder(View view) {
                super(view);
                imvOfAds = (ImageView) view.findViewById(R.id.imvOfAds);
                tvProductTitle = (TextView) view.findViewById(R.id.tv_add_product_title);
                tvStatus = (TextView) view.findViewById(R.id.tv_add_product_status);
                tvDesciption = (TextView) view.findViewById(R.id.tv_add_product_item_description);
                checkboxLike = (CheckBox) view.findViewById(R.id.checkboxLike);
                checkboxShare = (CheckBox) view.findViewById(R.id.checkboxShare);
                imvBtnEditRemove = (ImageButton) view.findViewById(R.id.imvBtnEditRemove);

            }
        }

    }
}
