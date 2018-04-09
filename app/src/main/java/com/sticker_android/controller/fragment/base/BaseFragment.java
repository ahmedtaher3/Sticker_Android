package com.sticker_android.controller.fragment.base;

import android.support.v4.app.Fragment;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sticker_android.utils.Utils;

/**
 * Created by user on 23/3/18.
 */

public abstract class BaseFragment extends Fragment {
    public ImageLoader imageLoader = ImageLoader.getInstance();

    /**
     * Method is used to set the listeners on views
     */
    protected abstract void setViewListeners();

    /**
     * Method is used to set the references of views
     */
    protected abstract void setViewReferences(View view);

    abstract protected boolean isValidData();

    @Override
    public void onPause() {
        super.onPause();
        Utils.hideKeyboard(getActivity());
    }
}
