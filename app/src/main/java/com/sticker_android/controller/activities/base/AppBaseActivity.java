package com.sticker_android.controller.activities.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.sticker_android.R;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by user on 22/3/18.
 */

public abstract class AppBaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      /*  getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);*/
    }

    /**
     * Method is used to set the listeners on views
     */
    protected abstract void setViewListeners();

    /**
     * Method is used to set the references of views
     */
    protected abstract void setViewReferences();

    abstract protected boolean isValidData();


    /**
     * Find Current activity object.
     *
     * @return
     */
    protected AppBaseActivity getActivity() {
        return this;
    }


    /** Method is used to start a new Activity
     * @param cls Name of the class which you want to forward
     */
    protected void startNewActivity(Class<?> cls){
        startActivity(new Intent(getActivity(),cls));
        overridePendingTransition(R.anim.activity_animation_enter,
                R.anim.activity_animation_exit);

    }
    /** Method is used to start a new Activity
     * @param intent Data which you want to forward to next Activity
     */

    protected void startNewActivityWithData(Intent intent){
        startActivity(intent);
        overridePendingTransition(R.anim.activity_animation_enter,
                R.anim.activity_animation_exit);

    }

    /** Method is used to start a new Activity with Result
     * @param cls Name of the class which you want to forward
     * @param bundle  Data which you want to forward to next Activity
     * @param requestCode request code for Activity  so you can get the result
     */
    protected void startActivityWithResult(Class<?> cls, Bundle bundle, int requestCode) {
        Intent intent = new Intent(getActivity(), cls);
        if (bundle != null) {
            intent.putExtra("data", bundle);
        }
        startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.activity_animation_enter, R.anim.activity_animation_exit);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
