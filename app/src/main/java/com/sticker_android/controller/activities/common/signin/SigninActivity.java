package com.sticker_android.controller.activities.common.signin;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sticker_android.R;
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.controller.activities.common.signup.SignUpActivity;
import com.sticker_android.utils.CommonSnackBar;
import com.sticker_android.utils.Utils;

public class SigninActivity extends AppBaseActivity implements View.OnClickListener {

    EditText edtEmail;
    private EditText edtPassword;
    private TextView tvForgotPassword;
    private Button btnLogin;
    private TextView tvSignUp;
    private CheckedTextView chtvFan,chtvDesigner,chtvCorporate;
    private LinearLayout bgLl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        setViewReferences();
        setViewListeners();
    }

    @Override
    protected void setViewListeners() {
         btnLogin.setOnClickListener(this);
        tvSignUp.setOnClickListener(this);
        chtvFan.setOnClickListener(this);
        chtvCorporate.setOnClickListener(this);
        chtvDesigner.setOnClickListener(this);
    }

    @Override
    protected void setViewReferences() {
        edtEmail=findViewById(R.id.act_signin_edt_email);
        edtPassword=findViewById(R.id.act_signin_edt_password);
        tvForgotPassword=findViewById(R.id.act_signin_forgot_password);
        btnLogin =findViewById(R.id.act_signin_btn_login);
        tvSignUp=findViewById(R.id.act_signin_tv_signup);
        chtvFan=findViewById(R.id.act_signin_chtv_fan);
        chtvDesigner=findViewById(R.id.act_signin_chtv_designer);
        chtvCorporate=findViewById(R.id.act_signin_chtv_corporate);
        bgLl=  findViewById(R.id.act_signin_bg_ll);
    }

    @Override
    protected boolean isValidData() {

        String email = this.edtEmail.getText().toString().trim();
        if (email.isEmpty()) {
            this.edtEmail.requestFocus();
            CommonSnackBar.show(edtEmail,getString(R.string.msg_email_cannot_be_empty), Snackbar.LENGTH_SHORT);
            return false;
        } else if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            String password = this.edtPassword.getText().toString();
            if (password.isEmpty()) {
                this.edtPassword.requestFocus();
                CommonSnackBar.show(edtEmail,getString(R.string.msg_password_cannot_be_empty), Snackbar.LENGTH_SHORT);
                return false;
            }
        }else {
            CommonSnackBar.show(edtEmail,getString(R.string.msg_email_not_valid), Snackbar.LENGTH_SHORT);
            return false;
        }
           return true;
    }

    @Override
    public void onClick(View v) {
        Utils.hideKeyboard(this);
        switch (v.getId()){
            case R.id.act_signin_btn_login:
                if(isValidData()){
                    Toast.makeText(getApplicationContext(),"Validated",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.act_signin_tv_signup:
                startNewActivity(SignUpActivity.class);
                break;
            case R.id.act_signin_chtv_fan:
                if(((CheckedTextView) v).isChecked()){
                    fanBackgroundChange(v);
                }else{
                    chtvFan.setTextColor(getResources().getColor(R.color.edt_background_tint));
                    ((CheckedTextView) v).setChecked(true);
                }
                break;
            case R.id.act_signin_chtv_designer:
                if(((CheckedTextView) v).isChecked()){
                    designerBackgroundChange(v);
                }else{
                    chtvDesigner.setTextColor(getResources().getColor(R.color.edt_background_tint));
                    ((CheckedTextView) v).setChecked(true);
                }
                break;
            case R.id.act_signin_chtv_corporate:
                if(((CheckedTextView) v).isChecked()){
                  corporatefanBackgroundChange(v);
                }else{
                    chtvCorporate.setTextColor(getResources().getColor(R.color.edt_background_tint));
                    ((CheckedTextView) v).setChecked(true);
                }
                break;
            default:

        }
    }
      private void fanBackgroundChange(View v){
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

    private void designerBackgroundChange(View v){
        chtvDesigner.setTextColor(getResources().getColor(R.color.colorDesignerText));
        bgLl.setBackgroundResource(R.drawable.gradient_bg_des_hdpi);
        chtvCorporate.setChecked(false);
        chtvFan.setChecked(false);
        chtvFan.setTextColor(getResources().getColor(R.color.edt_background_tint));
        chtvCorporate.setTextColor(getResources().getColor(R.color.edt_background_tint));
        btnLogin.setBackgroundDrawable(getResources().getDrawable(R.drawable.designer_btn_background));
        tvSignUp.setTextColor(getResources().getColor(R.color.colorDesignerText));


    }

    private void corporatefanBackgroundChange(View v){
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

}
