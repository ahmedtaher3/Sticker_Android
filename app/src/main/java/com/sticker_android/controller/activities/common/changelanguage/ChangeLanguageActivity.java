package com.sticker_android.controller.activities.common.changelanguage;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.sticker_android.R;
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.controller.activities.common.signin.SigninActivity;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.sharedpref.AppPref;

import java.util.Locale;

/**
 * Class is used to change the Language of app
 */
public class ChangeLanguageActivity extends AppBaseActivity implements View.OnClickListener{

    private RadioGroup radioGroup;
    private RadioButton rdbEnglish,rdbArabic;
    private    AppPref appPref;
    private Button btnEnglish,btnArabic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_language);
        changeStatusBarColor(Color.parseColor("#38525f"));
        setViewReferences();
        setViewListeners();
        appPref();

    }

    private void appPref() {
        appPref=new AppPref(this);
    }


    @Override
    protected void setViewListeners() {

      btnArabic.setOnClickListener(this);
        btnEnglish.setOnClickListener(this);
    }

    @Override
    protected void setViewReferences() {
       btnEnglish   =  (Button) findViewById(R.id.act_change_lang_btn_english);
        btnArabic   =  (Button) findViewById(R.id.act_change_lang_btn_arabic);
    }

    @Override
    protected boolean isValidData() {
        return false;
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
        Intent intent = new Intent(this, SigninActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_animation_enter,
                R.anim.activity_animation_exit);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.act_change_lang_btn_english:
                appPref.setLanguage(1);
                appPref.setLanguageStatus(true);
                Utils.changeLanguage("en",this,SigninActivity.class);
                //setLocale("en");
                break;
            case R.id.act_change_lang_btn_arabic:
                appPref.setLanguage(2);
                appPref.setLanguageStatus(true);
                Utils.changeLanguage("ar",this,SigninActivity.class);
               // setLocale("ar");
                break;
        }
    }
}
