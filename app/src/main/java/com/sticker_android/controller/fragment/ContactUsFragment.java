package com.sticker_android.controller.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.sticker_android.utils.UserTypeEnum;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.helper.PermissionManager;
import com.sticker_android.utils.sharedpref.AppPref;

import retrofit2.Call;

import static com.sticker_android.utils.helper.PermissionManager.Constant.MAKE_CALL_RQ;


public class ContactUsFragment extends BaseFragment implements View.OnClickListener {
    private AppPref appPref;
    private User user;
    private TextView tvEmailContactUs;
    private TextView tvContactUsContactNum;
    private String mMobileNumber;
    private String mEmail="abc@abc.com";

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
        user =appPref.getUserInfo();
    }

    private void init() {
        appPref=new AppPref(getActivity());

    }


    private void getContactApi() {
        Call<ApiResponse> apiResponseCall= RestClient.getService().apiGetContent(user.getId(),"1");
        apiResponseCall.enqueue(new ApiCall(getActivity()) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if(apiResponse.success){
                    tvContactUsContactNum.setText(apiResponse.paylpad.getData().getMobile());
                    tvEmailContactUs.setText(apiResponse.paylpad.getData().getEmail());
                    mMobileNumber=apiResponse.paylpad.getData().getMobile();
                    mEmail=apiResponse.paylpad.getData().getEmail();
                }
            }

            @Override
            public void onFail(Call<ApiResponse> call, Throwable t) {
            }
        });
    }
    private void setBackground() {
        appPref=new AppPref(getActivity());
        user =appPref.getUserInfo();
        if(user.getUserType()!=null)
        {
            UserTypeEnum userTypeEnum=Enum.valueOf(UserTypeEnum.class,user.getUserType().toUpperCase());
            switch (userTypeEnum) {
                case FAN:
                    tvEmailContactUs.setTextColor(getResources().getColor(R.color.colorFanText));
                    tvContactUsContactNum.setTextColor(getResources().getColor(R.color.colorFanText));
                    break;
                case DESIGNER:
                    tvEmailContactUs.setTextColor(getResources().getColor(R.color.colorDesignerText));
                    tvContactUsContactNum.setTextColor(getResources().getColor(R.color.colorDesignerText));
                    break;
                case CORPORATE:
                    tvEmailContactUs.setTextColor(getResources().getColor(R.color.colorCorporateText));
                    tvContactUsContactNum.setTextColor(getResources().getColor(R.color.colorCorporateText));

                    break;

            }
        }
    }

    @Override
    protected void setViewListeners() {
        tvEmailContactUs.setOnClickListener(this);
        tvContactUsContactNum.setOnClickListener(this);
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


    private void makePhoneCall(String contactNo) {

        try {
            if (contactNo != null) {
                Utils.showPhoneCallPopup(getActivity(), contactNo);
            } else {
                Utils.showToast(getActivity(), getString(R.string.no_contact_available));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvContactUsContactNum:
                if(PermissionManager.checkCallPermissionInFragment(getActivity(), this, MAKE_CALL_RQ)){
                    makePhoneCall(tvContactUsContactNum.getText().toString().trim());
                }
                break;
            case R.id.tvEmailContactUs:
                String to[] = {tvEmailContactUs.getText().toString().trim()};
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, to);
                startActivity(Intent.createChooser(intent, "Complete action using"));
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MAKE_CALL_RQ:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if (mMobileNumber != null) {
                        makePhoneCall(mMobileNumber);
                    }

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Utils.showToast(getActivity(), getString(R.string.phone_call_permission));
                }
                break;
        }
    }
}
