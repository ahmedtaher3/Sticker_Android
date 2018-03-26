package com.sticker_android.controller.fragment;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.sticker_android.R;
import com.sticker_android.controller.activities.fan.home.FanHomeActivity;
import com.sticker_android.controller.fragment.base.BaseFragment;
import com.sticker_android.model.UserData;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.CommonSnackBar;
import com.sticker_android.utils.commonprogressdialog.CommonProgressBar;
import com.sticker_android.utils.sharedpref.AppPref;

import retrofit2.Call;

public class ProfileFragment extends BaseFragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText firstName,lastName;
    private RelativeLayout rlBgProfile;
    private LinearLayout llCorporate;
    private AppPref appPref;
    private EditText edtCompanyName,edtCompanyAddress,edtProfileFirstName;
    private EditText edtProfileLastName,edtProfileEmail;
     private Button btnSubmit;
    private UserData userData;

    public ProfileFragment() {
        // Required empty public constructor
    }

     public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
       View view=  inflater.inflate(R.layout.fragment_profile, container, false);
        init();
        setViewReferences(view);
        setViewListeners();
        setUserBackground();
        setUserData();
    return view;
    }

    private void setUserData() {
        edtProfileFirstName.setText(userData.getFirstName());
        edtProfileLastName.setText(userData.getLastName());
        edtProfileEmail.setText(userData.getEmail());
        edtProfileFirstName.setSelection(edtProfileFirstName.getText().length());
        edtProfileLastName.setSelection(edtProfileLastName.getText().length());
        edtProfileEmail.setSelection(edtProfileEmail.getText().length());

    }

    private void setUserBackground() {
        userData=appPref.getUserInfo();
        if(userData.getUserType()!=null)
            switch (userData.getUserType()){
                case "fan":
                    rlBgProfile.setBackground(getResources().getDrawable(R.drawable.gradient_bg_fan_hdpi));
                    llCorporate.setVisibility(View.GONE);
                    break;
                case "designer":
                    rlBgProfile.setBackground(getResources().getDrawable(R.drawable.gradient_bg_des_hdpi));
                    llCorporate.setVisibility(View.GONE);
                    break;
                case "corporate":
                    rlBgProfile.setBackground(getResources().getDrawable(R.drawable.corporate_hdpi));
                    llCorporate.setVisibility(View.VISIBLE);
                     edtCompanyName.setText(userData.getCompanyName());
                    edtCompanyAddress.setText(userData.getCompanyAddress());
                    edtCompanyName.setSelection(edtCompanyName.getText().length());
                    edtCompanyAddress.setSelection(edtCompanyAddress.getText().length());
                    break;
            }
    }

    private void init() {
        appPref=new AppPref(getActivity());

    }


    @Override
    protected void setViewListeners() {
           btnSubmit.setOnClickListener(this);
    }

    @Override
    protected void setViewReferences(View view) {
        rlBgProfile=view.findViewById(R.id.bgProfile);
        llCorporate=view.findViewById(R.id.llCorporate);
        edtCompanyName=view.findViewById(R.id.act_profile_edt_company_name);
        edtCompanyAddress=view.findViewById(R.id.act_profile_edt_company_address);
        edtProfileFirstName=view.findViewById(R.id.act_profile_edt_first_name);
        edtProfileLastName=view.findViewById(R.id.act_profile_edt_last_name);
        edtProfileEmail=view.findViewById(R.id.act_profile_edt_email);
        btnSubmit=view.findViewById(R.id.act_profile_btn_register);
    }

    @Override
    protected boolean isValidData() {
        String firstName = this.edtProfileFirstName.getText().toString().trim();
        String lastName = this.edtProfileLastName.getText().toString().trim();
        String email = this.edtProfileEmail.getText().toString().trim();
        if (firstName.isEmpty()) {
            CommonSnackBar.show(edtProfileFirstName,getString(R.string.first_name_cannot_be_empty), Snackbar.LENGTH_SHORT);
            this.edtProfileFirstName.requestFocus();
            return false;
        }else if (lastName.isEmpty()) {
            CommonSnackBar.show(edtProfileLastName,getString(R.string.last_name_cannot_be_empty),Snackbar.LENGTH_SHORT);
            this.edtProfileLastName.requestFocus();
            return false;
        } else if (email.isEmpty()) {
                CommonSnackBar.show(edtProfileEmail,getString(R.string.msg_email_cannot_be_empty),Snackbar.LENGTH_SHORT);
                this.edtProfileEmail.requestFocus();
                return false;
            }/*else if (Patterns.EMAIL_ADDRESS.matcher(email).matches())  {
                CommonSnackBar.show(edtProfileEmail,getString(R.string.msg_email_not_valid),Snackbar.LENGTH_SHORT);
                this.edtProfileEmail.requestFocus();
                return false;
            }*/else
            return true;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.act_profile_btn_register:
                if(isValidData()){
                    updateProfileApi();
                }
                break;
        }
    }

    private void updateProfileApi() {
        if (userData.getId() != null) {
            final CommonProgressBar commonProgressBar = new CommonProgressBar(getActivity());
            commonProgressBar.show();
            Call<ApiResponse> apiResponseCall = RestClient.getService().updateProfile(userData.getId(), edtCompanyName.getText().toString(),
                   "", edtCompanyAddress.getText().toString(), edtProfileFirstName.getText().toString(), edtProfileLastName.getText().toString(),
                    edtProfileEmail.getText().toString(), userData.getUserType());
            apiResponseCall.enqueue(new ApiCall(getActivity()) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    commonProgressBar.hide();
                    if (apiResponse.status) {
                        appPref.saveUserObject(apiResponse.paylpad.getData());
                        appPref.setLoginFlag(true);
                        CommonSnackBar.show(edtCompanyAddress, "Data updated successfully", Snackbar.LENGTH_SHORT);

                    } else {
                        CommonSnackBar.show(edtCompanyAddress, apiResponse.error.message, Snackbar.LENGTH_SHORT);
                    }
                }

                @Override
                public void onFail(Call<ApiResponse> call, Throwable t) {
                    commonProgressBar.hide();
                }
            });

        }
    }
}
