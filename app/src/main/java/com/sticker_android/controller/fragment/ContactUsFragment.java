package com.sticker_android.controller.fragment;

import android.os.Bundle;
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


public class ContactUsFragment extends BaseFragment {
    private AppPref appPref;
    private UserData userData;
    private TextView tvEmailContactUs;
    private TextView tvContactUsContactNum;

    public ContactUsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view=  inflater.inflate(R.layout.fragment_contact_us, container, false);
        init();
        setViewReferences(view);
        setViewListeners();
        setBackground();
        getuserData();
        getContactApi();
    return view;
    }

    private void getuserData() {
        userData=appPref.getUserInfo();
    }

    private void init() {
        appPref=new AppPref(getActivity());

    }


    private void getContactApi() {
        Call<ApiResponse> apiResponseCall= RestClient.getService().apiGetContent(userData.getId(),"1");
        apiResponseCall.enqueue(new ApiCall(getActivity()) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if(apiResponse.success){
               tvContactUsContactNum.setText(apiResponse.paylpad.getData().getMobile());
                    tvEmailContactUs.setText(apiResponse.paylpad.getData().getEmail());
                }
            }

            @Override
            public void onFail(Call<ApiResponse> call, Throwable t) {

            }
        });
    }
    private void setBackground() {
        appPref=new AppPref(getActivity());
        userData=appPref.getUserInfo();
        if(userData.getUserType()!=null)
            switch (userData.getUserType()){
                case "fan":
                    tvEmailContactUs.setTextColor(getResources().getColor(R.color.colorFanText));
                    tvContactUsContactNum.setTextColor(getResources().getColor(R.color.colorFanText));
                    break;
                case "designer":
                    tvEmailContactUs.setTextColor(getResources().getColor(R.color.colorDesignerText));
                    tvContactUsContactNum.setTextColor(getResources().getColor(R.color.colorDesignerText));
                    break;
                case "corporate":
                    tvEmailContactUs.setTextColor(getResources().getColor(R.color.colorCorporateText));
                    tvContactUsContactNum.setTextColor(getResources().getColor(R.color.colorCorporateText));

                    break;
            }
    }

    @Override
    protected void setViewListeners() {

    }

    @Override
    protected void setViewReferences(View view) {
        tvEmailContactUs=(TextView)view.findViewById(R.id.tvEmailContactUs);
        tvContactUsContactNum=(TextView)view.findViewById(R.id.tvContactUsContactNum);
    }

    @Override
    protected boolean isValidData() {
        return false;
    }


}
