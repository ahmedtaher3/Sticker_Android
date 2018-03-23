package com.sticker_android.controller.activities.common.signup;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sticker_android.R;
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.utils.CommonSnackBar;

public class SignUpActivity extends AppBaseActivity {

    private EditText edtFirstName,edtLastName,edtEmail,edtConfirmPassword,edtPassword;
    private Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    @Override
    protected void setViewListeners() {

    btnSignUp.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
     if(isValidData())
         Toast.makeText(getApplicationContext(),"Validated",Toast.LENGTH_SHORT).show();
    }
     });
    }

    @Override
    protected void setViewReferences() {
         edtFirstName=findViewById(R.id.act_signup_edt_first_name);
         edtLastName=  findViewById(R.id.act_signup_edt_last_name);
         edtEmail=     findViewById(R.id.act_signup_edt_email);
         edtPassword= findViewById(R.id.act_signup_edt_password);
         edtConfirmPassword=findViewById(R.id.act_signup_edt_confirm_password);
        btnSignUp=findViewById(R.id.act_signup_btn_register);
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
                    String confirmPassword = this.edtPassword.getText().toString().trim();
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
}
