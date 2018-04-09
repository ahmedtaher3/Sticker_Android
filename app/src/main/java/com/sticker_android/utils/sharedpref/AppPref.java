package com.sticker_android.utils.sharedpref;

/**
 * Created by user on 24/7/17.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.sticker_android.model.User;

/**
 * Make Class shared Preference to set and get the response.
 */
public class AppPref {

    private Context context;

    public AppPref(Context context) {
        this.context = context;
    }


    public User getUserInfo()
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

        String userInfo = settings.getString("user_data", null);
        User user = new Gson().fromJson(userInfo,
                User.class);
        if(user ==null)
        {
            user =new User();
        }
        return user;
    }

    public void saveUserObject(User user) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = sp.edit();
        prefsEditor.putString("user_data", new Gson().toJson(user));
        prefsEditor.apply();
    }

  /*
    * setLoginFlag() method is used to set Login Flag value in SharedPreference.
     * */

    public void setLoginFlag( boolean value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("login", value);
        editor.commit();
    }

    /*
    * getLoginFlag() method is used to get Login Flag value from SharedPreference.
     * */

    public boolean getLoginFlag(boolean defVal) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean("login", defVal);
    }


    /*
    * setLanguage() method is used to set Login Flag value in SharedPreference.
     * */

    public void setLanguage( int value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("language", value);
        editor.commit();
    }
     /*
    * getLanguage() method is used to get Login Flag value from SharedPreference.
     * */

    public int getLanguage(int defVal) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt("language", defVal);
    }

/*
    * setLoginFlag() method is used to set Login Flag value in SharedPreference.
     * */

    public void setLanguageStatus( boolean value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("languageShow", value);
        editor.commit();
    }

    /*
    * getLoginFlag() method is used to get Login Flag value from SharedPreference.
     * */

    public boolean getLanguageStatus(boolean defVal) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean("languageShow", defVal);
    }


}