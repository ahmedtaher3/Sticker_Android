package com.sticker_android.controller.activities.common.splash;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;

import com.sticker_android.R;
import com.sticker_android.constant.AppConstant;
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.controller.activities.common.changelanguage.ChangeLanguageActivity;
import com.sticker_android.controller.activities.common.signin.SigninActivity;
import com.sticker_android.controller.activities.corporate.CorporateProfileActivity;
import com.sticker_android.controller.activities.corporate.home.CorporateHomeActivity;
import com.sticker_android.controller.activities.designer.home.DesignerHomeActivity;
import com.sticker_android.controller.activities.fan.home.FanHomeActivity;
import com.sticker_android.controller.notification.LocalNotification;
import com.sticker_android.model.User;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.sharedpref.AppPref;
import com.sticker_android.utils.version.GetLatestVersion;
import com.sticker_android.view.BadgeUtils;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppBaseActivity {

    private AppPref appPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        init();

        changeStatusBarColor(Color.BLACK);
        BadgeUtils.setBadge(this, 0);
        LocalNotification.clearNotifications(this);
        getRandomAdApi();
        waitForFewSecond();
        new GetLatestVersion(this).setVersionListener(new GetLatestVersion.VersionListener() {
            @Override
            public void versionCheck(boolean updated) {
                if(!updated){
                    waitForFewSecond();
                }
            }
        }).execute();
    }

    private void init() {
        appPref = new AppPref(this);
        appPref.clearCategoryList();
    }

    private void setSelectedLangage(Class<?> cls) {

        int language = appPref.getLanguage(1);
        // setLocale(language,cls);
        if (language == 2) {
            Utils.changeLanguage("ar", this, cls);

        } else {
            Utils.changeLanguage("en", this, cls);

        }
    }

    /**
     * Method is used for waiting  few second to start Main App
     */
    private void waitForFewSecond() {
        new Handler().postDelayed(new SplashRunnable(), AppConstant.SPLASH_TIMER_WAIT);
    }

    @Override
    protected void setViewListeners() {

    }

    @Override
    protected void setViewReferences() {

    }

    @Override
    protected boolean isValidData() {
        return false;
    }

    /**
     * Class is used as a seperate thread  for waiting few seconds
     */
    class SplashRunnable implements Runnable {
        @Override
        public void run() {

            if (appPref.getLoginFlag(false)) {
                moveToActivity();
                finish();
            } else {
                setSelectedLangage(ChangeLanguageActivity.class);
                finish();
            }
        }
    }

    /**
     * setLocale() set the localization configuration according to your selected language.
     *
     * @param lang
     * @param cls
     */

    public void setLocale(int lang, Class<?> cls) {
        Locale myLocale = null;
        if (lang == 2) {
            myLocale = new Locale("ar");
        } else {
            myLocale = new Locale("en");
        }
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        startNewActivity(cls);
    }

    public void moveToActivity() {
        User user = appPref.getUserInfo();
        if (user.getUserType().equals("corporate")) {
            if (user.getCompanyName() != null && !user.getCompanyName().isEmpty()) {
                //   startNewActivity(CorporateHomeActivity.class);
                setSelectedLangage(CorporateHomeActivity.class);
            } else {
                // startNewActivity(CorporateProfileActivity.class);
                setSelectedLangage(CorporateProfileActivity.class);
            }
        } else if (user.getUserType().equals("fan")) {
            // startNewActivity(FanHomeActivity.class);
            setSelectedLangage(FanHomeActivity.class);
        } else if (user.getUserType().equals("designer")) {
            // startNewActivity(DesignerHomeActivity.class);
            setSelectedLangage(DesignerHomeActivity.class);
        }

    }


    private void getRandomAdApi() {

        if (appPref.getLoginFlag(false)) {
            User adObj = appPref.getUserInfo();
            Call<ApiResponse> apiResponseCall = RestClient.getService().getRandomFeaturedProduct(adObj.getLanguageId(), adObj.getAuthrizedKey(), adObj.getId(), "product");

            apiResponseCall.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body().status) {
                            appPref.saveAds(response.body().paylpad.product);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {

                }
            });




       /*    apiResponseCall.enqueue(new ApiCall(getActivity()) {
                @Override
                public void onSuccess(ApiResponse apiResponse) {
                    if (apiResponse.status) {
                        appPref.saveAds(apiResponse.paylpad.product);
                    }
                }

                @Override
                public void onFail(Call<ApiResponse> call, Throwable t) {

                }
            });
        */
        }
    }

}
