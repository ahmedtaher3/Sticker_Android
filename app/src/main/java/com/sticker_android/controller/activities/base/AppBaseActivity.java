package com.sticker_android.controller.activities.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sticker_android.R;
import com.sticker_android.application.StickerApp;
import com.sticker_android.controller.fragment.common.ProfileFragment;
import com.sticker_android.controller.fragment.fan.fanhome.FanHomeFragment;
import com.sticker_android.model.interfaces.NetworkPopupEventListener;
import com.sticker_android.utils.AppConstants;
import com.sticker_android.utils.AppLogger;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.sharedpref.AppPref;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
/**
 * Created by user on 22/3/18.
 */

public abstract class AppBaseActivity extends AppCompatActivity {

    public ImageLoader imageLoader = ImageLoader.getInstance();
    public DisplayImageOptions displayImageOptions;
    public ProfileFragment mProfileFragment;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);*/
        /*changeStatusBarColor(getResources().getColor(R.color.colorDesignerText));*/

        displayImageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();
        StickerApp.getInstance().setCurrentActivity(this);
        initApp();

    }

    /**
     * App Local
     */
    public void initApp() {

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


    /**
     * Method is used to start a new Activity
     *
     * @param cls Name of the class which you want to forward
     */
    protected void startNewActivity(Class<?> cls) {
        startActivity(new Intent(getActivity(), cls));
        overridePendingTransition(R.anim.activity_animation_enter,
                R.anim.activity_animation_exit);

    }

    /**
     * Method is used to start a new Activity
     *
     * @param intent Data which you want to forward to next Activity
     */

    protected void startNewActivityWithData(Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.anim.activity_animation_enter,
                R.anim.activity_animation_exit);

    }

    /**
     * Method is used to start a new Activity with Result
     *
     * @param cls         Name of the class which you want to forward
     * @param bundle      Data which you want to forward to next Activity
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

        AppLogger.debug(AppBaseActivity.class.getSimpleName(),"language base"+new AppPref(this).getLanguage(1));
        if (new AppPref(this).getLanguage(1)==2)
            Utils.setLocale(this, "ar");
        else
            Utils.setLocale(this, "en");
/*
        if (!new AppPref(this).getUserInfo().getLanguageId().equalsIgnoreCase("0")) {



        }else {
            Utils.setLocale(this, "en");
        }
*/
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

    public void changeStatusBarColor(int statusBarColor) {
        boolean shouldChangeStatusBarTintToDark = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();

            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
                if (statusBarColor == Color.WHITE) {
                    window.setStatusBarColor(Color.parseColor("#CCCCCC"));
                } else {
                    window.setStatusBarColor(statusBarColor);
                }
            } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                // finally change the color
                window.setStatusBarColor(statusBarColor);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setStatusBarGradiant(Activity activity, int userType) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            Drawable background = null;
            if (userType == AppConstants.FAN) {
                background = activity.getResources().getDrawable(R.drawable.fan_status_bar_gradient);
            } else if (userType == AppConstants.DESIGNER) {
                background = activity.getResources().getDrawable(R.drawable.designer_status_bar_gradient);
            } else {
                background = activity.getResources().getDrawable(R.drawable.corporate_status_bar_gradient);
            }
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setBackgroundDrawable(background);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Utils.hideKeyboard(this);
    }

    /**
     * will show custom popup for no internet connection
     *
     * @param context
     * @param parent
     * @param eventListener
     * @param requestCode
     */
    public void manageNoInternetConnectionLayout(Context context, final RelativeLayout parent, final NetworkPopupEventListener eventListener, final int requestCode) {

        final View view = LayoutInflater.from(context).inflate(R.layout.layout_no_internet_connection, null);
        if (parent != null && parent.getChildCount() > 0) {
            parent.removeAllViews();
        }
        parent.addView(view);

        LinearLayout llRetry = (LinearLayout) view.findViewById(R.id.llRetry);
        llRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (parent != null && parent.getChildCount() > 0) {
                    parent.removeAllViews();
                }
                eventListener.onRetryClickListener(requestCode);
            }
        });
    }

    public void updateCallbackMessage() {

    }


    public void clearBackStack() {
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = manager
                    .getBackStackEntryAt(0);
            manager.popBackStack(first.getId(),
                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

}
