package com.sticker_android.controller.fragment.designer.contentapproval;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sticker_android.R;
import com.sticker_android.controller.adaptors.CorporateContentApproval;
import com.sticker_android.controller.fragment.base.BaseFragment;
import com.sticker_android.controller.fragment.corporate.contentapproval.CorporateContentApprovalAdsFragment;
import com.sticker_android.model.User;
import com.sticker_android.model.contest.OngoingContest;
import com.sticker_android.model.corporateproduct.Product;
import com.sticker_android.utils.sharedpref.AppPref;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class DesignerContentApprGifFragment extends BaseFragment {

    private RecyclerView recContentApproval;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout llNoDataFound;
    private AppPref appPref;
    private User mUserdata;
    private TextView tvNoAdsUploaded;
    private LinearLayoutManager mLayoutManager;
    private Context mContext;
    private CorporateContentApproval mAdapter;
    private RelativeLayout rlContent;
    private LinearLayout llLoaderView;
    private RelativeLayout rlConnectionContainer;
    private TextView txtNoDataFoundTitle, txtNoDataFoundContent;
    private ArrayList<Product> mProductList;
    private static final String TAG = CorporateContentApprovalAdsFragment.class.getSimpleName();
    private View view;


    public DesignerContentApprGifFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_content_approval, container, false);
        init();
        getuserInfo();
        setViewReferences(view);
        setViewListeners();
        mAdapter = new CorporateContentApproval(getActivity());
        llNoDataFound.setVisibility(View.GONE);
        mProductList = new ArrayList<>();
        getContentApi();
        recContentApproval.setAdapter(mAdapter);
        recyclerViewLayout();
        return view;
    }

    private void getContentApi() {

        mProductList.add(new Product());
        mAdapter.setData(mProductList);
    }

    private void init() {

        appPref = new AppPref(getActivity());
    }

    private void getuserInfo() {
        mUserdata = appPref.getUserInfo();
    }

    @Override
    protected void setViewListeners() {

    }

    @Override
    protected void setViewReferences(View view) {
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        recContentApproval = (RecyclerView) view.findViewById(R.id.recContentApproval);
        tvNoAdsUploaded = (TextView) view.findViewById(R.id.tvNoAdsUploaded);
        rlContent = (RelativeLayout) view.findViewById(R.id.rlContent);
        llNoDataFound = (LinearLayout) view.findViewById(R.id.llNoDataFound);
        txtNoDataFoundTitle = (TextView) view.findViewById(R.id.txtNoDataFoundTitle);
        txtNoDataFoundContent = (TextView) view.findViewById(R.id.txtNoDataFoundContent);
        rlConnectionContainer = (RelativeLayout) view.findViewById(R.id.rlConnectionContainer);
        llLoaderView = (LinearLayout) view.findViewById(R.id.llLoader);

    }

    @Override
    protected boolean isValidData() {
        return false;
    }


    /**
     * Method is used to set the layout on recycler view
     */
    private void recyclerViewLayout() {

        recContentApproval.hasFixedSize();

        mLayoutManager = new LinearLayoutManager(getContext());

        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recContentApproval.setLayoutManager(mLayoutManager);
    }

}
