package com.sticker_android.controller.activities.common.signin;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sticker_android.R;
import com.sticker_android.constant.AppConstant;
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.controller.activities.common.changelanguage.ChangeLanguageActivity;
import com.sticker_android.controller.activities.common.signup.SignUpActivity;
import com.sticker_android.controller.activities.corporate.CorporateProfileActivity;
import com.sticker_android.controller.activities.corporate.home.DesignerHomeActivity;
import com.sticker_android.controller.activities.designer.home.CorporateHomeActivity;
import com.sticker_android.controller.activities.fan.home.FanHomeActivity;
import com.sticker_android.model.User;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.AppConstants;
import com.sticker_android.utils.ProgressDialogHandler;
import com.sticker_android.utils.ShareOneTouchAlertNewBottom;
import com.sticker_android.utils.UserTypeEnum;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.sharedpref.AppPref;

import retrofit2.Call;

public class SigninActivity extends AppBaseActivity implements View.OnClickListener {

    EditText edtEmail;
    private EditText edtPassword;
    private TextView tvForgotPassword;
    private Button btnLogin;
    private TextView tvSignUp;
    private LinearLayout bgLl;
    private AppPref appPref;
    private RadioGroup radioGroup;
    private RadioGroup radioGroupSelect;
    public  String selectedOption="fan";
    private RadioButton rdbtnDesigner, rdbtnFan, rdbtnCorporate;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarGradiant(SigninActivity.this, AppConstants.FAN);
        init();
        setContentView(R.layout.activity_signin);
        setToolbar();
        setViewReferences();
        setViewListeners();
        languageSelection();
        /*changeStatusBarColor(getResources().getColor(R.color.colorFanText));*/
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Utils.changeLanguage(String.valueOf(appPref.getLanguage(0)),this,SigninActivity.class);
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
    }

    private void init() {
        appPref = new AppPref(this);
    }

    @Override
    protected void setViewListeners() {
        btnLogin.setOnClickListener(this);
        tvSignUp.setOnClickListener(this);
        tvForgotPassword.setOnClickListener(this);
    }

    @Override
    protected void setViewReferences() {
        edtEmail = (EditText) findViewById(R.id.act_signin_edt_email);
        edtPassword = (EditText) findViewById(R.id.act_signin_edt_password);
        tvForgotPassword = (TextView) findViewById(R.id.act_signin_forgot_password);
        btnLogin = (Button) findViewById(R.id.act_signin_btn_login);
        tvSignUp = (TextView) findViewById(R.id.act_signin_tv_signup);
        bgLl = (LinearLayout) findViewById(R.id.act_signin_bg_ll);
        rdbtnFan = (RadioButton) findViewById(R.id.rdbtnFan);
        rdbtnDesigner = (RadioButton) findViewById(R.id.rdbtnDesigner);
        rdbtnCorporate = (RadioButton) findViewById(R.id.rdbtnCorporate);
        radioGroupSelect = (RadioGroup) findViewById(R.id.RadioGroupSelect);
    }

    @Override
    protected boolean isValidData() {

        String email = this.edtEmail.getText().toString().trim();
        if (email.isEmpty()) {
            this.edtEmail.requestFocus();
            Utils.showToast(this,getString(R.string.msg_email_cannot_be_empty));
            //  CommonSnackBar.show(edtEmail, getString(R.string.msg_email_cannot_be_empty), Snackbar.LENGTH_SHORT);
            return false;
        } else if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            String password = this.edtPassword.getText().toString();
            if (password.isEmpty()) {
                this.edtPassword.requestFocus();
                Utils.showToast(this,getString(R.string.msg_password_cannot_be_empty));

                // CommonSnackBar.show(edtEmail, getString(R.string.msg_password_cannot_be_empty), Snackbar.LENGTH_SHORT);
                return false;
            }
        } else {
            Utils.showToast(this,getString(R.string.msg_email_not_valid));
            //CommonSnackBar.show(edtEmail, getString(R.string.msg_email_not_valid), Snackbar.LENGTH_SHORT);
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        Utils.hideKeyboard(this);
        switch (v.getId()) {
            case R.id.act_signin_btn_login:
                if (isValidData()) {
                    loginApiCall();
                }
                break;
            case R.id.act_signin_tv_signup:
                edtEmail.setText("");
                edtPassword.setText("");
                Intent intent=new Intent(this,SignUpActivity.class);
                intent.putExtra(AppConstants.USERSELECTION,selectedOption);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_animation_enter,
                        R.anim.activity_animation_exit);
                break;
            case R.id.act_signin_forgot_password:
                Log.d("Forgot password","cloicked");
                openBottomSheet();
                break;
            default:

        }
    }

    /**
     * Method is used for login api
     */
    private void loginApiCall() {

        String deviceId = Utils.getDeviceId(this);
        final ProgressDialogHandler progressDialogHandler=new ProgressDialogHandler(this);
        progressDialogHandler.show();
        Call<ApiResponse> apiResponseCall=RestClient.getService().userLogin(edtEmail.getText().toString(),edtPassword.getText().toString(),"android","1233",deviceId,selectedOption);
        apiResponseCall.enqueue(new ApiCall(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                progressDialogHandler.hide();
                if (apiResponse.status) {
                    appPref.saveUserObject(apiResponse.paylpad.getData());
                    appPref.setLoginFlag(true);
                    moveToActivity();
                } else
                    Utils.showToast(SigninActivity.this,apiResponse.error.message);

                //  CommonSnackBar.show(edtEmail, apiResponse.error.message, Snackbar.LENGTH_SHORT);

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
            if(user.getCompanyName()!=null&&!user.getCompanyName().isEmpty())
            {
                intent=new Intent(SigninActivity.this,CorporateHomeActivity.class);
            }else{
                intent=new Intent(SigninActivity.this,CorporateProfileActivity.class);
            }
        } else if (user.getUserType().equals("fan")) {
            intent=new Intent(SigninActivity.this,FanHomeActivity.class);
        } else if (user.getUserType().equals("designer")) {
            intent=new Intent(SigninActivity.this,DesignerHomeActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_animation_enter,
                R.anim.activity_animation_exit);
        finish();
    }


    private void openBottomSheet() {

        ShareOneTouchAlertNewBottom dialogFragment = new  ShareOneTouchAlertNewBottom(new ShareOneTouchAlertNewBottom.DialogListener() {
            @Override
            public void listener(Dialog dialog, EditText editText) {
                if (isForgetEmailValidate(editText)) {
                    apiForgotPassword(editText, (BottomSheetDialog) dialog);
                }
            }




        },selectedOption);
        dialogFragment.show(getSupportFragmentManager(), "Forgot password");

    }

    private void apiForgotPassword(EditText edtEmail, final BottomSheetDialog mBottomSheetDialog) {
        final ProgressDialogHandler progressDialogHandler=new ProgressDialogHandler(this);
        progressDialogHandler.show();
        Call<ApiResponse> apiResponseCall = RestClient.getService().forgotPassword(edtEmail.getText().toString());
        apiResponseCall.enqueue(new ApiCall(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                progressDialogHandler.hide();
                if (apiResponse.status) {
                    mBottomSheetDialog.dismiss();
                    Utils.showToast(SigninActivity.this,apiResponse.message.message);
                    //CommonSnackBar.show(SigninActivity.this.edtEmail, apiResponse.message.message, Snackbar.LENGTH_SHORT);
                } else {
                    Utils.showToast(SigninActivity.this,apiResponse.error.message);

//                    CommonSnackBar.show(SigninActivity.this.edtEmail, apiResponse.error.message, Snackbar.LENGTH_SHORT);
                }
            }

            @Override
            public void onFail(Call<ApiResponse> call, Throwable t) {
                progressDialogHandler.hide();
            }
        });

    }

    private boolean isForgetEmailValidate(EditText edtEmail) {

        String email = edtEmail.getText().toString().trim();
        if (email.isEmpty()) {
            edtEmail.requestFocus();
            Toast.makeText(getApplicationContext(),getString(R.string.msg_email_cannot_be_empty),Toast.LENGTH_SHORT).show();
            //   CommonSnackBar.show(edtEmail, getString(R.string.msg_email_cannot_be_empty), Snackbar.LENGTH_SHORT);
            return false;
        } else if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return true;
        } else {
            Toast.makeText(getApplicationContext(),getString(R.string.msg_email_not_valid),Toast.LENGTH_SHORT).show();
            //   CommonSnackBar.show(edtEmail, getString(R.string.msg_email_not_valid), Snackbar.LENGTH_SHORT);
            return false;
        }

    }
    private void languageSelection() {

        radioGroupSelect.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if(checkedId==R.id.rdbtnCorporate){
                    selectedOption="corporate";
                    rdbtnCorporate.setTextColor(getResources().getColor(R.color.corporateBtnBackground));
                    bgLl.setBackgroundResource(R.drawable.gradient_bg_hdpi);
                    rdbtnDesigner.setTextColor(getResources().getColor(R.color.edt_background_tint));
                    rdbtnFan.setTextColor(getResources().getColor(R.color.edt_background_tint));
                    btnLogin.setBackgroundDrawable(getResources().getDrawable(R.drawable.corporate_btn_background));
                    tvSignUp.setTextColor(getResources().getColor(R.color.colorCorporateText));
                    /*changeStatusBarColor(getResources().getColor(R.color.colorstatusBarCorporate));*/
                    setStatusBarGradiant(SigninActivity.this, AppConstants.CORPORATE);
                }else if(checkedId==R.id.rdbtnDesigner){
                    selectedOption="designer";
                    rdbtnDesigner.setTextColor(getResources().getColor(R.color.colorDesignerText));
                    bgLl.setBackgroundResource(R.drawable.gradient_bg_des_hdpi);
                    rdbtnCorporate.setTextColor(getResources().getColor(R.color.edt_background_tint));
                    rdbtnFan.setTextColor(getResources().getColor(R.color.edt_background_tint));
                    btnLogin.setBackgroundDrawable(getResources().getDrawable(R.drawable.designer_btn_background));
                    tvSignUp.setTextColor(getResources().getColor(R.color.colorDesignerText));
                    /*changeStatusBarColor(getResources().getColor(R.color.colorstatusBarDesigner));*/
                    setStatusBarGradiant(SigninActivity.this, AppConstants.DESIGNER);

                }else if(checkedId==R.id.rdbtnFan){
                    selectedOption="fan";
                    rdbtnFan.setTextColor(getResources().getColor(R.color.colorFanText));
                    bgLl.setBackgroundResource(R.drawable.gradient_bg_fan_hdpi);
                    btnLogin.setBackgroundDrawable(getResources().getDrawable(R.drawable.fan_btn_background));
                    tvSignUp.setTextColor(getResources().getColor(R.color.colorFanText));
                    rdbtnCorporate.setTextColor(getResources().getColor(R.color.edt_background_tint));
                    rdbtnDesigner.setTextColor(getResources().getColor(R.color.edt_background_tint));
                    /*changeStatusBarColor(getResources().getColor(R.color.colorstatusBarFan));*/
                    setStatusBarGradiant(SigninActivity.this, AppConstants.FAN);
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.fan_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startNewActivity(ChangeLanguageActivity.class);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (!hasFocus) {
            Utils.hideSoftKeyboard(this, null);
        }
    }


}
