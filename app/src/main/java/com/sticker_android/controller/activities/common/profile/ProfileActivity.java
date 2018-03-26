package com.sticker_android.controller.activities.common.profile;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.sticker_android.R;
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.controller.activities.common.signin.SigninActivity;
import com.sticker_android.model.UserData;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.CommonSnackBar;
import com.sticker_android.utils.commonprogressdialog.CommonProgressBar;
import com.sticker_android.utils.sharedpref.AppPref;

import retrofit2.Call;

public class ProfileActivity extends AppBaseActivity implements View.OnClickListener{

    private EditText firstName,lastName;
    private RelativeLayout rlBgProfile;
    private LinearLayout llCorporate;
    private AppPref appPref;
    private EditText edtCompanyName,edtCompanyAddress,edtProfileFirstName;
    private EditText edtProfileLastName,edtProfileEmail;
    private Button btnSubmit;
    private UserData userData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setViewReferences();
        setViewListeners();
        init();
    }

    @Override
    protected void setViewListeners() {
        btnSubmit.setOnClickListener(this);
    }

    @Override
    protected void setViewReferences() {
        rlBgProfile= (RelativeLayout) findViewById(R.id.bgProfile);
        llCorporate= (LinearLayout) findViewById(R.id.llCorporate);
        edtCompanyName= (EditText) findViewById(R.id.act_profile_edt_company_name);
        edtCompanyAddress= (EditText) findViewById(R.id.act_profile_edt_company_address);
        edtProfileFirstName= (EditText) findViewById(R.id.act_profile_edt_first_name);
        edtProfileLastName= (EditText) findViewById(R.id.act_profile_edt_last_name);
        edtProfileEmail= (EditText) findViewById(R.id.act_profile_edt_email);
        btnSubmit= (Button) findViewById(R.id.act_profile_btn_register);
    }

    @Override
    protected boolean isValidData()
    {
        String firstName = this.edtProfileFirstName.getText().toString().trim();
        String lastName = this.edtProfileLastName.getText().toString().trim();
        if (firstName.isEmpty()) {
            CommonSnackBar.show(edtProfileFirstName,getString(R.string.first_name_cannot_be_empty), Snackbar.LENGTH_SHORT);
            this.edtProfileFirstName.requestFocus();
            return false;
        }else if (lastName.isEmpty()) {
            CommonSnackBar.show(edtProfileLastName,getString(R.string.last_name_cannot_be_empty),Snackbar.LENGTH_SHORT);
            this.edtProfileLastName.requestFocus();
            return false;
        }
        String email = this.edtProfileEmail.getText().toString().trim();
        if (email.isEmpty()) {
            CommonSnackBar.show(edtProfileEmail,getString(R.string.msg_email_cannot_be_empty),Snackbar.LENGTH_SHORT);
            this.edtProfileEmail.requestFocus();
            return false;
        }else if (Patterns.EMAIL_ADDRESS.matcher(email).matches())  {
            CommonSnackBar.show(edtProfileEmail,getString(R.string.msg_email_not_valid),Snackbar.LENGTH_SHORT);
            this.edtProfileEmail.requestFocus();
            return false;
        }

        return true;
    }


    private void init() {
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
                    break;
            }
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
                    userData.getEmail(), userData.getUserType());
            apiResponseCall.enqueue(new ApiCall(getActivity()) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    commonProgressBar.hide();
                    if (apiResponse.status) {
                        appPref.saveUserObject(apiResponse.paylpad.getData());
                        appPref.setLoginFlag(true);
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
