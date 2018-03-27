package com.sticker_android.controller.activities.common.terms;

import android.os.Bundle;

import com.sticker_android.R;
import com.sticker_android.controller.activities.base.AppBaseActivity;

public class TermsActivity extends AppBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
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
