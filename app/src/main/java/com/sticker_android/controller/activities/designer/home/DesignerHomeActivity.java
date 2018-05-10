package com.sticker_android.controller.activities.designer.home;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sticker_android.R;
import com.sticker_android.constant.AppConstant;
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.controller.activities.common.contest.ApplyCorporateContestActivity;
import com.sticker_android.controller.activities.common.signin.SigninActivity;
import com.sticker_android.controller.activities.fan.home.FanHomeActivity;
import com.sticker_android.controller.fragment.common.AccountSettingFragment;
import com.sticker_android.controller.fragment.common.ProfileFragment;
import com.sticker_android.controller.fragment.designer.DesignerHomeFragment;
import com.sticker_android.controller.fragment.designer.DesignerNotificationFragment;
import com.sticker_android.controller.fragment.designer.DesignerPendingContentFragment;
import com.sticker_android.controller.fragment.designer.DesignerReportFragment;
import com.sticker_android.controller.fragment.designer.contentapproval.DesignerContentApprovalFragment;
import com.sticker_android.controller.fragment.designer.contest.DesignerContestFragment;
import com.sticker_android.controller.notification.LocalNotification;
import com.sticker_android.model.User;
import com.sticker_android.model.notification.Acme;
import com.sticker_android.model.notification.AppNotification;
import com.sticker_android.model.notification.ContestObj;
import com.sticker_android.model.notification.NotificationApp;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiConstant;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.AppLogger;
import com.sticker_android.utils.ProgressDialogHandler;
import com.sticker_android.utils.UserTypeEnum;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.fragmentinterface.UpdateToolbarTitle;
import com.sticker_android.utils.sharedpref.AppPref;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.Locale;

import retrofit2.Call;

