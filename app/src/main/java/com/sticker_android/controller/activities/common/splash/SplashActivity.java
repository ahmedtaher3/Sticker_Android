package com.sticker_android.controller.activities.common.splash;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;

import com.sticker_android.R;
import com.sticker_android.constant.AppConstant;
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.controller.activities.common.changelanguage.ChangeLanguageActivity;
import com.sticker_android.controller.activities.common.signin.SigninActivity;
import com.sticker_android.controller.activities.corporate.CorporateProfileActivity;
import com.sticker_android.controller.activities.designer.home.DesignerHomeActivity;
import com.sticker_android.controller.activities.corporate.home.CorporateHomeActivity;
import com.sticker_android.controller.activities.fan.home.FanHomeActivity;
import com.sticker_android.model.User;
import com.sticker_android.utils.sharedpref.AppPref;

import java.util.Locale;

public class SplashActivity extends AppBaseActivity {

    private AppPref appPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        init();
        waitForFewSecond();
        setSelectedLangage();
        changeStatusBarColor(getResources().getColor(R.color.colorFanText));

    }

    private void init() {
        appPref=new AppPref(this);
    }

    private void setSelectedLangage() {

       int language= appPref.getLanguage(0);
      //  setLocale(String.valueOf(language));
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
            if(appPref.getLoginFlag(false))
            {
                moveToActivity();
                finish();
            }else if(!appPref.getLanguageStatus(false)){
                startNewActivity(ChangeLanguageActivity.class);
                finish();
            }else {
                startNewActivity(SigninActivity.class);
                finish();

            }


        }
    }

    /**
     * setLocale() set the localization configuration according to your selected language.
     *
     * @param lang
     */

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        startNewActivity(SigninActivity.class);
    }

  public void  moveToActivity() {
      User user = appPref.getUserInfo();
      if (user.getUserType().equals("corporate")) {
          if (user.getCompanyName() != null&& !user.getCompanyName().isEmpty()){
              startNewActivity(CorporateHomeActivity.class);
      } else {
          startNewActivity(CorporateProfileActivity.class);
      }
    }
      else if (user.getUserType().equals("fan")) {
          startNewActivity(FanHomeActivity.class);
      } else if (user.getUserType().equals("designer")) {
          startNewActivity(DesignerHomeActivity.class);
      }

  }

}
