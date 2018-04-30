package com.sticker_android.controller.activities.fan.home;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sticker_android.R;
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.controller.activities.common.signin.SigninActivity;
import com.sticker_android.controller.fragment.common.AccountSettingFragment;
import com.sticker_android.controller.fragment.common.ProfileFragment;
import com.sticker_android.controller.fragment.fan.fancustomization.FanCustomizationFragment;
import com.sticker_android.controller.fragment.fan.fandownloads.FanDownloadFragment;
import com.sticker_android.controller.fragment.fan.fanhome.FanHomeFragment;
import com.sticker_android.controller.fragment.fan.notification.FanNotification;
import com.sticker_android.model.User;
import com.sticker_android.network.ApiConstant;
import com.sticker_android.utils.UserTypeEnum;
import com.sticker_android.utils.sharedpref.AppPref;
import com.theartofdev.edmodo.cropper.CropImage;

public class FanHomeActivity extends AppBaseActivity
        implements NavigationView.OnNavigationItemSelectedListener ,ProfileFragment.OnFragmentProfileListener{

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private AppPref appPref;
    private User user;
    private CoordinatorLayout mainView;
    private MenuItem item;
    private TextView tvUserName;
    private TextView tvEmail;
    private AlertDialog languageDialog;
    private MenuItem mSelectedMenu;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*setStatusBarGradiant(this, AppConstants.FAN);*/
        setContentView(R.layout.activity_fan_home);
        init();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setToolBarTitle();
        setToolbarBackground(toolbar);
        setSupportActionBar(toolbar);
        setViewReferences();
        setViewListeners();
        actionBarToggle(toolbar);
        changeStatusBarColor(getResources().getColor(R.color.colorstatusBarFan));
        setUserDataIntoNaviagtion();
       // showFragmentManually();
        replaceFragment(new FanHomeFragment());
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
        View header= navigationView.getHeaderView(0);
        TextView   tvUserName=(TextView)header.findViewById(R.id.tvUserName);
        TextView   tvEmail=(TextView)header.findViewById(R.id.tvEmail);
        tvUserName.setText(user.getFirstName()+" "+ user.getLastName());
        tvEmail.setText(user.getEmail());
        ImageView imageProfile= (ImageView) header.findViewById(R.id.imageViewProfile);
        imageLoader.displayImage(ApiConstant.IMAGE_URl+ user.getCompanyLogo(),imageProfile, displayImageOptions);
        LinearLayout linearLayout= (LinearLayout) header.findViewById(R.id.nav_header_common);
        linearLayout.setBackground(getResources().getDrawable(R.drawable.fan_profile_bg_hdpi));
        imageProfile.setImageResource(R.drawable.fan_xhdpi);
    }
    private void setBackground(Toolbar toolbar) {
        UserTypeEnum userTypeEnum=Enum.valueOf(UserTypeEnum.class,user.getUserType().toUpperCase());

        switch (userTypeEnum){
            case FAN:
                toolbar.setBackground(getResources().getDrawable(R.drawable.fan_header_xhdpi));
                break;
            case DESIGNER:
                toolbar.setBackground(getResources().getDrawable(R.drawable.gradient_bg_des_hdpi));

                break;
            case CORPORATE:
                toolbar.setBackground(getResources().getDrawable(R.drawable.gradient_bg_hdpi));

                break;
        }
    }

    private void init() {
        appPref=new AppPref(this);
        user =appPref.getUserInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
        setUserDataIntoNaviagtion();
    }

    private void setToolBarTitle() {
        TextView  textView= (TextView) toolbar.findViewById(R.id.tvToolbar);
        textView.setText(getResources().getString(R.string.txt_home));
        toolbar.setTitle("");
        centerToolbarText(toolbar,textView);
    }

    private void centerToolbarText(final Toolbar toolbar, final TextView textView) {
        toolbar.postDelayed(new Runnable()
        {
            @Override
            public void run ()
            {
                int maxWidth = toolbar.getWidth();
                int titleWidth = textView.getWidth();
                int iconWidth = maxWidth - titleWidth;

                if (iconWidth > 0)
                {
                    //icons (drawer, menu) are on left and right side
                    int width = maxWidth - iconWidth * 2;
                    textView.setMinimumWidth(width);
                    textView.getLayoutParams().width = width;
                }
            }
        }, 0);
    }

    private void setToolbarBackground(Toolbar toolbar) {
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
      /*  ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
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
        toggle.syncState();*/
        drawer.setScrimColor(Color.TRANSPARENT);
    }
    private void manageNavigationClickItem(MenuItem item) {
        if (item == null) {
            return;
        }
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.container_home);

        TextView  textView= (TextView) toolbar.findViewById(R.id.tvToolbar);
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragmentClass=null;
        if (id == R.id.nav_home && !(f instanceof FanHomeFragment)) {
            textView.setText("Home");
            fragmentClass = new FanHomeFragment();
        } else if (id == R.id.nav_downloads && !(f instanceof FanDownloadFragment)) {
            textView.setText("Downloads");
            fragmentClass = new FanDownloadFragment();
        } else if (id == R.id.nav_customization && !(f instanceof FanCustomizationFragment)) {
            textView.setText("Customization");
            fragmentClass = new FanCustomizationFragment();

        } else if (id == R.id.nav_profile && !(f instanceof ProfileFragment)) {
            textView.setText("Home");
            //  startNewActivity(ViewProfileActivity.class);
            fragmentClass = new ProfileFragment();
            textView.setText(getResources().getString(R.string.txt_myprofile));
            overridePendingTransition(R.anim.activity_animation_enter,
                    R.anim.activity_animation_exit);
        }
        else if (id == R.id.nav_account_setting && !(f instanceof AccountSettingFragment)) {
            textView.setText(getString(R.string.txt_account_setting));
            fragmentClass = AccountSettingFragment.newInstance("","");
        }
        else if (id == R.id.nav_notification && !(f instanceof FanNotification)) {
            textView.setText(getString(R.string.txt_notifications));
            fragmentClass = new FanNotification();
        }
        else if (id == R.id.nav_logout) {
            userLogout();
        }

        // Insert the fragment by replacing any existing fragment
        if(fragmentClass!=null) {
            replaceFragment(fragmentClass);        }
        drawer.closeDrawer(GravityCompat.START);

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
        //   tvUserName=(TextView)findViewById(R.id.tvUserName);
        // tvEmail=(TextView)findViewById(R.id.tvEmail);

    }

    @Override
    protected boolean isValidData() {
        return false;
    }

    @Override
    public void onBackPressed() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.container_home);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (f instanceof FanHomeFragment) {
            exitOnBack();
        } else {
            setToolBarTitle();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container_home, new FanHomeFragment(), getResources().getString(R.string.txt_home));
            transaction.setCustomAnimations(R.anim.activity_animation_enter, R.anim.activity_animation_exit,
                    R.anim.activity_animation_enter, R.anim.activity_animation_exit);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    private void exitOnBack() {
        if (doubleBackToExitPressedOnce) {
            clearBackStack();
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

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent objEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return super.onKeyUp(keyCode, objEvent);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        this.mSelectedMenu = item;
        drawer.closeDrawer(GravityCompat.START);
             return true;
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

    public void replaceFragment(final Fragment fragmentClass) {
        final String tag=fragmentClass.getClass().getSimpleName();
                FragmentTransaction fragmentTransaction =
                        getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container_home,
                        fragmentClass, tag);
                int count = getFragmentManager().getBackStackEntryCount();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();


    }


    private void showFragmentManually() {
        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment=new FanHomeFragment();
        transaction.replace(R.id.container_home, fragment);
        transaction.commit();
    }
    public void setProfileFragmentReference(ProfileFragment profileFragmentReference){
        this.mProfileFragment = profileFragmentReference;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            mProfileFragment.onActivityResult(requestCode, resultCode, data);
        }

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 131:
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
}
