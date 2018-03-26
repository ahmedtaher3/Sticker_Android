package com.sticker_android.utils.sharedpref;

/**
 * Created by user on 24/7/17.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.sticker_android.model.UserData;

/**
 * Make Class shared Preference to set and get the response.
 */
public class AppPref {

    private Context context;

    public AppPref(Context context) {
        this.context = context;
    }


    public UserData getUserInfo()
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

        String userInfo = settings.getString("user_data", null);
        UserData userData = new Gson().fromJson(userInfo,
                UserData.class);
        if(userData==null)
        {
            userData=new UserData();
        }
        return userData;
    }

    public void saveUserObject(UserData userData) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = sp.edit();
        prefsEditor.putString("user_data", new Gson().toJson(userData));
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



}