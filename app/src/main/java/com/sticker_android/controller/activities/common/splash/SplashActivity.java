package com.sticker_android.controller.activities.common.splash;

import android.os.Bundle;
import android.os.Handler;

import com.sticker_android.R;
import com.sticker_android.constant.AppConstant;
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.controller.activities.common.signin.SignInActivity;

public class SplashActivity extends AppBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        waitForFewSecond();
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
            startNewActivity(SignInActivity.class);
            finish();
        }
    }
}
