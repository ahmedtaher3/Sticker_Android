
package com.sticker_android.network;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

import com.sticker_android.R;
import com.sticker_android.controller.activities.common.signin.SigninActivity;
import com.sticker_android.utils.sharedpref.AppPref;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ankit on 9/1/17
 */

public abstract class ApiCall implements Callback<ApiResponse> {

    private Activity mActivity;
    private boolean pdStatus, hideApiError;
    private Dialog mProgressDialog;
    private Call<ApiResponse> responseCall;

    public ApiCall(Activity mActivity) {
        this(mActivity, false);
    }

    public ApiCall(Activity mActivity, Call<ApiResponse> responseCall) {
        this(mActivity, true);
        this.responseCall = responseCall;
        if (pdStatus) {
            //  mProgressDialog = Utils.apiCallDialog(mActivity, responseCall);
        }
    }

    public ApiCall(Activity mActivity, int hideErrorFlag) {
        this.mActivity = mActivity;
        this.hideApiError = hideErrorFlag != -1 ? true : false;
    }

    private ApiCall(Activity mActivity, boolean pdStatus) {
        this.mActivity = mActivity;
        this.pdStatus = pdStatus;
    }

    @Override
    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
        if (pdStatus) {
            if (!mActivity.isFinishing())
                mProgressDialog.dismiss();
        }
        if (response.isSuccessful()) {
            ApiResponse apiResponse = response.body();
            if (apiResponse.responseCode == 1001) {
                //session expire
                //redirect to login screen
                expireSession(apiResponse);
            } else {
                //continue callback
                onSuccess(apiResponse);
            }
            if (apiResponse.authrizedStatus == 0) {
                //session expire
                //redirect to login screen
                expireSession(apiResponse);

            }
        }
        }

    public abstract void onSuccess(ApiResponse apiResponse);

    public abstract void onFail(Call<ApiResponse> call, Throwable t);

    @Override
    public void onFailure(Call<ApiResponse> call, Throwable t) {
        if (!hideApiError) {
            new ApiError(mActivity, t);
        }
        onFail(call, t);

        //  LogUtils.error("Request Cancel by user Manually");
    }

   /* */

    /***
     * user token is expire then user will automatically redirect to home screen
     *
     * @param apiResponse response
     */
    private void expireSession(ApiResponse apiResponse) {

  /* *//***
         * user token is expire then user will automatically redirect to home screen
         * @param apiResponse response
         */
        final AlertDialog alertDialog = new AlertDialog.Builder(mActivity).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Session Timeout !");
        alertDialog.setTitle("Your session has expired.");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new AppPref(mActivity).userLogout();
                Intent logoutIntent;
                logoutIntent = new Intent(mActivity, SigninActivity.class);
                logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                // logoutIntent.putExtra(Utils.REFRESH, true);
                mActivity.startActivity(logoutIntent);
                mActivity.overridePendingTransition(R.anim.activity_animation_enter, R.anim.activity_animation_exit);


            }
        });
        alertDialog.show();
    }

}
