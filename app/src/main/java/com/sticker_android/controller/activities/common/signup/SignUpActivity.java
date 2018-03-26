package com.sticker_android.controller.activities.common.signup;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.sticker_android.R;
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.controller.activities.common.profile.ProfileActivity;
import com.sticker_android.controller.activities.common.signin.SigninActivity;
import com.sticker_android.controller.activities.corporate.home.DesignerHomeActivity;
import com.sticker_android.controller.activities.designer.home.CorporateHomeActivity;
import com.sticker_android.controller.activities.fan.home.FanHomeActivity;
import com.sticker_android.model.UserData;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.CommonSnackBar;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.commonprogressdialog.CommonProgressBar;
import com.sticker_android.utils.sharedpref.AppPref;

import retrofit2.Call;

public class SignUpActivity extends AppBaseActivity {

    private EditText edtFirstName,edtLastName,edtEmail,edtConfirmPassword,edtPassword;
    private Button btnSignUp;
    private AppPref appPref;
    private LinearLayout bgSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow_small);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setViewReferences();
        setViewListeners();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                            }
        });
        setBackground();
    }

    private void setBackground() {
    switch (SigninActivity.selectedOption){
        case "fan":
            bgSignup.setBackground(getResources().getDrawable(R.drawable.gradient_bg_fan_hdpi));
            btnSignUp.setBackgroundDrawable(getResources().getDrawable(R.drawable.fan_btn_background));
            break;
        case "designer":
            bgSignup.setBackground(getResources().getDrawable(R.drawable.gradient_bg_des_hdpi));
            btnSignUp.setBackgroundDrawable(getResources().getDrawable(R.drawable.designer_btn_background));
            break;
        case "corporate":
            bgSignup.setBackground(getResources().getDrawable(R.drawable.gradient_bg_hdpi));
           btnSignUp.setBackgroundDrawable(getResources().getDrawable(R.drawable.corporate_btn_background));
            break;
    }
    }

    private void init() {
         appPref=new AppPref(this);
    }

    @Override
    protected void setViewListeners() {

    btnSignUp.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
     if(isValidData())
      apiSignUp();
    }
     });
    }

    /**
     * Method is used to register the user
     */
    private void apiSignUp() {

        String deviceId=   Utils.getDeviceId(this);
        int language=getSelectedLanguage();

        final CommonProgressBar commonProgressBar=new CommonProgressBar(this);
        commonProgressBar.show();
        Call<ApiResponse> apiResponseCall= RestClient.getService().userRegistration(language,edtEmail.getText().toString(),
                edtPassword.getText().toString(),edtFirstName.getText().toString(),edtLastName.getText().toString(),
                SigninActivity.selectedOption,"android","111",deviceId);
        apiResponseCall.enqueue(new ApiCall(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                commonProgressBar.hide();
                if(apiResponse.status) {
                    appPref.saveUserObject(apiResponse.paylpad.getData());
                    appPref.setLoginFlag(true);
                    moveToActivity();
                }else{
                    CommonSnackBar.show(edtEmail,apiResponse.error.message,Snackbar.LENGTH_SHORT);
            }
            }
            @Override
            public void onFail(Call<ApiResponse> call, Throwable t) {
                commonProgressBar.hide();
            }
        });
    }

    private void moveToActivity() {
       startNewActivity(ProfileActivity.class);
    }
    @Override
    protected void setViewReferences() {
         edtFirstName=findViewById(R.id.act_signup_edt_first_name);
         edtLastName=  findViewById(R.id.act_signup_edt_last_name);
         edtEmail=     findViewById(R.id.act_signup_edt_email);
         edtPassword= findViewById(R.id.act_signup_edt_password);
         edtConfirmPassword=findViewById(R.id.act_signup_edt_confirm_password);
        btnSignUp=findViewById(R.id.act_signup_btn_register);
        bgSignup=(LinearLayout)findViewById(R.id.bgSignup);
    }

    @Override
    protected boolean isValidData() {

        String firstName = this.edtFirstName.getText().toString().trim();
        String lastName = this.edtLastName.getText().toString().trim();
        if (firstName.isEmpty()) {
            CommonSnackBar.show(edtFirstName,getString(R.string.first_name_cannot_be_empty),Snackbar.LENGTH_SHORT);
            this.edtFirstName.requestFocus();
            return false;
        }
        if (lastName.isEmpty()) {
            CommonSnackBar.show(edtFirstName,getString(R.string.last_name_cannot_be_empty),Snackbar.LENGTH_SHORT);
            this.edtLastName.requestFocus();
            return false;
        } else {
            String email = this.edtEmail.getText().toString().trim();
            if (email.isEmpty()) {
                CommonSnackBar.show(edtEmail,getString(R.string.msg_email_cannot_be_empty),Snackbar.LENGTH_SHORT);
                this.edtEmail.requestFocus();
                return false;
            } else if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                String password = this.edtPassword.getText().toString().trim();
                if (password.isEmpty()) {
                    CommonSnackBar.show(edtPassword,getString(R.string.password_cannot_be_empty),Snackbar.LENGTH_SHORT);
                    this.edtPassword.requestFocus();
                    return false;
                } else if (password.length() < 8) {
                    CommonSnackBar.show(edtPassword,getString(R.string.password_cannot_be_less),Snackbar.LENGTH_SHORT);
                    this.edtPassword.requestFocus();
                    return false;
                } else {
                    String confirmPassword = this.edtConfirmPassword.getText().toString().trim();
                    if (confirmPassword.isEmpty()) {
                        CommonSnackBar.show(edtConfirmPassword,getString(R.string.confirm_password_cannot_be_empty),Snackbar.LENGTH_SHORT);
                        this.edtPassword.requestFocus();
                        return false;

                    } else if (password.equals(confirmPassword)) {

                    } else {
                        CommonSnackBar.show(edtPassword,getString(R.string.confirm_password_not_match),Snackbar.LENGTH_SHORT);
                        this.edtPassword.requestFocus();
                        return false;
                    }
                }
            } else {
                CommonSnackBar.show(edtEmail,getString(R.string.msg_email_not_valid),Snackbar.LENGTH_SHORT);
                this.edtEmail.requestFocus();
                return false;
            }
        }
        return true;

    }

    public int getSelectedLanguage() {


        return appPref.getLanguage(0);
    }
}
