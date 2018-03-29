package com.sticker_android.controller.fragment.corporate;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sticker_android.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CorporateReportFragment extends Fragment {


    public CorporateReportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_corporate_report, container, false);
    }

}
