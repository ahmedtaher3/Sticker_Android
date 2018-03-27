package com.sticker_android.controller.activities.common.signin;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sticker_android.R;
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.controller.activities.common.signup.SignUpActivity;
import com.sticker_android.controller.activities.fan.home.FanHomeActivity;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.CommonSnackBar;
import com.sticker_android.utils.ShareOneTouchAlertNewBottom;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.commonprogressdialog.CommonProgressBar;
import com.sticker_android.utils.sharedpref.AppPref;

import retrofit2.Call;

public class SigninActivity extends AppBaseActivity implements View.OnClickListener {

    EditText edtEmail;
    private EditText edtPassword;
    private TextView tvForgotPassword;
    private Button btnLogin;
    private TextView tvSignUp;
    private CheckedTextView chtvFan, chtvDesigner, chtvCorporate;
    private LinearLayout bgLl;
    private AppPref appPref;
    private RadioGroup radioGroup;
    private RadioGroup radioGroupSelect;
    public static String selectedOption="fan";
    private RadioButton rdbtnDesigner, rdbtnFan, rdbtnCorporate;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        setContentView(R.layout.activity_signin);
        setViewReferences();
        setViewListeners();
        languageSelection();
        changeStatusBarColor(getResources().getColor(R.color.colorFanText));
    }

    private void init() {
        appPref = new AppPref(this);
    }

    @Override
    protected void setViewListeners() {
        btnLogin.setOnClickListener(this);
        tvSignUp.setOnClickListener(this);
        chtvFan.setOnClickListener(this);
        chtvCorporate.setOnClickListener(this);
        chtvDesigner.setOnClickListener(this);
        tvForgotPassword.setOnClickListener(this);
    }

    @Override
    protected void setViewReferences() {
        edtEmail = (EditText) findViewById(R.id.act_signin_edt_email);
        edtPassword = (EditText) findViewById(R.id.act_signin_edt_password);
        tvForgotPassword = (TextView) findViewById(R.id.act_signin_forgot_password);
        btnLogin = (Button) findViewById(R.id.act_signin_btn_login);
        tvSignUp = (TextView) findViewById(R.id.act_signin_tv_signup);
        chtvFan = (CheckedTextView) findViewById(R.id.act_signin_chtv_fan);
        chtvDesigner = (CheckedTextView) findViewById(R.id.act_signin_chtv_designer);
        chtvCorporate = (CheckedTextView) findViewById(R.id.act_signin_chtv_corporate);
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
            CommonSnackBar.show(edtEmail, getString(R.string.msg_email_cannot_be_empty), Snackbar.LENGTH_SHORT);
            return false;
        } else if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            String password = this.edtPassword.getText().toString();
            if (password.isEmpty()) {
                this.edtPassword.requestFocus();
                CommonSnackBar.show(edtEmail, getString(R.string.msg_password_cannot_be_empty), Snackbar.LENGTH_SHORT);
                return false;
            }
        } else {
            CommonSnackBar.show(edtEmail, getString(R.string.msg_email_not_valid), Snackbar.LENGTH_SHORT);
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
                startNewActivity(SignUpActivity.class);
                break;
            case R.id.act_signin_chtv_fan:
                if (((CheckedTextView) v).isChecked()) {
                    fanBackgroundChange(v);
                } else {
                    chtvFan.setTextColor(getResources().getColor(R.color.edt_background_tint));
                    ((CheckedTextView) v).setChecked(true);
                }
                break;
            case R.id.act_signin_chtv_designer:
                if (((CheckedTextView) v).isChecked()) {
                    designerBackgroundChange(v);
                } else {
                    chtvDesigner.setTextColor(getResources().getColor(R.color.edt_background_tint));
                    ((CheckedTextView) v).setChecked(true);
                }
                break;
            case R.id.act_signin_chtv_corporate:
                if (((CheckedTextView) v).isChecked()) {
                    corporatefanBackgroundChange(v);
                } else {
                    chtvCorporate.setTextColor(getResources().getColor(R.color.edt_background_tint));
                    ((CheckedTextView) v).setChecked(true);
                }
                break;
            case R.id.act_signin_forgot_password:
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
        final CommonProgressBar commonProgressBar = new CommonProgressBar(this);
        commonProgressBar.show();
       Call<ApiResponse> apiResponseCall=RestClient.getService().userLogin(edtEmail.getText().toString(),edtPassword.getText().toString(),"android","1233",deviceId,selectedOption);
        apiResponseCall.enqueue(new ApiCall(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                commonProgressBar.hide();
                if (apiResponse.status) {
                    appPref.saveUserObject(apiResponse.paylpad.getData());
                    appPref.setLoginFlag(true);
                    startNewActivity(FanHomeActivity.class);
                } else
                    CommonSnackBar.show(edtEmail, apiResponse.error.message, Snackbar.LENGTH_SHORT);
            }

            @Override
            public void onFail(Call<ApiResponse> call, Throwable t) {
                commonProgressBar.hide();
            }
        });
    }

    private void openBottomSheet() {

        /*final BottomSheetDialogFragment mBottomSheetDialog = new BottomSheetDialogFragment(getActivity());
        View sheetView = getActivity().getLayoutInflater().inflate(R.layout.forgot_password, null);
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mBottomSheetDialog.show();
        final EditText edtEmail = (EditText) sheetView.findViewById(R.id.forgot_password_edt_email);
        Button sendMail = (Button) sheetView.findViewById(R.id.sendMail);
        sendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
                if (isForgetEmailValidate(edtEmail)) {
                    apiForgotPassword(edtEmail, mBottomSheetDialog);
                }
            }
        });*/

        ShareOneTouchAlertNewBottom dialogFragment = new  ShareOneTouchAlertNewBottom(new ShareOneTouchAlertNewBottom.DialogListener() {
            @Override
            public void listener(Dialog dialog, EditText editText) {
                if (isForgetEmailValidate(editText)) {
                    apiForgotPassword(editText, (BottomSheetDialog) dialog);
                }
            }




        });
        dialogFragment.show(getSupportFragmentManager(), "Forgot password");

    }

    private void apiForgotPassword(EditText edtEmail, final BottomSheetDialog mBottomSheetDialog) {

        final CommonProgressBar commonProgressBar = new CommonProgressBar(this);
        commonProgressBar.show();
        Call<ApiResponse> apiResponseCall = RestClient.getService().forgotPassword(edtEmail.getText().toString());
        apiResponseCall.enqueue(new ApiCall(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                commonProgressBar.hide();
                if (apiResponse.status) {
                    mBottomSheetDialog.dismiss();
                    CommonSnackBar.show(SigninActivity.this.edtEmail, apiResponse.message.message, Snackbar.LENGTH_SHORT);
                } else {
                    CommonSnackBar.show(SigninActivity.this.edtEmail, apiResponse.error.message, Snackbar.LENGTH_SHORT);
                }
            }

            @Override
            public void onFail(Call<ApiResponse> call, Throwable t) {
                commonProgressBar.hide();
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

    private void fanBackgroundChange(View v) {
        chtvFan.setTextColor(getResources().getColor(R.color.colorFanText));
        bgLl.setBackgroundResource(R.drawable.gradient_bg_fan_hdpi);
        chtvCorporate.setChecked(false);
        chtvDesigner.setChecked(false);
        chtvCorporate.setTextColor(getResources().getColor(R.color.edt_background_tint));
        chtvDesigner.setTextColor(getResources().getColor(R.color.edt_background_tint));
        //  btnLogin.setTextAppearance(this, R.style.btnstyleFan);
        btnLogin.setBackgroundDrawable(getResources().getDrawable(R.drawable.fan_btn_background));
        tvSignUp.setTextColor(getResources().getColor(R.color.colorFanText));


    }

    private void designerBackgroundChange(View v) {
        chtvDesigner.setTextColor(getResources().getColor(R.color.colorDesignerText));
        bgLl.setBackgroundResource(R.drawable.gradient_bg_des_hdpi);
        chtvCorporate.setChecked(false);
        chtvFan.setChecked(false);
        chtvFan.setTextColor(getResources().getColor(R.color.edt_background_tint));
        chtvCorporate.setTextColor(getResources().getColor(R.color.edt_background_tint));
        btnLogin.setBackgroundDrawable(getResources().getDrawable(R.drawable.designer_btn_background));
        tvSignUp.setTextColor(getResources().getColor(R.color.colorDesignerText));


    }

    private void corporatefanBackgroundChange(View v) {
        chtvCorporate.setTextColor(getResources().getColor(R.color.corporateBtnBackground));
        bgLl.setBackgroundResource(R.drawable.gradient_bg_hdpi);
        chtvFan.setChecked(false);
        chtvDesigner.setChecked(false);
        chtvDesigner.setTextColor(getResources().getColor(R.color.edt_background_tint));
        chtvFan.setTextColor(getResources().getColor(R.color.edt_background_tint));
        btnLogin.setTextAppearance(this, R.style.btnstyleCorporate);
        btnLogin.setBackgroundDrawable(getResources().getDrawable(R.drawable.corporate_btn_background));
        tvSignUp.setTextColor(getResources().getColor(R.color.colorCorporateText));


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
                        changeStatusBarColor(getResources().getColor(R.color.colorstatusBarCorporate));
                }else if(checkedId==R.id.rdbtnDesigner){
                    selectedOption="designer";
                    rdbtnDesigner.setTextColor(getResources().getColor(R.color.colorDesignerText));
                    bgLl.setBackgroundResource(R.drawable.gradient_bg_des_hdpi);
                    rdbtnCorporate.setTextColor(getResources().getColor(R.color.edt_background_tint));
                    rdbtnFan.setTextColor(getResources().getColor(R.color.edt_background_tint));
                    btnLogin.setBackgroundDrawable(getResources().getDrawable(R.drawable.designer_btn_background));
                    tvSignUp.setTextColor(getResources().getColor(R.color.colorDesignerText));
                    changeStatusBarColor(getResources().getColor(R.color.colorstatusBarDesigner));

                }else if(checkedId==R.id.rdbtnFan){
                    selectedOption="fan";
                    rdbtnFan.setTextColor(getResources().getColor(R.color.colorFanText));
                    bgLl.setBackgroundResource(R.drawable.gradient_bg_fan_hdpi);
                    btnLogin.setBackgroundDrawable(getResources().getDrawable(R.drawable.fan_btn_background));
                    tvSignUp.setTextColor(getResources().getColor(R.color.colorFanText));
                    rdbtnCorporate.setTextColor(getResources().getColor(R.color.edt_background_tint));
                    rdbtnDesigner.setTextColor(getResources().getColor(R.color.edt_background_tint));
                    changeStatusBarColor(getResources().getColor(R.color.colorstatusBarFan));

                }

            }
        });
    }


}
