package com.sticker_android.controller.fragment.base;

import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by user on 23/3/18.
 */

public abstract class BaseFragment extends Fragment {

    /**
     * Method is used to set the listeners on views
     */
    protected abstract void setViewListeners();

    /**
     * Method is used to set the references of views
     */
    protected abstract void setViewReferences(View view);

    abstract protected boolean isValidData();
}
