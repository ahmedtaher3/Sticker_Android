package com.sticker_android.controller.activities.common.terms;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.sticker_android.R;
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.controller.activities.common.signin.SigninActivity;
import com.sticker_android.model.User;
import com.sticker_android.utils.AppConstants;
import com.sticker_android.utils.sharedpref.AppPref;

public class TermsActivity extends AppBaseActivity {

    private AppPref appPref ;
    private Toolbar toolbar;
    private User user;
    private String type="fan";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        getUserSelectedOption();
        setToolbarData();
        setViewReferences();
        setViewListeners();
        toolbar.setNavigationIcon(R.drawable.back_arrow_small);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setToolbarData() {
        setToolBarTitle();
        setSupportActionBar(toolbar);
        setViewReferences();
        setViewListeners();
        changeStatusBarColor(getResources().getColor(R.color.colorstatusBarCorporate));
        setBackground(toolbar);
    }

    private void setToolBarTitle() {
        TextView textView= (TextView) toolbar.findViewById(R.id.tvToolbar);
        textView.setText(getActivity().getString(R.string.txt_hint_terms_conditions));
        toolbar.setTitle("");
    }
    private void getUserSelectedOption() {
        if(getIntent().getExtras()!=null){
            type=getIntent().getExtras().getString(AppConstants.USERSELECTION);
        }

    }

    private void setBackground(Toolbar toolbar) {
        switch (type){
            case "fan":
                toolbar.setBackground(getResources().getDrawable(R.drawable.fan_header_hdpi));
                /*changeStatusBarColor(getResources().getColor(R.color.colorstatusBarFan));*/
                setStatusBarGradiant(this, AppConstants.FAN);
                break;
            case "designer":
                toolbar.setBackground(getResources().getDrawable(R.drawable.designer_header_hdpi));
                /*changeStatusBarColor(getResources().getColor(R.color.colorstatusBarDesigner));*/
                setStatusBarGradiant(this, AppConstants.DESIGNER);
                break;
            case "corporate":
                toolbar.setBackground(getResources().getDrawable(R.drawable.corporate_header_hdpi));
                /*changeStatusBarColor(getResources().getColor(R.color.colorstatusBarCorporate));*/
                setStatusBarGradiant(this, AppConstants.CORPORATE);
                break;
        }
    }

    @Override
    protected void setViewListeners() {

    }

    @Override
    protected void setViewReferences() {

    }

    @Override
    protected boolean isValidData() {
        return false;
    }
}
