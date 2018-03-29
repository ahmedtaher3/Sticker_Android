package com.sticker_android.controller.activities.fan.home;

import android.os.Bundle;
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
import android.widget.Toast;

import com.sticker_android.R;
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.controller.activities.common.signin.SigninActivity;
import com.sticker_android.controller.activities.common.userprofile.ViewProfileActivity;
import com.sticker_android.controller.fragment.AccountSettingFragment;
import com.sticker_android.controller.fragment.fancustomization.FanCustomizationFragment;
import com.sticker_android.controller.fragment.fandownloads.FanDownloadFragment;
import com.sticker_android.controller.fragment.fanhome.FanHomeFragment;
import com.sticker_android.model.UserData;
import com.sticker_android.network.ApiConstant;
import com.sticker_android.utils.sharedpref.AppPref;

import java.util.List;

public class FanHomeActivity extends AppBaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private  DrawerLayout drawer;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private AppPref appPref;
    private UserData userData;
    private MenuItem item;
    private TextView tvUserName;
    private TextView tvEmail;
    private AlertDialog languageDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        tvUserName.setText(userData.getFirstName()+" "+userData.getLastName());
        tvEmail.setText(userData.getEmail());
        ImageView imageProfile= (ImageView) header.findViewById(R.id.imageViewProfile);
        imageLoader.displayImage(ApiConstant.IMAGE_URl+userData.getCompanyLogo(),imageProfile);

        LinearLayout linearLayout= (LinearLayout) header.findViewById(R.id.nav_header_common);
        linearLayout.setBackground(getResources().getDrawable(R.drawable.fan_profile_bg_hdpi));
        imageProfile.setImageResource(R.drawable.fan_xhdpi);
    }
    private void setBackground(Toolbar toolbar) {
        switch (userData.getUserType()){
            case "fan":
                toolbar.setBackground(getResources().getDrawable(R.drawable.fan_header_xhdpi));
                break;
            case "designer":
                toolbar.setBackground(getResources().getDrawable(R.drawable.gradient_bg_des_hdpi));

                break;
            case "corporate":
                toolbar.setBackground(getResources().getDrawable(R.drawable.gradient_bg_hdpi));

                break;
        }
    }

    private void init() {
         appPref=new AppPref(this);
        userData=appPref.getUserInfo();
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
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
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
     //   tvUserName=(TextView)findViewById(R.id.tvUserName);
       // tvEmail=(TextView)findViewById(R.id.tvEmail);

    }

    @Override
    protected boolean isValidData() {
        return false;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        FragmentManager fm = getSupportFragmentManager();
        if (onBackPressed(fm)) {
            return;
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
            startNewActivity(ViewProfileActivity.class);
            fragmentClass = new FanHomeFragment();
             }
        else if (id == R.id.nav_account_setting) {
            textView.setText(getString(R.string.txt_account_setting));
            fragmentClass = AccountSettingFragment.newInstance("","");
        }
        else if (id == R.id.nav_logout) {
            appPref.saveUserObject(new UserData());
            appPref.setLoginFlag(false);
            startNewActivity(SigninActivity.class);
            SigninActivity.selectedOption="fan";
            finish();
        }

        // Insert the fragment by replacing any existing fragment

        FragmentManager fragmentManager = getSupportFragmentManager();
        if(fragmentClass!=null)
            fragmentManager.beginTransaction().replace(R.id.container_home, fragmentClass).commit();
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
        transaction.replace(R.id.container_home, new FanHomeFragment());
        transaction.commit();
    }

}