public class DesignerHomeActivity extends AppBaseActivity implements
        NavigationView.OnNavigationItemSelectedListener, ProfileFragment.OnFragmentProfileListener, UpdateToolbarTitle, AccountSettingFragment.ILanguageUpdate {
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private CoordinatorLayout mainView;
    private Toolbar toolbar;
    private AppPref appPref;
    private User user;
    private AlertDialog languageDialog;
    private boolean doubleBackToExitPressedOnce;
    private MenuItem mSelectedMenu;
    private FragmentManager mFragmentManager;
    boolean isCalled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*setStatusBarGradiant(this, AppConstants.DESIGNER);*/
        setContentView(R.layout.activity_designer_home);
        init();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setToolBarTitle(null);
        setToolbarBackground(toolbar);
        setSupportActionBar(toolbar);
        setViewReferences();
        setViewListeners();
        actionBarToggle(toolbar);
        changeStatusBarColor(getResources().getColor(R.color.colorstatusBarDesigner));
        setUserDataIntoNaviagtion();

        mFragmentManager = getSupportFragmentManager();
        replaceFragment(mFragmentManager, new DesignerHomeFragment());
        setToolBarTitle(getResources().getString(R.string.txt_home));
        setBadgeCount();
        initializeCountDrawer(appPref.getNewMessagesCount(0));
    }

    /**
     * Humberg notification dot visibility
     */
    private void setBadgeCount() {
        if (appPref.getNewMessagesCount(0) > 0)
            toolbar.findViewById(R.id.tv_nav_menu_badge).setVisibility(View.VISIBLE);
        else
            toolbar.findViewById(R.id.tv_nav_menu_badge).setVisibility(View.GONE);
    }

    private void setUserDataIntoNaviagtion() {
        View header = navigationView.getHeaderView(0);
        TextView tvUserName = (TextView) header.findViewById(R.id.tvUserName);
        TextView tvEmail = (TextView) header.findViewById(R.id.tvEmail);
        tvUserName.setText(user.getFirstName() + " " + user.getLastName());
        tvEmail.setText(user.getEmail());

        ImageView imageProfile = (ImageView) header.findViewById(R.id.imageViewProfile);
        LinearLayout linearLayout = (LinearLayout) header.findViewById(R.id.nav_header_common);
        linearLayout.setBackground(getResources().getDrawable(R.drawable.designer_profile_bg_hdpi));
        imageProfile.setImageResource(R.drawable.designer_hdpi);
        imageLoader.displayImage(ApiConstant.IMAGE_URl + user.getCompanyLogo(), imageProfile, displayImageOptions);
    }

    /**
     * Method is used to set the counter in notification tab
     *
     * @param count
     */
    private void initializeCountDrawer(int count) {
        int itemId = MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.nav_notification)).getId();
        TextView notificationCounter = (TextView) navigationView.getMenu().findItem(itemId).getActionView();
        notificationCounter.setGravity(Gravity.CENTER);
        notificationCounter.setTypeface(null, Typeface.BOLD);
        notificationCounter.setTextColor(getResources().getColor(R.color.colorFloatingCorporate));
        notificationCounter.setText(count > 0 ? String.valueOf(count) : null);
    }

    @Override
    protected boolean isValidData() {
        return false;
    }

    private void setBackground(Toolbar toolbar) {
        UserTypeEnum userTypeEnum = Enum.valueOf(UserTypeEnum.class, user.getUserType().toUpperCase());
        switch (userTypeEnum) {
            case FAN:
                toolbar.setBackground(getResources().getDrawable(R.drawable.gradient_bg_fan_hdpi));
                break;
            case DESIGNER:
                toolbar.setBackground(getResources().getDrawable(R.drawable.designer_header_hdpi));
                changeStatusBarColor(getResources().getColor(R.color.colorstatusBarDesigner));
                break;
            case CORPORATE:
                toolbar.setBackground(getResources().getDrawable(R.drawable.gradient_bg_hdpi));
                break;
        }
    }

    private void init() {
        appPref = new AppPref(this);
        user = appPref.getUserInfo();
    }

    private void setToolBarTitle(String title) {
        TextView textView = (TextView) toolbar.findViewById(R.id.tvToolbar);
        textView.setText((title != null && title.trim().length() != 0) ?
                title : getResources().getString(R.string.txt_home));
    }

    private void setToolbarBackground(Toolbar toolbar) {
        Drawable drawable = getBaseContext().getResources().getDrawable(R.drawable.designer_header_hdpi);
        if (Build.VERSION.SDK_INT >= 16) {
            toolbar.setBackground(drawable);
        } else {
            toolbar.setBackgroundDrawable(drawable);
        }
        if (user.getUserType() != null)
            setBackground(toolbar);
    }

    private void actionBarToggle(Toolbar toolbar) {
        final ImageView imageView = (ImageView) toolbar.findViewById(R.id.imv_nav_drawer_menu);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer != null) {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                mainView.setTranslationX(slideOffset * drawerView.getWidth());
                drawer.bringChildToFront(drawerView);
                drawer.requestLayout();
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                supportInvalidateOptionsMenu();

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                supportInvalidateOptionsMenu();
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_humberg));
                manageNavigationClickItem(mSelectedMenu);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
      /*  ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            public void onDrawerClosed(View view) {
                supportInvalidateOptionsMenu();
                manageNavigationClickItem(mSelectedMenu);
            }

            public void onDrawerOpened(View drawerView) {
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                mainView.setTranslationX(slideOffset * drawerView.getWidth());
                drawer.bringChildToFront(drawerView);
                drawer.requestLayout();
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();*/
        drawer.setScrimColor(Color.TRANSPARENT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
        setUserDataIntoNaviagtion();
    }

    @Override
    protected void setViewListeners() {
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void setViewReferences() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mainView = (CoordinatorLayout) findViewById(R.id.mainView);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        this.mSelectedMenu = item;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void manageNavigationClickItem(MenuItem item) {
        if (item == null) {
            return;
        }
        TextView textView = (TextView) toolbar.findViewById(R.id.tvToolbar);
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragmentClass = null;
        if (id == R.id.nav_home) {
            fragmentClass = new DesignerHomeFragment();
            textView.setText(getResources().getString(R.string.txt_home));
        } else if (id == R.id.nav_content_for_appproval) {
            fragmentClass = new DesignerContentApprovalFragment();
            //  textView.setText("Content Approval");
        } else if (id == R.id.nav_report) {
            fragmentClass = new DesignerReportFragment();
            textView.setText("Report");
        } else if (id == R.id.nav_contest) {
            fragmentClass = new DesignerContestFragment();
            textView.setText("Contest");
        } else if (id == R.id.nav_profile) {
            // startNewActivity(ViewProfileActivity.class);
            //  fragmentClass = new DesignerHomeFragment();
            fragmentClass = new ProfileFragment();
            textView.setText(getResources().getString(R.string.txt_myprofile));
        } else if (id == R.id.nav_account_setting) {
            fragmentClass = AccountSettingFragment.newInstance("", "");
            textView.setText(getResources().getString(R.string.txt_account_setting));
        } else if (id == R.id.nav_logout) {
            userLogout();
        } else if (id == R.id.nav_notification) {
            fragmentClass = DesignerNotificationFragment.newInstance();
            textView.setText(getResources().getString(R.string.txt_notifications));

        }
        setUserDataIntoNaviagtion();
        // Insert the fragment by replacing any existing fragment

        if (fragmentClass != null) {
            replaceFragment(mFragmentManager, fragmentClass);
        }
    }

    private void userLogout() {
        final ProgressDialogHandler progressDialogHandler = new ProgressDialogHandler(this);
        progressDialogHandler.show();

        Call<ApiResponse> apiResponseCall = RestClient.getService().userLogout(user.getLanguageId(), user.getAuthrizedKey(), user.getId());

        apiResponseCall.enqueue(new ApiCall(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                progressDialogHandler.hide();
                if (apiResponse.status) {
                    appPref.userLogout();
                    Intent intent = new Intent(getActivity(), SigninActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.activity_animation_enter,
                            R.anim.activity_animation_exit);
                    finish();
                }
            }

            @Override
            public void onFail(Call<ApiResponse> call, Throwable t) {
                progressDialogHandler.hide();
            }
        });
    }

    public void replaceFragment(FragmentManager manager, Fragment fragment) {
        String backStateName = fragment.getClass().getName();
        String fragmentTag = backStateName;

        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);
        if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null) {
            //"Fragment does not exist."
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.container_home, fragment, fragmentTag);
            ft.addToBackStack(backStateName);
            ft.commitAllowingStateLoss();
        } else {
            //"Fragment already exist."
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent != null) {
            boolean dataRefreshNeeded = intent.getBooleanExtra(AppConstant.DATA_REFRESH_NEEDED, false);

            if (dataRefreshNeeded) {
                //data of home has been changed
                Fragment fragment = mFragmentManager.findFragmentById(R.id.container_home);
                if (fragment instanceof DesignerHomeFragment) {
                    ((DesignerHomeFragment) fragment).updateAttachedVisibleFragment();
                } else if (fragment instanceof DesignerPendingContentFragment) {
                    ((DesignerPendingContentFragment) fragment).updateAttachedVisibleFragment();
                }
            }
        }
        LocalNotification.clearNotifications(this);
        if (intent.getExtras() != null) {
            if (intent.getExtras().getParcelable("obj") != null) {
                AppNotification appNotification = new AppNotification();
                appNotification = intent.getExtras().getParcelable("obj");
                if (appNotification.payload.acmeObj.status == 5)
                    setIntentData(appNotification);
            }
        }
