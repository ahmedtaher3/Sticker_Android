package com.sticker_android.controller.activities.corporate.home;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
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

import com.sticker_android.R;
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.controller.activities.common.signin.SigninActivity;
import com.sticker_android.controller.fragment.AccountSettingFragment;
import com.sticker_android.controller.fragment.ProfileFragment;
import com.sticker_android.controller.fragment.corporate.CorporateHomeFragment;
import com.sticker_android.controller.fragment.corporate.CorporateReportFragment;
import com.sticker_android.controller.fragment.corporate.contentapproval.CorporateContentApprovalFragment;
import com.sticker_android.controller.fragment.corporate.contest.CorporateContestFragment;
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

public class CorporateHomeActivity extends AppBaseActivity implements
        NavigationView.OnNavigationItemSelectedListener, ProfileFragment.OnFragmentProfileListener {
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private AppPref appPref;
    private User user;
    private AlertDialog languageDialog;
    private String TAG = CorporateHomeActivity.class.getSimpleName();

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
            }
        }, 200);
        setViewReferences();
        setViewListeners();
        changeStatusBarColor(getResources().getColor(R.color.colorstatusBarCorporate));
        showFragmentManually();
        setUserDataIntoNaviagtion();
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
    public void onBackPressed() {
        TextView textView = (TextView) toolbar.findViewById(R.id.tvToolbar);
        textView.setText(getResources().getString(R.string.txt_home));
        toolbar.setTitle("");
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        TextView textView = (TextView) toolbar.findViewById(R.id.tvToolbar);
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragmentClass = null;
        if (id == R.id.nav_home) {
            fragmentClass = new CorporateHomeFragment();
            textView.setText(getResources().getString(R.string.txt_home));
            // Handle the camera action
        } else if (id == R.id.nav_report) {
            fragmentClass = new CorporateReportFragment();
            textView.setText("Report");
            //    Toast.makeText(getApplicationContext(),"Under Development",Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_profile) {
            //  startNewActivity(ViewProfileActivity.class);
            fragmentClass = new ProfileFragment();
            textView.setText(getResources().getString(R.string.txt_myprofile));
            overridePendingTransition(R.anim.activity_animation_enter,
                    R.anim.activity_animation_exit);
        } else if (id == R.id.nav_account_setting) {
            fragmentClass = AccountSettingFragment.newInstance("", "");
            textView.setText(getResources().getString(R.string.txt_account_setting));
        } else if (id == R.id.nav_logout) {
            userLogout();
        }else if(id==R.id.nav_corp_contest){
            textView.setText(getResources().getString(R.string.txt_contest));
            fragmentClass=new CorporateContestFragment();
        }else if(id==R.id.nav_content_for_approval){
            textView.setText(getResources().getString(R.string.txt_content_approval));
            fragmentClass=new CorporateContentApprovalFragment();
        }

        // Insert the fragment by replacing any existing fragment
        setUserDataIntoNaviagtion();
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentClass != null) {
            replaceFragment(fragmentClass);
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void replaceFragment(Fragment fragmentClass) {
        FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.activity_animation_enter, R.anim.activity_animation_exit,
                R.anim.activity_animation_enter, R.anim.activity_animation_exit);
        fragmentTransaction.replace(R.id.container_home,
                fragmentClass);
        int count = getFragmentManager().getBackStackEntryCount();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    private void userLogout() {
        appPref.saveUserObject(new User());
        appPref.setLoginFlag(false);
        Intent intent = new Intent(getActivity(), SigninActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_animation_enter,
                R.anim.activity_animation_exit);
        finish();
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
        transaction.replace(R.id.container_home, new CorporateHomeFragment());
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
        if(resultCode==RESULT_OK){
            switch (requestCode){
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


}