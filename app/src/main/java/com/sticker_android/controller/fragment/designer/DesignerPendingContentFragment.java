package com.sticker_android.controller.fragment.designer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sticker_android.R;
import com.sticker_android.constant.AppConstant;
import com.sticker_android.controller.activities.designer.addnew.AddNewDesignActivity;
import com.sticker_android.controller.fragment.base.BaseFragment;
import com.sticker_android.controller.fragment.corporate.ad.AdsFragment;
import com.sticker_android.controller.fragment.corporate.product.ProductsFragment;
import com.sticker_android.model.User;
import com.sticker_android.model.corporateproduct.Product;
import com.sticker_android.model.enums.DesignType;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.utils.AppLogger;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.sharedpref.AppPref;

import java.lang.reflect.Field;
import java.util.Locale;

import retrofit2.Call;

/**
 * Created by user on 29/3/18.
 */

public class DesignerPendingContentFragment extends BaseFragment implements View.OnClickListener, SearchView.OnQueryTextListener {

    private final String TAG = DesignerHomeFragment.class.getSimpleName();
    private TabLayout tabLayout;
    private AppPref appPref;
    private User mUserdata;
    private RelativeLayout rlFragmentContainer;
    private int index;
    private Call<ApiResponse> apiResponseCall;
    private SearchView searchView;
    private MenuItem item;
    private FragmentManager mFragmentManager;

