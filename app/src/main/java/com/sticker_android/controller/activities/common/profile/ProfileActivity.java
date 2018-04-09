package com.sticker_android.controller.activities.common.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
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
import com.sticker_android.controller.activities.designer.home.DesignerHomeActivity;
import com.sticker_android.controller.activities.corporate.home.CorporateHomeActivity;
import com.sticker_android.controller.activities.fan.home.FanHomeActivity;
import com.sticker_android.model.User;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.AppConstants;
import com.sticker_android.utils.CommonSnackBar;
import com.sticker_android.utils.ProgressDialogHandler;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.sharedpref.AppPref;

import retrofit2.Call;

import static com.theartofdev.edmodo.cropper.CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE;


public class ProfileActivity extends AppBaseActivity implements View.OnClickListener {

    private final String TAG = ProfileActivity.class.getSimpleName();
    private EditText firstName, lastName;
    private RelativeLayout rlBgProfile;
    private LinearLayout llCorporate;
    private AppPref appPref;
    private EditText edtCompanyName, edtCompanyAddress, edtProfileFirstName;
    private EditText edtProfileLastName, edtProfileEmail;
    private Button btnSubmit;
    private User user;


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
        rlBgProfile = (RelativeLayout) findViewById(R.id.bgProfile);
        llCorporate = (LinearLayout) findViewById(R.id.llCorporate);
        edtCompanyName = (EditText) findViewById(R.id.act_profile_edt_company_name);
        edtCompanyAddress = (EditText) findViewById(R.id.act_profile_edt_company_address);
        edtProfileFirstName = (EditText) findViewById(R.id.act_profile_edt_first_name);
        edtProfileLastName = (EditText) findViewById(R.id.act_profile_edt_last_name);
        edtProfileEmail = (EditText) findViewById(R.id.act_profile_edt_email);
        btnSubmit = (Button) findViewById(R.id.act_profile_btn_register);
    }

    @Override
    protected boolean isValidData() {
        String firstName = this.edtProfileFirstName.getText().toString().trim();
        String lastName = this.edtProfileLastName.getText().toString().trim();
        if (firstName.isEmpty()) {
            CommonSnackBar.show(edtProfileFirstName, getString(R.string.first_name_cannot_be_empty), Snackbar.LENGTH_SHORT);
            this.edtProfileFirstName.requestFocus();
            return false;
        } else if (lastName.isEmpty()) {
            CommonSnackBar.show(edtProfileLastName, getString(R.string.last_name_cannot_be_empty), Snackbar.LENGTH_SHORT);
            this.edtProfileLastName.requestFocus();
            return false;
        }
        String email = this.edtProfileEmail.getText().toString().trim();
        if (email.isEmpty()) {
            CommonSnackBar.show(edtProfileEmail, getString(R.string.msg_email_cannot_be_empty), Snackbar.LENGTH_SHORT);
            this.edtProfileEmail.requestFocus();
            return false;
        } else if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            CommonSnackBar.show(edtProfileEmail, getString(R.string.msg_email_not_valid), Snackbar.LENGTH_SHORT);
            this.edtProfileEmail.requestFocus();
            return false;
        }

        return true;
    }


    private void init() {
        Log.e(TAG, "Inside init() method");
        appPref = new AppPref(this);
        user = appPref.getUserInfo();
        if (user.getUserType() != null)
            Log.e(TAG, "User type => " + user.getUserType());
        switch (user.getUserType()) {
            case "fan":
                rlBgProfile.setBackground(getResources().getDrawable(R.drawable.gradient_bg_fan_hdpi));
                llCorporate.setVisibility(View.GONE);
                /*changeStatusBarColor(getResources().getColor(R.color.colorstatusBarFan));*/
                setStatusBarGradiant(this, AppConstants.FAN);
                break;
            case "designer":
                rlBgProfile.setBackground(getResources().getDrawable(R.drawable.gradient_bg_des_hdpi));
                llCorporate.setVisibility(View.GONE);
                /*changeStatusBarColor(getResources().getColor(R.color.colorstatusBarDesigner));*/
                setStatusBarGradiant(this, AppConstants.DESIGNER);
                break;
            case "corporate":
                rlBgProfile.setBackground(getResources().getDrawable(R.drawable.gradient_bg_hdpi));
                llCorporate.setVisibility(View.VISIBLE);
                /*changeStatusBarColor(getResources().getColor(R.color.colorstatusBarCorporate));*/
                setStatusBarGradiant(this, AppConstants.CORPORATE);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.act_profile_btn_register:
                if (isValidData()) {
                    updateProfileApi();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CROP_IMAGE_ACTIVITY_REQUEST_CODE){

        }
    }

    private void updateProfileApi() {
        if (user.getId() != null) {
            final ProgressDialogHandler progressDialogHandler = new ProgressDialogHandler(getActivity());
            progressDialogHandler.show();
            Call<ApiResponse> apiResponseCall = RestClient.getService().updateProfile(user.getId(), edtCompanyName.getText().toString(),
                    "", edtCompanyAddress.getText().toString(), edtProfileFirstName.getText().toString(), edtProfileLastName.getText().toString(),
                    user.getEmail(), user.getUserType());
            apiResponseCall.enqueue(new ApiCall(getActivity()) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    progressDialogHandler.hide();
                    if (apiResponse.status) {
                        appPref.saveUserObject(apiResponse.paylpad.getData());
                        appPref.setLoginFlag(true);
                        moveToNext();
                    } else {
                        CommonSnackBar.show(edtCompanyAddress, apiResponse.error.message, Snackbar.LENGTH_SHORT);
                    }
                }

                @Override
                public void onFail(Call<ApiResponse> call, Throwable t) {
                    progressDialogHandler.hide();
                }
            });

        }
    }


    private void moveToNext() {
        User user = appPref.getUserInfo();
        if (user.getUserType().equals("corporate")) {
            startNewActivity(CorporateHomeActivity.class);
        } else if (user.getUserType().equals("fan")) {
            startNewActivity(FanHomeActivity.class);
        } else if (user.getUserType().equals("designer")) {
            startNewActivity(DesignerHomeActivity.class);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            appPref.setLoginFlag(false);
            startNewActivity(SigninActivity.class);
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
