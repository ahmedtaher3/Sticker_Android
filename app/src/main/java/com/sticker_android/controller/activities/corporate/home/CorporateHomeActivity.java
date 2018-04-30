package com.sticker_android.controller.activities.corporate.home;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
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
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.controller.activities.common.signin.SigninActivity;
import com.sticker_android.controller.fragment.common.AccountSettingFragment;
import com.sticker_android.controller.fragment.common.ProfileFragment;
import com.sticker_android.controller.fragment.corporate.CorporateHomeFragment;
import com.sticker_android.controller.fragment.corporate.CorporateReportFragment;
import com.sticker_android.controller.fragment.corporate.contentapproval.CorporateContentApprovalFragment;
import com.sticker_android.controller.fragment.corporate.contest.CorporateContestFragment;
import com.sticker_android.controller.fragment.corporate.notification.CorporateNotificationFragment;
import com.sticker_android.model.User;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiConstant;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.UserTypeEnum;
import com.sticker_android.utils.fragmentinterface.UpdateToolbarTitle;
import com.sticker_android.utils.sharedpref.AppPref;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.Locale;

import retrofit2.Call;

public class CorporateHomeActivity extends AppBaseActivity implements
        NavigationView.OnNavigationItemSelectedListener, ProfileFragment.OnFragmentProfileListener, UpdateToolbarTitle {
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private CoordinatorLayout mainView;
    private Toolbar toolbar;
    private AppPref appPref;
    private User user;
    private AlertDialog languageDialog;
    private String TAG = CorporateHomeActivity.class.getSimpleName();
    Fragment fragmentClass = null;
    boolean doubleBackToExitPressedOnce = false;
    private boolean isFragClicked;
    private MenuItem mSelectedMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*setStatusBarGradiant(this, AppConstants.CORPORATE);*/
        setContentView(R.layout.activity_corporate_home);
        init();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                toolbar = (Toolbar) findViewById(R.id.toolbar);
                setToolBarTitle();
                setToolbarBackground(toolbar);
                setSupportActionBar(toolbar);
                actionBarToggle(toolbar);
                toolbar.setTitle("");
                setBadgeCount();
            }
        }, 200);
        setViewReferences();
        setViewListeners();
        changeStatusBarColor(getResources().getColor(R.color.colorstatusBarCorporate));
        showFragmentManually();
        setUserDataIntoNaviagtion();

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


    private void setUserDataIntoNaviagtion() {
        View header = navigationView.getHeaderView(0);
        TextView tvUserName = (TextView) header.findViewById(R.id.tvUserName);
        TextView tvEmail = (TextView) header.findViewById(R.id.tvEmail);
        tvUserName.setText(user.getFirstName() + " " + user.getLastName());
        tvEmail.setText(user.getEmail());
        ImageView imageProfile = (ImageView) header.findViewById(R.id.imageViewProfile);
        LinearLayout linearLayout = (LinearLayout) header.findViewById(R.id.nav_header_common);
        linearLayout.setBackground(getResources().getDrawable(R.drawable.profile_bg_hdpi));
        imageProfile.setImageResource(R.drawable.corporate_hdpi);
        imageLoader.displayImage(ApiConstant.IMAGE_URl + user.getCompanyLogo(), imageProfile, displayImageOptions);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
        setUserDataIntoNaviagtion();
    }

    @Override
    protected boolean isValidData() {
        return false;
    }


    private void setBackground(Toolbar toolbar) {
        UserTypeEnum userTypeEnum = Enum.valueOf(UserTypeEnum.class, user.getUserType().toUpperCase());
        switch (userTypeEnum) {
            case FAN:
                toolbar.setBackground(getResources().getDrawable(R.drawable.fan_header_hdpi));
                break;
            case DESIGNER:
                toolbar.setBackground(getResources().getDrawable(R.drawable.designer_header_hdpi));
                break;
            case CORPORATE:
                toolbar.setBackground(getResources().getDrawable(R.drawable.corporate_header_hdpi));
                break;
        }
    }

    private void init() {
        appPref = new AppPref(this);
        user = appPref.getUserInfo();
    }


    private void setToolBarTitle() {
        TextView textView = (TextView) toolbar.findViewById(R.id.tvToolbar);
        textView.setText(getResources().getString(R.string.txt_home));
        toolbar.setTitle(" ");
    }


    private void setToolbarBackground(Toolbar toolbar) {
        if (user.getUserType() != null)
            setBackground(toolbar);
    }

    private void actionBarToggle(Toolbar toolbar) {

        final ImageView imageView = toolbar.findViewById(R.id.imv_nav_drawer_menu);
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
        /*
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            public void onDrawerClosed(View view) {
                supportInvalidateOptionsMenu();
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
<<<<<<< HEAD
        toggle.syncState();*/

        //   toggle.syncState();
        drawer.setScrimColor(Color.TRANSPARENT);

    }

    private void manageNavigationClickItem(MenuItem item) {
        if(item == null){
            return;
        }
        String tag = getResources().getString(R.string.txt_home);
        TextView textView = (TextView) toolbar.findViewById(R.id.tvToolbar);
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.container_home);

        if (id == R.id.nav_home && !(f instanceof CorporateHomeFragment)) {
            fragmentClass = CorporateHomeFragment.newInstance();
            textView.setText(getResources().getString(R.string.txt_home));
            tag = getResources().getString(R.string.txt_home);
            replaceFragment(fragmentClass, tag);
            // Handle the camera action
        } else if (id == R.id.nav_report && !(f instanceof CorporateReportFragment)) {
            fragmentClass = new CorporateReportFragment();
            textView.setText(R.string.txt_report);
            tag = getResources().getString(R.string.txt_report);
            replaceFragment(fragmentClass, tag);
            //    Toast.makeText(getApplicationContext(),"Under Development",Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_profile && !(f instanceof ProfileFragment)) {
            //  startNewActivity(ViewProfileActivity.class);
            fragmentClass = new ProfileFragment();
            textView.setText(getResources().getString(R.string.txt_myprofile));
            tag = getResources().getString(R.string.txt_myprofile);
            replaceFragment(fragmentClass, tag);
        } else if (id == R.id.nav_account_setting && !(f instanceof AccountSettingFragment)) {
            fragmentClass = AccountSettingFragment.newInstance("", "");
            textView.setText(getResources().getString(R.string.txt_account_setting));
            tag = getResources().getString(R.string.txt_account_setting);
            replaceFragment(fragmentClass, tag);
        } else if (id == R.id.nav_logout) {
            userLogout();
        } else if (id == R.id.nav_corp_contest && !(f instanceof CorporateContestFragment)) {
            textView.setText(getResources().getString(R.string.txt_contest));
            fragmentClass = new CorporateContestFragment();
            tag = getResources().getString(R.string.txt_contest);
            replaceFragment(fragmentClass, tag);
        } else if (id == R.id.nav_content_for_approval && !(f instanceof CorporateContentApprovalFragment)) {
            textView.setText(R.string.txt_pending_content);
            fragmentClass = new CorporateContentApprovalFragment();
            tag = getResources().getString(R.string.txt_content_approval);
            replaceFragment(fragmentClass, tag);
        } else if (id == R.id.nav_notification && !(f instanceof CorporateNotificationFragment)) {
            textView.setText(R.string.txt_notifications);
            fragmentClass = CorporateNotificationFragment.newInstance();
            tag = getResources().getString(R.string.txt_notifications);
            replaceFragment(fragmentClass, tag);
        }

        // Insert the fragment by replacing any existing fragment
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


    @Override
    public void onBackPressed() {

        Fragment f = getSupportFragmentManager().findFragmentById(R.id.container_home);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (f instanceof CorporateHomeFragment) {
            exitOnBack();
        } else {
            setToolBarTitle();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container_home, CorporateHomeFragment.newInstance(), getResources().getString(R.string.txt_home));
            transaction.setCustomAnimations(R.anim.activity_animation_enter, R.anim.activity_animation_exit,
                    R.anim.activity_animation_enter, R.anim.activity_animation_exit);
            transaction.addToBackStack(null);
            transaction.commit();
        }

    }

    private void exitOnBack() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.press_again_to_exit), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        this.mSelectedMenu = item;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void replaceFragment(final Fragment fragmentClass, final String tag) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentTransaction fragmentTransaction =
                        getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container_home,
                        fragmentClass, tag);
                int count = getFragmentManager().getBackStackEntryCount();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        }, 200);


    }

    private void userLogout() {
        appPref.userLogout();
        Intent intent = new Intent(getActivity(), SigninActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_animation_enter,
                R.anim.activity_animation_exit);
        finish();
    }

    public boolean isAdded(String tag) {

        int count = getSupportFragmentManager().getBackStackEntryCount();
        for (int i = 0; i < count; i++) {
            if (getSupportFragmentManager().findFragmentByTag(tag) != null)
                if (tag == getSupportFragmentManager().findFragmentByTag(tag).getTag()) {
                    getFragmentManager().popBackStackImmediate();
                    return true;
                }
        }
        return false;
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
        Call<ApiResponse> apiResponseCall = RestClient.getService().changeLanguage(user.getId(), language, "");
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
            appPref.setLanguage(0);
        } else if (selectedId == rdbArabic.getId()) {
            setLocale("ar");
            appPref.setLanguage(1);
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

    private void showFragmentManually() {
        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container_home, CorporateHomeFragment.newInstance(), getResources().getString(R.string.txt_home));
        transaction.setCustomAnimations(R.anim.activity_animation_enter, R.anim.activity_animation_exit,
                R.anim.activity_animation_enter, R.anim.activity_animation_exit);
        transaction.commit();
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
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 11:
                    for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                        fragment.onActivityResult(requestCode, resultCode, data);
                    }
                    break;
            }

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
    public boolean onKeyUp(int keyCode, KeyEvent objEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return super.onKeyUp(keyCode, objEvent);
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
}
