package com.sticker_android.controller.activities.common.contentapprovaldetails;

import android.os.Bundle;

import com.sticker_android.R;
import com.sticker_android.controller.activities.base.AppBaseActivity;

public class ContentApprovalDetails extends AppBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_approval_details);
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
