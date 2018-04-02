package com.sticker_android.controller.activities.corporate.home;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
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
import android.util.DisplayMetrics;
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
import com.sticker_android.controller.fragment.AccountSettingFragment;
import com.sticker_android.controller.fragment.ProfileFragment;
import com.sticker_android.controller.fragment.designer.DesignerContentFragment;
import com.sticker_android.controller.fragment.designer.DesignerContestFragment;
import com.sticker_android.controller.fragment.designer.DesignerHomeFragment;
import com.sticker_android.controller.fragment.designer.DesignerReportFragment;
import com.sticker_android.controller.fragment.fanhome.FanHomeFragment;
import com.sticker_android.model.User;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiConstant;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.UserTypeEnum;
import com.sticker_android.utils.sharedpref.AppPref;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.Locale;

import retrofit2.Call;

public class DesignerHomeActivity extends AppBaseActivity implements NavigationView.OnNavigationItemSelectedListener,ProfileFragment.OnFragmentProfileListener {
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private AppPref appPref;
    private User user;
    private AlertDialog languageDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*setStatusBarGradiant(this, AppConstants.DESIGNER);*/
        setContentView(R.layout.activity_designer_home);
        init();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setToolBarTitle();
        setToolbarBackground(toolbar);
        setSupportActionBar(toolbar);
        setViewReferences();
        setViewListeners();
        actionBarToggle(toolbar);
        showFragmentManually();
        changeStatusBarColor(getResources().getColor(R.color.colorstatusBarDesigner));
        setUserDataIntoNaviagtion();
    }
    private void setUserDataIntoNaviagtion() {
        View header= navigationView.getHeaderView(0);
        TextView   tvUserName=(TextView)header.findViewById(R.id.tvUserName);
        TextView   tvEmail=(TextView)header.findViewById(R.id.tvEmail);
        tvUserName.setText(user.getFirstName()+" "+ user.getLastName());
        tvEmail.setText(user.getEmail());

        ImageView imageProfile= (ImageView) header.findViewById(R.id.imageViewProfile);
        LinearLayout linearLayout= (LinearLayout) header.findViewById(R.id.nav_header_common);
        linearLayout.setBackground(getResources().getDrawable(R.drawable.designer_profile_bg_hdpi));
        imageProfile.setImageResource(R.drawable.designer_hdpi);
        imageLoader.displayImage(ApiConstant.IMAGE_URl+ user.getCompanyLogo(),imageProfile, displayImageOptions);

    }
    @Override
    protected boolean isValidData() {
        return false;
    }



    private void setBackground(Toolbar toolbar) {
        UserTypeEnum userTypeEnum=Enum.valueOf(UserTypeEnum.class,user.getUserType().toUpperCase());
        switch (userTypeEnum){
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
        appPref=new AppPref(this);
        user =appPref.getUserInfo();
    }


    private void setToolBarTitle() {
        TextView textView= (TextView) toolbar.findViewById(R.id.tvToolbar);
        textView.setText(getResources().getString(R.string.txt_home));
       }


    private void setToolbarBackground(Toolbar toolbar) {
        Drawable drawable=getBaseContext().getResources().getDrawable(R.drawable.designer_header_hdpi);
        if (Build.VERSION.SDK_INT >= 16){
            toolbar.setBackground(drawable);
        }else{
            toolbar.setBackgroundDrawable(drawable);
        }
        if(user.getUserType()!=null)
            setBackground(toolbar);
    }

    private void actionBarToggle(Toolbar toolbar) {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
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

    }
    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();
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
        TextView textView=(TextView) toolbar.findViewById(R.id.tvToolbar);
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragmentClass=null;
        if (id == R.id.nav_home) {
            fragmentClass = new DesignerHomeFragment();
            textView.setText(getResources().getString(R.string.txt_home));
            // Handle the camera action
        } else if (id == R.id.nav_content_for_appproval) {
            fragmentClass = new DesignerContentFragment();
            textView.setText("Content Approval");
            //    Toast.makeText(getApplicationContext(),"Under Development",Toast.LENGTH_SHORT).show();

        }else if(id==R.id.nav_report){
            fragmentClass = new DesignerReportFragment();
            textView.setText("Report");

        } else if(id==R.id.nav_contest){
            fragmentClass = new DesignerContestFragment();
            textView.setText("Contest");
        }else if (id == R.id.nav_profile) {
           // startNewActivity(ViewProfileActivity.class);
          //  fragmentClass = new DesignerHomeFragment();
            fragmentClass = new ProfileFragment();
            textView.setText(getResources().getString(R.string.txt_myprofile));

//            textView.setText(getResources().getString(R.string.txt_home));
            overridePendingTransition(R.anim.activity_animation_enter,
                    R.anim.activity_animation_exit);
        }
        else if (id == R.id.nav_account_setting) {
            fragmentClass = AccountSettingFragment.newInstance("","");
            textView.setText(getResources().getString(R.string.txt_account_setting));
        }
        else if (id == R.id.nav_logout) {
            appPref.saveUserObject(new User());
            appPref.setLoginFlag(false);
            Toast.makeText(getApplicationContext(),"User logout Successfully",Toast.LENGTH_SHORT).show();
            startNewActivity(SigninActivity.class);
            finish();
        }
        setUserDataIntoNaviagtion();
        // Insert the fragment by replacing any existing fragment

        FragmentManager fragmentManager = getSupportFragmentManager();
        if(fragmentClass!=null) {
            replaceFragment(fragmentClass);
        } drawer.closeDrawer(GravityCompat.START);
        return true;
    }

   public void replaceFragment(Fragment fragmentClass){
    FragmentTransaction fragmentTransaction =
            getSupportFragmentManager().beginTransaction();
    fragmentTransaction.setCustomAnimations(R.anim.activity_animation_enter, R.anim.activity_animation_exit,
            R.anim.activity_animation_enter, R.anim.activity_animation_exit);
    fragmentTransaction.replace(R.id.container_home,
            fragmentClass);
       fragmentTransaction.addToBackStack(null).commit();
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
        ImageView imvLogoChangeLanguage= (ImageView) languageDialogview.findViewById(R.id.imvLogoChangeLanguage);
        final RadioGroup radioGroup = (RadioGroup)languageDialogview. findViewById(R.id.myRadioGroup);
        final RadioButton rdbEnglish = (RadioButton) languageDialogview.findViewById(R.id.rdbEnglish);
        final RadioButton rdbArabic = (RadioButton)languageDialogview. findViewById(R.id.rdbArabic);
        Button dialogButton = (Button) languageDialogview.findViewById(R.id.btn_update);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLanguage(radioGroup,rdbEnglish,rdbArabic);
                updatelanguageApi();
                languageDialog.dismiss();
            }
        });

        languageDialogview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(languageDialog!=null)
                    languageDialog.dismiss();
            }
        });
    }

    private void updatelanguageApi() {
        final int language= appPref.getLanguage(0);
        Call<ApiResponse> apiResponseCall=  RestClient.getService().changeLanguage(user.getId(),language,"");
        apiResponseCall.enqueue(new ApiCall(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if(apiResponse.status){
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
        transaction.replace(R.id.container_home, new FanHomeFragment());
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
