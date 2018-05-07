package com.sticker_android.controller.fragment.common;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sticker_android.R;
import com.sticker_android.controller.fragment.base.BaseFragment;
import com.sticker_android.model.User;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.sharedpref.AppPref;

import retrofit2.Call;


public class AboutUsFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TextView textView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private AppPref appPref;
    private User user;

    public AboutUsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AboutUsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AboutUsFragment newInstance(String param1, String param2) {
        AboutUsFragment fragment = new AboutUsFragment();
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
        View view = inflater.inflate(R.layout.fragment_about_us, container, false);
        init();
        setViewReferences(view);
        setViewListeners();
        getContactApi();
        return view;
    }

    private void init() {
        appPref=new AppPref(getActivity());
        user=appPref.getUserInfo();

    }

    private void getContactApi() {

        Call<ApiResponse> apiResponseCall= RestClient.getService().apiGetContent("4");
        apiResponseCall.enqueue(new ApiCall(getActivity()) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if(apiResponse.status){
                    textView.setText(apiResponse.paylpad.getData().getInfoText());
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
        textView = view.findViewById(R.id.tv_terms_conditions);
    }

    @Override
    protected boolean isValidData() {
        return false;
    }
}
