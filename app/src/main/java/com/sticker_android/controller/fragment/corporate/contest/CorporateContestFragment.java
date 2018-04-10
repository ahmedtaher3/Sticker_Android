package com.sticker_android.controller.fragment.corporate.contest;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sticker_android.R;
import com.sticker_android.controller.fragment.base.BaseFragment;
import com.sticker_android.utils.fragmentinterface.UpdateToolbarTitle;

/**
 * A simple {@link Fragment} subclass.
 */
public class CorporateContestFragment extends BaseFragment {

    private UpdateToolbarTitle mUpdateToolbarCallback;
    public CorporateContestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mUpdateToolbarCallback.updateToolbarTitle(getResources().getString(R.string.txt_contest));
            }
        }, 300);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_corporate_contest, container, false);
    }

    @Override
    protected void setViewListeners() {

    }

    @Override
    protected void setViewReferences(View view) {

    }

    @Override
    protected boolean isValidData() {
        return false;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mUpdateToolbarCallback = (UpdateToolbarTitle) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }
}
