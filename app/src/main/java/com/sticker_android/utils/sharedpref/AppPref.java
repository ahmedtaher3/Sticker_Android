package com.sticker_android.utils.sharedpref;

/**
 * Created by user on 24/7/17.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.sticker_android.model.User;
import com.sticker_android.model.corporateproduct.Category;
import com.sticker_android.model.data.CategoryDataWrapper;

import java.util.ArrayList;

/**
 * Make Class shared Preference to set and get the response.
 */
public class AppPref {

    private Context context;
    public static final String SHARED_PREFS_NAME = "sport_widget";

    public AppPref(Context context) {
        this.context = context;
    }

    public User getUserInfo() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

        String userInfo = settings.getString("user_data", null);
        User user = new Gson().fromJson(userInfo, User.class);
        if(user ==null) {
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

    public void saveCategoryList(ArrayList<Category> categories) {
        CategoryDataWrapper categoryDataWrapper = new CategoryDataWrapper();
        categoryDataWrapper.categories = categories;
        SharedPreferences sp = context.getSharedPreferences(SHARED_PREFS_NAME, 0);
        SharedPreferences.Editor prefsEditor = sp.edit();
        prefsEditor.putString("category_data", new Gson().toJson(categoryDataWrapper));
        prefsEditor.commit();
    }

    public ArrayList<Category> getCategoryList(){
        SharedPreferences settings = context.getSharedPreferences(SHARED_PREFS_NAME, 0);
        String categoryInfo = settings.getString("category_data", null);
        CategoryDataWrapper categoryDataWrapper = new Gson().fromJson(categoryInfo, CategoryDataWrapper.class);

        if(categoryDataWrapper == null
                || (categoryDataWrapper != null && categoryDataWrapper.categories != null
                && categoryDataWrapper.categories.size() == 0)){
            return new ArrayList<>();
        }
        else{
            return categoryDataWrapper.categories;
        }
    }

    public void clearCategoryList(){
        SharedPreferences sp = context.getSharedPreferences(SHARED_PREFS_NAME, 0);
        SharedPreferences.Editor prefsEditor = sp.edit();
        prefsEditor.remove("category_data");
        prefsEditor.commit();
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



    /**
     * This method is to save the new messages count
     *@param value
     */
    public  void saveNewMessagesCount( int value){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("messageCount", value);
        editor.commit();

    }

    /**
     * This method is to get the new messages count
     *@return message count
     */
    public  int getNewMessagesCount(int defVal){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt("messageCount", defVal);
    }

}