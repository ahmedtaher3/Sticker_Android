package com.sticker_android.controller.activities.corporate.addnew;

import android.os.Bundle;

import com.sticker_android.R;
import com.sticker_android.controller.activities.base.AppBaseActivity;

public class AddNewCorporateActivity extends AppBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_corporate);
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
