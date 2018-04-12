
package com.sticker_android.network;

import android.app.Activity;
import android.app.Dialog;

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
            if (apiResponse.responseCode==1001) {
                //session expire
                //redirect to login screen
                //expireSession(apiResponse);
            } else {
                //continue callback
                onSuccess(apiResponse);
            }
        }
    }

    public abstract void onSuccess(ApiResponse apiResponse);

    public abstract void onFail(Call<ApiResponse> call, Throwable t);

    @Override
    public void onFailure(Call<ApiResponse> call, Throwable t) {
        if(!hideApiError){
            new ApiError(mActivity,t);
        }
        onFail(call,t);

        //  LogUtils.error("Request Cancel by user Manually");
    }

   /* *//***
     * user token is expire then user will automatically redirect to home screen
     * @param apiResponse response
     *//*
    private void expireSession(ApiResponse apiResponse) {
        SessionExpire.sessionExptireDialog(apiResponse,mActivity);
        //Monika//
        ChatHelper.setUnregisterUser(mActivity);
        ChatHelper.clearDataOnLogout(mActivity);                  //Clear local database on logout

        DialogUtils.showDialog(mActivity, apiResponse.error.getMessage(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new AppSharedPreference(mActivity).logout(mActivity);
                Intent logoutIntent;
                logoutIntent = new Intent(mActivity, LandingActivity.class);
                logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                // logoutIntent.putExtra(Utils.REFRESH, true);
                mActivity.startActivity(logoutIntent);
                //  mActivity.overridePendingTransition(R.anim.fragment_trans_left_in, R.anim.activity_trans_right_out);

            }
        });
    }*/}
