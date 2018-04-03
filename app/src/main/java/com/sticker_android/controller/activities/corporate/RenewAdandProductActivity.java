package com.sticker_android.controller.activities.corporate;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sticker_android.R;
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.model.User;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.sharedpref.AppPref;
import com.sticker_android.view.SetDate;

public class RenewAdandProductActivity extends AppBaseActivity implements View.OnClickListener {

    private AppPref appPref;
    private User userdata;
    private Toolbar toolbar;
    private TextView rePost;
    private EditText edtExpireDate;
    private EditText edtCorpName,edtDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renew_adand_product);
        init();
        getuserInfo();
        setToolbar();
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

    @Override
    protected void setViewListeners() {

    }

    @Override
    protected void setViewReferences() {
        rePost = (Button) findViewById(R.id.act_corp_add_new_btn_re_post);
        edtExpireDate=(EditText)findViewById(R.id.act_add_new_corp_edt_name);
        edtDescription=(EditText) findViewById(R.id.edtDescription);
        edtCorpName=(EditText) findViewById(R.id.act_add_new_corp_edt_name);

    }

    @Override
    protected boolean isValidData() {

        if(edtCorpName.getText().toString().trim().isEmpty()){
            Utils.showToast(this,"Please enter a name.");
            return false;
        }else if(edtExpireDate.getText().toString().trim().isEmpty()){
            Utils.showToast(this,"Please enter a expire date.");

            return false;
        }else if(edtDescription.getText().toString().trim().isEmpty()){
            return false;
        }
        return true;

    }

    private void init() {

        appPref = new AppPref(getActivity());
    }

    private void getuserInfo() {
        userdata = appPref.getUserInfo();
    }

    /**
     * Method is used to set the toolbar title
     */
    private void setToolBarTitle() {
        TextView textView = (TextView) toolbar.findViewById(R.id.tvToolbar);
        textView.setText(getResources().getString(R.string.act_corp_txt_renew));
        toolbar.setTitle(" ");
    }

    /**
     * Method is used to set the toolbar
     */
    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setToolbarBackground();
        setToolBarTitle();
        setSupportActionBar(toolbar);
    }

    /**
     * Method is used to set the toolbar background
     */
    private void setToolbarBackground() {
        toolbar.setBackground(getResources().getDrawable(R.drawable.corporate_header_hdpi));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.act_corp_add_new_btn_re_post:
                if(isValidData()) {
                    Toast.makeText(getApplicationContext(), "" + "called", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.act_add_new_ad_corp_edt_expire_date:
                SetDate setDate=new SetDate(edtExpireDate,this);
                break;
            default:
        }
    }
}
