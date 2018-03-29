package com.sticker_android.controller.activities.common.userprofile;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.sticker_android.R;
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.model.UserData;
import com.sticker_android.utils.sharedpref.AppPref;

public class ViewProfileActivity extends AppBaseActivity {

    private Toolbar toolbar;
    private AppPref appPref;
    private UserData userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        init();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setToolbarData();
        setBackground(toolbar);
    }

    private void setToolbarData() {
        toolbar.setTitle("");
        setToolBarTitle();
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back_arrow_small);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    private void init() {
        appPref=new AppPref(this);
        userData=appPref.getUserInfo();
    }
    private void setBackground(Toolbar toolbar) {
        switch (userData.getUserType()){
            case "fan":
                toolbar.setBackground(getResources().getDrawable(R.drawable.fan_header_hdpi));
                changeStatusBarColor(getResources().getColor(R.color.colorstatusBarFan));
                break;
            case "designer":
                toolbar.setBackground(getResources().getDrawable(R.drawable.designer_header_hdpi));
                changeStatusBarColor(getResources().getColor(R.color.colorstatusBarDesigner));
                break;
            case "corporate":
                toolbar.setBackground(getResources().getDrawable(R.drawable.corporate_header_hdpi));
                changeStatusBarColor(getResources().getColor(R.color.colorstatusBarCorporate));
                break;
        }
    }

    private void setToolBarTitle() {
        TextView textView= (TextView) toolbar.findViewById(R.id.tvToolbar);
        textView.setText(getResources().getString(R.string.txt_myprofile));
        // centerToolbarText(toolbar,textView);
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
