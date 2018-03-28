package com.sticker_android.controller.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sticker_android.R;
import com.sticker_android.controller.fragment.base.BaseFragment;
import com.sticker_android.model.UserData;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.sharedpref.AppPref;

import retrofit2.Call;


public class TermsAndConditionFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView tvTerms;
private AppPref appPref;
    private UserData userData;

    public TermsAndConditionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TermsAndConditionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TermsAndConditionFragment newInstance(String param1, String param2) {
        TermsAndConditionFragment fragment = new TermsAndConditionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_terms_and_condition, container, false);
        init();
        setViewReferences(view);
        getuserData();
        getTermsConditionData();

         return view;
    }

    private void getuserData() {
    userData=appPref.getUserInfo();
    }

    private void init() {
    appPref=new AppPref(getActivity());

    }

    private void getTermsConditionData() {
      Call<ApiResponse> apiResponseCall= RestClient.getService().apiGetContent(userData.getId(),"2");
    apiResponseCall.enqueue(new ApiCall(getActivity()) {
        @Override
        public void onSuccess(ApiResponse apiResponse) {
            if(apiResponse.success){
                tvTerms.setText(apiResponse.paylpad.getData().getInfoText());
            }
        }

        @Override
        public void onFail(Call<ApiResponse> call, Throwable t) {

        }
    });
    }


    @Override
    protected void setViewListeners() {

    }

    @Override
    protected void setViewReferences(View view) {
       tvTerms=view.findViewById(R.id.tv_terms_conditions);
    }

    @Override
    protected boolean isValidData() {
        return false;
    }


}
