package com.sticker_android.controller.activities.common.changelanguage;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.sticker_android.R;
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.controller.activities.common.signin.SigninActivity;
import com.sticker_android.utils.sharedpref.AppPref;

import java.util.Locale;

public class ChangeLanguageActivity extends AppBaseActivity {

    private RadioGroup radioGroup;
    private RadioButton rdbEnglish,rdbArabic;
private    AppPref appPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_language);
        setViewReferences();
        setViewListeners();
        languageSelection();
        appPref();
        findViewById(R.id.act_change_lang_btn_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getSelectedLanguage();
                appPref.setLanguageStatus(true);
            }
        });
    }

    private void appPref() {
        appPref=new AppPref(this);
    }

    private void languageSelection() {

   radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
       @Override
       public void onCheckedChanged(RadioGroup group, int checkedId) {
           if(checkedId==R.id.rdbEnglish){

           }else if(checkedId==R.id.rdbArabic){

           }
       }
   });
    }

    private void getSelectedLanguage() {

        int selectedId = radioGroup.getCheckedRadioButtonId();
        if (selectedId == rdbEnglish.getId()) {
            setLocale("en");
            appPref.setLanguage(0);
        } else if (selectedId == rdbArabic.getId()) {
            setLocale("ar");
            appPref.setLanguage(1);
        }

    }
    @Override
    protected void setViewListeners() {

    }

    @Override
    protected void setViewReferences() {
        radioGroup = (RadioGroup) findViewById(R.id.myRadioGroup);
        rdbEnglish = (RadioButton) findViewById(R.id.rdbEnglish);
        rdbArabic = (RadioButton) findViewById(R.id.rdbArabic);
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
        startNewActivity(SigninActivity.class);
        finish();
    }

}
