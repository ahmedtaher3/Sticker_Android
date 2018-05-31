package com.sticker_android.utils.version;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 31/5/18.
 */

public class GetLatestVersion {

    private final String TAG = GetLatestVersion.class.getSimpleName();
    private Context mContext;
    private String currentVersion = "";
    private VersionListener versionListener;

    public interface VersionListener{
        void versionCheck(boolean updated);
    }

    public GetLatestVersion(Context context){
        this.mContext = context;
    }

    public GetLatestVersion setVersionListener(VersionListener listener){
        this.versionListener = listener;
        return this;
    }

    public void execute(){
        try {
            currentVersion = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        checkVersionUpdate();
    }

    private void checkVersionUpdate(){
        checkAppVersion();
    }
    private void checkAppVersion() {
        Call<ApiResponse> apiResponseCallVersion = RestClient.getService().checkVersion("app_version");

        apiResponseCallVersion.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.body().status) {
                    if (response.body().paylpad != null) {
                        String version = response.body().paylpad.appVersion.appVersion;
                        if (!version.equalsIgnoreCase(Utils.getVersionInfo(mContext))) {
                            if(versionListener != null){
                                versionListener.versionCheck(true);
                            }
                            mContext.startActivity(new Intent(mContext, GooglePlayUpdateActivity.class));
                        }
                        else{
                            versionListener.versionCheck(false);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {

            }
        });
    }

}