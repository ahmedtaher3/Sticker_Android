package com.sticker_android.controller.activities.fan.home;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.sticker_android.R;
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.controller.activities.common.signup.SignUpActivity;
import com.sticker_android.controller.fragment.AccountSettingFragment;
import com.sticker_android.controller.fragment.ProfileFragment;
import com.sticker_android.controller.fragment.fancustomization.FanCustomizationFragment;
import com.sticker_android.controller.fragment.fandownloads.FanDownloadFragment;
import com.sticker_android.controller.fragment.fanhome.FanHomeFragment;
import com.sticker_android.model.UserData;
import com.sticker_android.utils.sharedpref.AppPref;

public class FanHomeActivity extends AppBaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private  DrawerLayout drawer;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private AppPref appPref;

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
        setNavigationMenu();
         }

    private void setNavigationMenu() {

    }

    private void init() {
         appPref=new AppPref(this);
    }

    private void setToolBarTitle() {
    TextView  textView=toolbar.findViewById(R.id.tvToolbar);
    textView.setText(getResources().getString(R.string.txt_account_setting));
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
        Drawable drawable=getBaseContext().getResources().getDrawable(R.drawable.gradient_bg_fan_hdpi);
        if (Build.VERSION.SDK_INT >= 16){
            toolbar.setBackground(drawable);
        }else{
            toolbar.setBackgroundDrawable(drawable);
        }

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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.fan_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragmentClass=null;
        if (id == R.id.nav_home) {
            fragmentClass = new FanHomeFragment();
               // Handle the camera action
        } else if (id == R.id.nav_downloads) {
            fragmentClass = new FanDownloadFragment();
        //    Toast.makeText(getApplicationContext(),"Under Development",Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_customization) {
            fragmentClass = new FanCustomizationFragment();
           // Toast.makeText(getApplicationContext(),"Under Development",Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_profile) {
            TextView  textView=toolbar.findViewById(R.id.tvToolbar);
            textView.setText(getResources().getString(R.string.txt_profile));
            fragmentClass = ProfileFragment.newInstance("","");
        }
        else if (id == R.id.nav_account_setting) {
            fragmentClass = AccountSettingFragment.newInstance("","");
        }
        else if (id == R.id.nav_logout) {
            appPref.saveUserObject(new UserData());
            appPref.setLoginFlag(false);
            Toast.makeText(getApplicationContext(),"User logout Successfully",Toast.LENGTH_SHORT).show();
             startNewActivity(SignUpActivity.class);
        }

        // Insert the fragment by replacing any existing fragment

        FragmentManager fragmentManager = getSupportFragmentManager();
         if(fragmentClass!=null)
        fragmentManager.beginTransaction().replace(R.id.container_home, fragmentClass).commit();
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
