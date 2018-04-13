package com.sticker_android.controller.activities.fan.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sticker_android.R;
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.controller.activities.common.signin.SigninActivity;
import com.sticker_android.controller.fragment.AccountSettingFragment;
import com.sticker_android.controller.fragment.ProfileFragment;
import com.sticker_android.controller.fragment.fancustomization.FanCustomizationFragment;
import com.sticker_android.controller.fragment.fandownloads.FanDownloadFragment;
import com.sticker_android.controller.fragment.fanhome.FanHomeFragment;
import com.sticker_android.model.User;
import com.sticker_android.network.ApiConstant;
import com.sticker_android.utils.UserTypeEnum;
import com.sticker_android.utils.sharedpref.AppPref;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.List;

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
        showFragmentManually();
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
        toggle.syncState();
        drawer.setScrimColor(Color.TRANSPARENT);
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
        TextView  textView= (TextView) toolbar.findViewById(R.id.tvToolbar);
        textView.setText(getResources().getString(R.string.txt_home));
        toolbar.setTitle("");
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(getFragmentManager().getBackStackEntryCount()>0){
            getFragmentManager().popBackStack();
        }else{
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        TextView  textView= (TextView) toolbar.findViewById(R.id.tvToolbar);
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragmentClass=null;
        if (id == R.id.nav_home) {
            textView.setText("Home");
            fragmentClass = new FanHomeFragment();
        } else if (id == R.id.nav_downloads) {
            textView.setText("Downloads");
            fragmentClass = new FanDownloadFragment();
        } else if (id == R.id.nav_customization) {
            textView.setText("Customization");
            fragmentClass = new FanCustomizationFragment();

        } else if (id == R.id.nav_profile) {
            textView.setText("Home");
          //  startNewActivity(ViewProfileActivity.class);
            fragmentClass = new ProfileFragment();
            textView.setText(getResources().getString(R.string.txt_myprofile));
            overridePendingTransition(R.anim.activity_animation_enter,
                    R.anim.activity_animation_exit);
        }
        else if (id == R.id.nav_account_setting) {
            textView.setText(getString(R.string.txt_account_setting));
            fragmentClass = AccountSettingFragment.newInstance("","");
        }
        else if (id == R.id.nav_logout) {
            appPref.saveUserObject(new User());
            appPref.setLoginFlag(false);
            startNewActivity(SigninActivity.class);
            finish();
        }

        // Insert the fragment by replacing any existing fragment
        if(fragmentClass!=null) {
            replaceFragment(fragmentClass);        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void replaceFragment(Fragment fragmentClass){

        FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();

        fragmentTransaction.setCustomAnimations(R.anim.activity_animation_enter,
                R.anim.activity_animation_exit,
                R.anim.activity_animation_enter, R.anim.activity_animation_exit);

        fragmentTransaction.replace(R.id.container_home,
                fragmentClass);

            fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();
    }


    private boolean onBackPressed(FragmentManager fm) {
        if (fm != null) {
            if (fm.getBackStackEntryCount() > 0) {
                fm.popBackStack();
                return true;
            }

            List<Fragment> fragList = fm.getFragments();
            if (fragList != null && fragList.size() > 0) {
                for (Fragment frag : fragList) {
                    if (frag == null) {
                        continue;
                    }
                    if (frag.isVisible()) {
                        if (onBackPressed(frag.getChildFragmentManager())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
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
    }

    @Override
    public void updatedata() {
        init();
        setUserDataIntoNaviagtion();
    }
}
