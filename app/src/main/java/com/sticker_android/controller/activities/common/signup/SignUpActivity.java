package com.sticker_android.controller.activities.common.signup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sticker_android.R;
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.controller.activities.common.terms.TermsActivity;
import com.sticker_android.controller.activities.corporate.CorporateProfileActivity;
import com.sticker_android.controller.activities.designer.home.DesignerHomeActivity;
import com.sticker_android.controller.activities.fan.home.FanHomeActivity;
import com.sticker_android.model.User;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.AppConstants;
import com.sticker_android.utils.ProgressDialogHandler;
import com.sticker_android.utils.UserTypeEnum;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.sharedpref.AppPref;

import retrofit2.Call;

public class SignUpActivity extends AppBaseActivity {

    private EditText edtFirstName, edtLastName, edtEmail, edtConfirmPassword, edtPassword;
    private Button btnSignUp;
    private AppPref appPref;
    private LinearLayout bgSignup;
    private CheckBox checkBoxTerms;
    private TextView tvTermsConditions;
    private String type="fan";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back_arrow_small);
        setViewReferences();
        setViewListeners();
        getUserSelectedOption();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        changeStatusBarColor(getResources().getColor(R.color.colorFanText));
        setBackground();
        setLoginButtonData();
    }

    private void getUserSelectedOption() {
    if(getIntent().getExtras()!=null){
          type=getIntent().getExtras().getString(AppConstants.USERSELECTION);
    }

    }

    private void setLoginButtonData() {
        btnSignUp.setText("Sign up");
    }


    /**
     * This method is used to set the background of app according to type
     */
    private void setBackground() {
        UserTypeEnum userTypeEnum=Enum.valueOf(UserTypeEnum.class,type.toUpperCase());
    switch (userTypeEnum){
        case FAN:
            bgSignup.setBackground(getResources().getDrawable(R.drawable.gradient_bg_fan_hdpi));
            btnSignUp.setBackgroundDrawable(getResources().getDrawable(R.drawable.fan_btn_background));
            /*changeStatusBarColor(getResources().getColor(R.color.colorstatusBarFan));*/
            setStatusBarGradiant(SignUpActivity.this, AppConstants.FAN);
            break;
        case DESIGNER:
            bgSignup.setBackground(getResources().getDrawable(R.drawable.gradient_bg_des_hdpi));
            btnSignUp.setBackgroundDrawable(getResources().getDrawable(R.drawable.designer_btn_background));
            /*changeStatusBarColor(getResources().getColor(R.color.colorstatusBarDesigner));*/
            setStatusBarGradiant(SignUpActivity.this, AppConstants.DESIGNER);
            break;
        case CORPORATE:
            bgSignup.setBackground(getResources().getDrawable(R.drawable.gradient_bg_hdpi));
           btnSignUp.setBackgroundDrawable(getResources().getDrawable(R.drawable.corporate_btn_background));
            /*changeStatusBarColor(getResources().getColor(R.color.colorstatusBarCorporate));*/
            setStatusBarGradiant(SignUpActivity.this, AppConstants.CORPORATE);
            break;
    }
    }

    private void init() {
        appPref = new AppPref(this);
    }

    @Override
    protected void setViewListeners() {
        tvTermsConditions.setOnClickListener(new View.OnClickListener() {
    @Override
     public void onClick(View v) {
        Intent intent=new Intent(SignUpActivity.this,TermsActivity.class);
        intent.putExtra(AppConstants.USERSELECTION,type);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_animation_enter,
                R.anim.activity_animation_exit);

    }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideKeyboard(SignUpActivity.this);
                if (isValidData()) {
                    if (checkBoxTerms.isChecked()) {
                        apiSignUp();
                    } else {
                        Utils.showToast(SignUpActivity.this, "Please select terms and conditions ");
                    }
                }
            }
        });
    }

    /**
     * Method is used to register the user
     */
    private void apiSignUp() {

        String deviceId = Utils.getDeviceId(this);
        int language = getSelectedLanguage();

        final ProgressDialogHandler progressDialogHandler=new ProgressDialogHandler(this);
        progressDialogHandler.show();

        Call<ApiResponse> apiResponseCall= RestClient.getService().userRegistration(language,edtEmail.getText().toString(),
                edtPassword.getText().toString(),edtFirstName.getText().toString(),edtLastName.getText().toString(),
                type,"android","111",deviceId);
        apiResponseCall.enqueue(new ApiCall(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
             progressDialogHandler.hide();
                if (apiResponse.status) {
                    appPref.saveUserObject(apiResponse.paylpad.getData());
                    appPref.setLoginFlag(true);
                    moveToActivity();
                } else {
                    Utils.showToast(SignUpActivity.this,apiResponse.error.message);

                    //     CommonSnackBar.show(edtEmail, apiResponse.error.message, Snackbar.LENGTH_SHORT);
                }
            }

            @Override
            public void onFail(Call<ApiResponse> call, Throwable t) {
       progressDialogHandler.hide();
            }
        });
    }

    private void moveToActivity() {
        Intent intent=null;
        User user = appPref.getUserInfo();
        if (user.getUserType().equals("corporate")) {
             intent=new Intent(SignUpActivity.this,CorporateProfileActivity.class);
        } else if (user.getUserType().equals("fan")) {
             intent=new Intent(SignUpActivity.this,FanHomeActivity.class);
        } else if (user.getUserType().equals("designer")) {
             intent=new Intent(SignUpActivity.this,DesignerHomeActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_animation_enter,
                R.anim.activity_animation_exit);
        finish();
    }

    @Override
    protected void setViewReferences() {
        edtFirstName = (EditText) findViewById(R.id.act_signup_edt_first_name);
        edtLastName = (EditText) findViewById(R.id.act_signup_edt_last_name);
        edtEmail = (EditText) findViewById(R.id.act_signup_edt_email);
        edtPassword = (EditText) findViewById(R.id.act_signup_edt_password);
        edtConfirmPassword = (EditText) findViewById(R.id.act_signup_edt_confirm_password);
        btnSignUp = (Button) findViewById(R.id.act_signup_btn_register);
        bgSignup = (LinearLayout)findViewById(R.id.bgSignup);
        checkBoxTerms=(CheckBox)findViewById(R.id.act_signup_terms_conditions);
        tvTermsConditions=(TextView)findViewById(R.id.tv_act_signup_terms_conditions);
    }

    @Override
    protected boolean isValidData() {

        String firstName = this.edtFirstName.getText().toString().trim();
        String lastName = this.edtLastName.getText().toString().trim();
        if (firstName.isEmpty()) {
            Utils.showToast(SignUpActivity.this,getString(R.string.first_name_cannot_be_empty));

         //   CommonSnackBar.show(edtFirstName, getString(R.string.first_name_cannot_be_empty), Snackbar.LENGTH_SHORT);
            this.edtFirstName.requestFocus();
            return false;
        }else if(firstName.length()<3){
            Utils.showToast(SignUpActivity.this,getString(R.string.first_name_cannot_be_less_than));

          //  CommonSnackBar.show(edtFirstName, getString(R.string.first_name_cannot_be_less_than), Snackbar.LENGTH_SHORT);
            this.edtFirstName.requestFocus();
            return false;

        }
        if (lastName.isEmpty()) {
            Utils.showToast(SignUpActivity.this,getString(R.string.last_name_cannot_be_empty));
          //  CommonSnackBar.show(edtFirstName, getString(R.string.last_name_cannot_be_empty), Snackbar.LENGTH_SHORT);
            this.edtLastName.requestFocus();
            return false;
        } else if(lastName.length()<3) {
            Utils.showToast(SignUpActivity.this,getString(R.string.last_name_cannot_be_less_than));
            //  CommonSnackBar.show(edtFirstName, getString(R.string.last_name_cannot_be_empty), Snackbar.LENGTH_SHORT);
            this.edtLastName.requestFocus();
            return false;
        }else {
            String email = this.edtEmail.getText().toString().trim();
            if (email.isEmpty()) {
                Utils.showToast(SignUpActivity.this,getString(R.string.msg_email_cannot_be_empty));
             //   CommonSnackBar.show(edtEmail, getString(R.string.msg_email_cannot_be_empty), Snackbar.LENGTH_SHORT);
                this.edtEmail.requestFocus();
                return false;
            } else if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                String password = this.edtPassword.getText().toString().trim();
                if (password.isEmpty()) {
                    Utils.showToast(SignUpActivity.this,getString(R.string.password_cannot_be_empty));
                    //CommonSnackBar.show(edtPassword, getString(R.string.password_cannot_be_empty), Snackbar.LENGTH_SHORT);
                    this.edtPassword.requestFocus();
                    return false;
                } else if (password.length() < 8) {
                    Utils.showToast(SignUpActivity.this,getString(R.string.password_cannot_be_less));

                  //  CommonSnackBar.show(edtPassword, getString(R.string.password_cannot_be_less), Snackbar.LENGTH_SHORT);
                    this.edtPassword.requestFocus();
                    return false;
                } /*else {
                    String confirmPassword = this.edtConfirmPassword.getText().toString().trim();
                    if (confirmPassword.isEmpty()) {
                        Utils.showToast(SignUpActivity.this,getString(R.string.confirm_password_cannot_be_empty));

                     //   CommonSnackBar.show(edtConfirmPassword, getString(R.string.confirm_password_cannot_be_empty), Snackbar.LENGTH_SHORT);
                        this.edtPassword.requestFocus();
                        return false;

                    } else if (password.equals(confirmPassword)) {

                    } else {
                        Utils.showToast(SignUpActivity.this,getString(R.string.confirm_password_not_match));

                        //CommonSnackBar.show(edtPassword, getString(R.string.confirm_password_not_match), Snackbar.LENGTH_SHORT);
                        this.edtPassword.requestFocus();
                        return false;
                    }
                }*/
            } else {
                Utils.showToast(SignUpActivity.this,getString(R.string.msg_email_not_valid));

              //  CommonSnackBar.show(edtEmail, getString(R.string.msg_email_not_valid), Snackbar.LENGTH_SHORT);
                this.edtEmail.requestFocus();
                return false;
            }
        }
        return true;

    }

    public int getSelectedLanguage() {


        return appPref.getLanguage(0);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (!hasFocus) {
            Utils.hideSoftKeyboard(this, null);
        }
    }

    @Override
    public void onBackPressed() {
        Utils.hideKeyboard(this);
        super.onBackPressed();
    }
}