/*
        if (isCalled) {
            isCalled=false;
            if (appPref.getLanguage(1) == 2)
                Utils.changeLanguage("ar", this, DesignerHomeActivity.class);
            else
                Utils.changeLanguage("en", this, DesignerHomeActivity.class);
        }
*/

    }

    private void setIntentData(AppNotification appNotification) {

        NotificationApp notificationApp = new NotificationApp();
        notificationApp.notificatinId = appNotification.payload.acmeObj.notificationId;
        Acme acme = new Acme();
        ContestObj contestObj = new ContestObj();
        contestObj.contestId = appNotification.payload.acmeObj.contestId;
        acme.contestObj = contestObj;
        notificationApp.acme = acme;
        Intent intentApplyContest = new Intent(this, ApplyCorporateContestActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(AppConstant.NOTIFICATION_OBJ, notificationApp);
        intentApplyContest.putExtras(bundle);
        startActivityForResult(intentApplyContest, AppConstant.INTENT_NOTIFICATION_CODE);

    }


    @Override
    public void onBackPressed() {
        Log.e("DesignerHomeActivity", "Count => " + getSupportFragmentManager().getBackStackEntryCount());
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (mFragmentManager.getBackStackEntryCount() == 1) {

            if (doubleBackToExitPressedOnce) {
                finish();
                return;
            } else {
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, R.string.press_again_to_exit, Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            }
        } else {
            TextView textView = (TextView) toolbar.findViewById(R.id.tvToolbar);
            textView.setText(getString(R.string.txt_home));
            mFragmentManager.popBackStackImmediate(DesignerHomeFragment.class.getName(), 0);
        }
    }

    private void openLanguageDialog() {

        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View languageDialogview = factory.inflate(R.layout.language_change_popup, null);
        if (languageDialog != null && languageDialog.isShowing()) {
            return;
        }

        languageDialog = new AlertDialog.Builder(getActivity()).create();
        languageDialog.setCancelable(false);
        languageDialog.setView(languageDialogview);
        languageDialog.show();
       /* languageDialog.getWindow()
                .findViewById(R.id.pop_up_language)
                .setBackgroundResource(android.R.color.transparent);*/
        languageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView imvLogoChangeLanguage = (ImageView) languageDialogview.findViewById(R.id.imvLogoChangeLanguage);
        final RadioGroup radioGroup = (RadioGroup) languageDialogview.findViewById(R.id.myRadioGroup);
        final RadioButton rdbEnglish = (RadioButton) languageDialogview.findViewById(R.id.rdbEnglish);
        final RadioButton rdbArabic = (RadioButton) languageDialogview.findViewById(R.id.rdbArabic);
        Button dialogButton = (Button) languageDialogview.findViewById(R.id.btn_update);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLanguage(radioGroup, rdbEnglish, rdbArabic);
                updatelanguageApi();
                languageDialog.dismiss();
            }
        });

        languageDialogview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (languageDialog != null)
                    languageDialog.dismiss();
            }
        });
    }

    private void updatelanguageApi() {
        final int language = appPref.getLanguage(0);
        Call<ApiResponse> apiResponseCall = RestClient.getService().changeLanguage(user.getId(), language, user.getAuthrizedKey());
        apiResponseCall.enqueue(new ApiCall(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.status) {
                    appPref.setLanguage(language);
                }
            }

            @Override
            public void onFail(Call<ApiResponse> call, Throwable t) {

            }
        });
    }

    private void updateLanguage(final RadioGroup radioGroup, final RadioButton rdbEnglish, final RadioButton rdbArabic) {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        if (selectedId == rdbEnglish.getId()) {
            setLocale("en");
            appPref.setLanguage(1);
        } else if (selectedId == rdbArabic.getId()) {
            setLocale("ar");
            appPref.setLanguage(2);
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
    }

    public void setProfileFragmentReference(ProfileFragment profileFragmentReference) {
        this.mProfileFragment = profileFragmentReference;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            mProfileFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void updatedata() {
        init();
        setUserDataIntoNaviagtion();
    }

    @Override
    public void updateToolbarTitle(String name) {
        TextView textView = (TextView) toolbar.findViewById(R.id.tvToolbar);
        textView.setText(name);

    }

    @Override
    public void updateCallbackMessage() {
        super.updateCallbackMessage();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setBadgeCount();
                initializeCountDrawer(appPref.getNewMessagesCount(0));

            }
        });

    }

    @Override
    public void updatelanguage(String language) {
        isCalled=true;
        if (language.equalsIgnoreCase("1")) {
            appPref.setLanguage(1);
            Utils.changeLanguage("en", this, DesignerHomeActivity.class);
            AppLogger.debug(FanHomeActivity.class.getSimpleName(), "language Account on update" + language);

        } else {
            appPref.setLanguage(2);
            Utils.changeLanguage("ar", this, DesignerHomeActivity.class);
            AppLogger.debug(FanHomeActivity.class.getSimpleName(), "language Account on update" + language);

        }


    }


}
