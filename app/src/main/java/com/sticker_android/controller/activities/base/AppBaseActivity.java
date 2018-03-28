package com.sticker_android.controller.activities.base;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sticker_android.R;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by user on 22/3/18.
 */

public abstract class AppBaseActivity extends AppCompatActivity {

    public ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);*/
        /*changeStatusBarColor(getResources().getColor(R.color.colorDesignerText));*/
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

   /* public void changeStatusBarColor(int color){
        Window window = this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        //window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        //window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(color);
          //  window.setStatusBarColor(getResources().getColor(R.color.colorDesignerText));
        }

    }*/

    public void changeStatusBarColor(int statusBarColor){
        boolean shouldChangeStatusBarTintToDark=false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();

            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            if(Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP){
                if(statusBarColor == Color.WHITE){
                    window.setStatusBarColor(Color.parseColor("#CCCCCC"));
                }
                else{
                    window.setStatusBarColor(statusBarColor);
                }
            }
            else if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
                // finally change the color
                window.setStatusBarColor(statusBarColor);
            }

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                View decor = getWindow().getDecorView();
                if (shouldChangeStatusBarTintToDark) {
                    decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                } else {
                    // We want to change tint color to white again.
                    // You can also record the flags in advance so that you can turn UI back completely if
                    // you have set other flags before, such as translucent or full screen.
                    decor.setSystemUiVisibility(0);
                }
            }
        }
    }


}
