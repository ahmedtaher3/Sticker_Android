package com.sticker_android.controller.activities.common.main;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;

import com.sticker_android.R;
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.controller.fragment.SignInFragment;
import com.sticker_android.controller.fragment.SignUpFragment;

/**
 * Class is used as a landing activity for all the users
 */
public class MainActivity extends AppBaseActivity  implements SignInFragment.SignUpCallback{



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar  toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();
        toolbar.setTitle("");
        setViewReferences();
        loadFragment(new SignInFragment());
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



    @Override
    public void isClicked() {
      loadFragment(new SignUpFragment());
        getSupportActionBar().show();
    }


    private void loadFragment(Fragment fragment) {
// create a FragmentManager
        FragmentManager fm = getSupportFragmentManager();
// create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
// replace the FrameLayout with new Fragment
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit(); // save the changes
    }

}
