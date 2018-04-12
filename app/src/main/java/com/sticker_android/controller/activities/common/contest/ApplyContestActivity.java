package com.sticker_android.controller.activities.common.contest;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.sticker_android.R;
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.controller.adaptors.ContestAdaptor;

import java.util.ArrayList;

/**
 * Class is used for the notification
 */
public class ApplyContestActivity extends AppBaseActivity {
    private RecyclerView recNotification;
    ArrayList<String> strings = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest);
        setViewReferences();
        setViewListeners();
        recyclerViewLayout();
        strings.add("hello test");
        setAdaptor();

    }

    private void setAdaptor() {
        ContestAdaptor contestAdaptor = new ContestAdaptor(this,strings);
        recNotification.setAdapter(contestAdaptor);
    }

    /**
     * Method is used to set the layout on recycler view
     */
    private void recyclerViewLayout() {
        recNotification.hasFixedSize();

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);

        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recNotification.setLayoutManager(mLayoutManager);
    }


    @Override
    protected void setViewListeners() {

    }

    @Override
    protected void setViewReferences() {
        recNotification = findViewById(R.id.recNotification);
    }

    @Override
    protected boolean isValidData() {
        return false;
    }




}
