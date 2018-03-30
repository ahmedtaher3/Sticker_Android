package com.sticker_android.controller.fragment;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.sticker_android.R;
import com.sticker_android.controller.adaptors.ViewPagerAdapter;
import com.sticker_android.controller.fragment.base.BaseFragment;
import com.sticker_android.model.UserData;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.sharedpref.AppPref;

import java.util.Locale;

import retrofit2.Call;


public class AccountSettingFragment extends BaseFragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ViewPagerAdapter adapter;
    private AppPref appPref;
    private UserData userdata;
    private AlertDialog languageDialog;

    public AccountSettingFragment() {
        // Required empty public constructor
    }

    public static AccountSettingFragment newInstance(String param1, String param2) {
        AccountSettingFragment fragment = new AccountSettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_account_setting, container, false);
        init();
        setViewReferences(view);
        setViewListeners();
        setupViewPager();
        setupViewPager();
        addFragmentToTab();
        setBackground();
        setSelectedTabColor();
        int runningDeviceConfig = getResources().getInteger(R.integer.running_device_config);

        if(runningDeviceConfig == 7){
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }
        else if(runningDeviceConfig == 10){
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        }
        else{
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }
        return view;
    }

    private void init() {
        appPref=new AppPref(getActivity());
        userdata= appPref.getUserInfo();
    }

    private void addFragmentToTab() {
        adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new ChangePasswordFragment(), "Change Password");
        adapter.addFragment(new ContactUsFragment(), "Contact Us ");
        adapter.addFragment(new TermsAndConditionFragment(), "Terms & Conditions");
        adapter.addFragment(new AboutUsFragment(), "About Us ");
        viewPager.setAdapter(adapter);

    }

    private void setSelectedTabColor() {
        tabLayout.setTabTextColors(Color.parseColor("#AAFFFFFF"), Color.WHITE);
    }

    private void setupViewPager() {
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setBackground() {
        switch (userdata.getUserType()){
            case "fan":
                tabLayout.setBackground(getResources().getDrawable(R.drawable.side_nav_fan));
                break;
            case "designer":
                tabLayout.setBackground(getResources().getDrawable(R.drawable.side_nav_designer));

                break;
            case "corporate":
                tabLayout.setBackground(getResources().getDrawable(R.drawable.side_nav_corporate));

                break;
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();

    }


    @Override
    protected void setViewListeners() {
        tabLayout.addOnTabSelectedListener(new TabListeners(viewPager));
    }

    @Override
    protected void setViewReferences(View view) {
        viewPager = (ViewPager)view. findViewById(R.id.view_pager);
        tabLayout = (TabLayout)view. findViewById(R.id.act_landing_tab);

    }

    @Override
    protected boolean isValidData() {
        return false;
    }



    public class TabListeners implements TabLayout.OnTabSelectedListener {

        private ViewPager viewPager;

        public TabListeners(ViewPager viewPager) {
            this.viewPager = viewPager;
        }

        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            Utils.hideKeyboard(getActivity());
            viewPager.setCurrentItem(tab.getPosition());
            Fragment fragment = adapter.getItem(tab.getPosition());
            if (fragment instanceof ChangePasswordFragment) {
                ((ChangePasswordFragment) fragment).clearField();
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.fan_home, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            openLanguageDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        TextView tvtxtChangeLanguage= (TextView)languageDialogview.findViewById(R.id.tvtxtChangeLanguage);
        int language=  appPref.getLanguage(0);
        rdbEnglish.setChecked(true);
        if(language>0){
            rdbArabic.setChecked(true);
        }
        setButtonBackground(dialogButton,tvtxtChangeLanguage);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideKeyboard(getActivity());
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

    private void setButtonBackground(Button dialogButton, TextView tvtxtChangeLanguage) {

        switch (userdata.getUserType()){
            case "fan":
                tvtxtChangeLanguage.setTextColor(getResources().getColor(R.color.colorFanText));
                dialogButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.fan_btn_background));
                break;
            case "designer":
                tvtxtChangeLanguage.setTextColor(getResources().getColor(R.color.colorDesignerText));
                dialogButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.designer_btn_background));

                break;
            case "corporate":
                tvtxtChangeLanguage.setTextColor(getResources().getColor(R.color.colorCorporateText));
                dialogButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.corporate_btn_background));
                break;
        }

    }

    private void updatelanguageApi() {

        final int language= appPref.getLanguage(0);
        Call<ApiResponse> apiResponseCall=  RestClient.getService().changeLanguage(userdata.getId(),language,"");
        apiResponseCall.enqueue(new ApiCall(getActivity()) {
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


}