    private final int ADD_NEW_DESIGN = 11;
    public static final int EDIT_DESIGN = 12;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.frag_desiner_content_for_approval, container, false);

        init();
        getuserInfo();
        setViewReferences(view);
        setViewListeners();
        mFragmentManager = getChildFragmentManager();

        addTabsDynamically();
        setSelectedTabColor();
        setBackground();
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        replaceFragmentOfContainer(mFragmentManager, new StickerFragment());
        setHasOptionsMenu(true);
        return view;
    }

    public void addTabsDynamically() {

        TabLayout.Tab stickerTab = tabLayout.newTab();
        stickerTab.setText(getString(R.string.stickers)); // set the Text for the first Tab
        tabLayout.addTab(stickerTab);

        TabLayout.Tab gifTab = tabLayout.newTab();
        gifTab.setText(getString(R.string.gif)); // set the Text for the first Tab
        tabLayout.addTab(gifTab);

        TabLayout.Tab emojiTab = tabLayout.newTab();
        emojiTab.setText(getString(R.string.emoji)); // set the Text for the first Tab
        tabLayout.addTab(emojiTab);

        Utils.setTabLayoutDivider(tabLayout, getActivity());
    }

    @Override
    protected boolean isValidData() {
        return false;
    }

    private void init() {
        appPref = new AppPref(getActivity());
    }

    private void getuserInfo() {
        mUserdata = appPref.getUserInfo();
    }

    @Override
    protected void setViewListeners() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(item != null){
                    item.collapseActionView();
                }
                switch (tab.getPosition()) {
                    case 0:
                        replaceFragmentOfContainer(mFragmentManager, new StickerFragment());
                        break;
                    case 1:
                        replaceFragmentOfContainer(mFragmentManager, new GIFFragment());
                        break;
                    case 2:
                        replaceFragmentOfContainer(mFragmentManager, new EmojiFragment());
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(item != null && searchView.getQuery().length() != 0){
            item.collapseActionView();
        }
    }

    /**
     * replace existing fragment of container
     *
     * @param fragment
     */
    private void replaceFragmentOfContainer(FragmentManager manager, Fragment fragment) {
        String backStateName = fragment.getClass().getName();
        String fragmentTag = backStateName;

        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);
        if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null) {
            AppLogger.error(TAG, "Fragment does not exist.");
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.rlFragmentContainer, fragment, fragmentTag);
            ft.addToBackStack(backStateName);
            ft.commitAllowingStateLoss();
        } else {
            AppLogger.error(TAG, "Fragment already exist.");
        }
    }

    public void updateAttachedVisibleFragment(){
        Fragment fragment = mFragmentManager.findFragmentById(R.id.rlFragmentContainer);

        if(fragment != null && fragment instanceof StickerFragment){
            ((StickerFragment)fragment).updateTheFragment();
        }
        else if(fragment != null && fragment instanceof GIFFragment){
            ((GIFFragment)fragment).updateTheFragment();
        }
        else if(fragment != null && fragment instanceof EmojiFragment){
            ((EmojiFragment)fragment).updateTheFragment();
        }

    }

    @Override
    protected void setViewReferences(View view) {
        tabLayout = (TabLayout) view.findViewById(R.id.act_landing_tab);
        rlFragmentContainer = (RelativeLayout) view.findViewById(R.id.rlFragmentContainer);
    }

    private void setSelectedTabColor() {
        tabLayout.setTabTextColors(Color.parseColor("#77FFFFFF"), Color.WHITE);
    }

    private void setBackground() {
        tabLayout.setBackground(getResources().getDrawable(R.drawable.side_nav_designer));
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        AppLogger.error(TAG, "Search text => " + query);
        searchResult(query.trim());
        searchView.setIconified(false);
        searchView.clearFocus();
        return false;
    }

    private void searchResult(String query) {
        Fragment fragment = mFragmentManager.findFragmentById(R.id.rlFragmentContainer);
        if(fragment != null && fragment instanceof StickerFragment){
            ((StickerFragment)fragment).searchByKeyword(query);
        }
        else if(fragment != null && fragment instanceof GIFFragment){
            ((GIFFragment)fragment).searchByKeyword(query);
        }
        else if(fragment != null && fragment instanceof EmojiFragment){
            ((EmojiFragment)fragment).searchByKeyword(query);
        }
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.fabAddNew:
                startActivityForResult(new Intent(getActivity(), AddNewDesignActivity.class), ADD_NEW_DESIGN);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ADD_NEW_DESIGN && resultCode == Activity.RESULT_OK && data != null){

            final Product product = data.getParcelableExtra(AppConstant.PRODUCT);
            if(tabLayout.getSelectedTabPosition() == 0
                    && product.getType().equalsIgnoreCase(DesignType.stickers.getType())){

                Fragment fragment = mFragmentManager.findFragmentById(R.id.rlFragmentContainer);
                if(fragment != null && fragment instanceof StickerFragment){
                    ((StickerFragment)fragment).addNewSticker((Product) data.getParcelableExtra(AppConstant.PRODUCT));
                }
            }
            else if(tabLayout.getSelectedTabPosition() == 1
                    && product.getType().equalsIgnoreCase(DesignType.gif.getType())){
                Fragment fragment = mFragmentManager.findFragmentById(R.id.rlFragmentContainer);
                if(fragment != null && fragment instanceof GIFFragment){
                    ((GIFFragment)fragment).addNewGIF((Product) data.getParcelableExtra(AppConstant.PRODUCT));
                }
            }
            else if(tabLayout.getSelectedTabPosition() == 2
                    && product.getType().equalsIgnoreCase(DesignType.emoji.getType())){
                Fragment fragment = mFragmentManager.findFragmentById(R.id.rlFragmentContainer);
                if(fragment != null && fragment instanceof EmojiFragment){
                    ((EmojiFragment)fragment).addNewEmoji((Product) data.getParcelableExtra(AppConstant.PRODUCT));
                }
            }
            else {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        if(product.getType().equalsIgnoreCase(DesignType.stickers.getType())){
                            tabLayout.getTabAt(0).select();
                        }
                        else if(product.getType().equalsIgnoreCase(DesignType.gif.getType())){
                            tabLayout.getTabAt(1).select();
                        }
                        else if(product.getType().equalsIgnoreCase(DesignType.emoji.getType())){
                            tabLayout.getTabAt(2).select();
                        }
                    }
                });
            }
        }
        else if(requestCode == EDIT_DESIGN && resultCode == Activity.RESULT_OK && data != null){
            final Product product = data.getParcelableExtra(AppConstant.PRODUCT);

            if(tabLayout.getSelectedTabPosition() == 0
                    && product.getType().equalsIgnoreCase(DesignType.stickers.getType())){

                Fragment fragment = mFragmentManager.findFragmentById(R.id.rlFragmentContainer);
                if(fragment != null && fragment instanceof StickerFragment){
                    ((StickerFragment)fragment).editSticker((Product) data.getParcelableExtra(AppConstant.PRODUCT));
                }
            }
            else if(tabLayout.getSelectedTabPosition() == 1
                    && product.getType().equalsIgnoreCase(DesignType.gif.getType())){
                Fragment fragment = mFragmentManager.findFragmentById(R.id.rlFragmentContainer);
                if(fragment != null && fragment instanceof GIFFragment){
                    ((GIFFragment)fragment).editGif((Product) data.getParcelableExtra(AppConstant.PRODUCT));
                }
            }
            else if(tabLayout.getSelectedTabPosition() == 2
                    && product.getType().equalsIgnoreCase(DesignType.emoji.getType())){
                Fragment fragment = mFragmentManager.findFragmentById(R.id.rlFragmentContainer);
                if(fragment != null && fragment instanceof EmojiFragment){
                    ((EmojiFragment)fragment).editEmoji((Product) data.getParcelableExtra(AppConstant.PRODUCT));
                }
            }
            else {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        if(product.getType().equalsIgnoreCase(DesignType.stickers.getType())){
                            tabLayout.getTabAt(0).select();
                        }
                        else if(product.getType().equalsIgnoreCase(DesignType.gif.getType())){
                            tabLayout.getTabAt(1).select();
                        }
                        else if(product.getType().equalsIgnoreCase(DesignType.emoji.getType())){
                            tabLayout.getTabAt(2).select();
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.designer_home_menu, menu);

        item = menu.findItem(R.id.search);
        searchView = (SearchView) MenuItemCompat.getActionView(item);
        setSearchIcons(searchView);
        searchView.setOnQueryTextListener(this);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setQueryHint(getString(R.string.search) + " " + Utils.capitlizeText(getSelectedType()));
            }
        });

        searchViewExpandListener(item);
    }

    private void searchViewExpandListener(MenuItem item) {

        MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Write your code here
                Fragment fragment = mFragmentManager.findFragmentById(R.id.rlFragmentContainer);
                if (fragment != null && fragment instanceof StickerFragment) {
                    ((StickerFragment) fragment).updateTheFragment();
                } else if (fragment != null && fragment instanceof GIFFragment) {
                    ((GIFFragment) fragment).updateTheFragment();
                } else if (fragment != null && fragment instanceof EmojiFragment) {
                    ((EmojiFragment) fragment).updateTheFragment();
                }
                return true;
            }
        });
    }

    private void setSearchIcons(SearchView searchView) {
        try {
            Field searchField = SearchView.class.getDeclaredField("mCloseButton");
            searchField.setAccessible(true);
            ImageView closeBtn = (ImageView) searchField.get(searchView);
            closeBtn.setImageResource(R.drawable.close_search);
        } catch (NoSuchFieldException e) {

        } catch (IllegalAccessException e) {
        }
    }

    /**
     * Method is used to get the type of posted product     *
     *
     * @return rerurns the type
     */
    public String getSelectedType() {
        String type = DesignType.stickers.getType();
        if (tabLayout.getSelectedTabPosition() == 0) {
            type = DesignType.stickers.getType();
        } else if (tabLayout.getSelectedTabPosition() == 1) {
            type = DesignType.gif.getType().toUpperCase(Locale.US);
        } else if (tabLayout.getSelectedTabPosition() == 2) {
            type = DesignType.emoji.getType();
        }
        return type;
    }
}
