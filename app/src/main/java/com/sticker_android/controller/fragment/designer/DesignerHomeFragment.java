package com.sticker_android.controller.fragment.designer;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sticker_android.R;
import com.sticker_android.controller.activities.corporate.addnew.AddNewCorporateActivity;
import com.sticker_android.controller.activities.designer.addnew.AddNewDesignActivity;
import com.sticker_android.controller.adaptors.ViewPagerAdapter;
import com.sticker_android.controller.fragment.base.BaseFragment;
import com.sticker_android.controller.fragment.corporate.CorporateHomeFragment;
import com.sticker_android.controller.fragment.corporate.ad.AdsFragment;
import com.sticker_android.controller.fragment.corporate.product.ProductsFragment;
import com.sticker_android.model.User;
import com.sticker_android.model.enums.DesignType;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.sharedpref.AppPref;

import java.lang.reflect.Field;
import java.util.Locale;

import retrofit2.Call;

/**
 * Created by user on 29/3/18.
 */

public class DesignerHomeFragment extends BaseFragment implements View.OnClickListener, SearchView.OnQueryTextListener{

    private FloatingActionButton fabAddNew;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ViewPagerAdapter adapter;
    private AppPref appPref;
    private User mUserdata;
    private int index;
    private Call<ApiResponse> apiResponseCall;
    private SearchView searchView;
    private MenuItem item;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_designer_home, container, false);

        init();
        getuserInfo();
        setViewReferences(view);
        setViewListeners();
        setupViewPager();
        Utils.setTabLayoutDivider(tabLayout);

        addFragmentToTab();
        setSelectedTabColor();
        setBackground();
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        setHasOptionsMenu(true);
        return view;
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
        fabAddNew.setOnClickListener(this);
        //tabLayout.addOnTabSelectedListener(new CorporateHomeFragment.TabListeners(viewPager));
    }

    @Override
    protected void setViewReferences(View view) {
        fabAddNew = (FloatingActionButton) view.findViewById(R.id.fabAddNew);
        viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        tabLayout = (TabLayout) view.findViewById(R.id.act_landing_tab);
    }

    private void setSelectedTabColor() {
        tabLayout.setTabTextColors(Color.parseColor("#77FFFFFF"), Color.WHITE);
    }

    private void setupViewPager() {
        tabLayout.setupWithViewPager(viewPager);
    }

    private void addFragmentToTab() {
        adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new StickerFragment(), getString(R.string.stickers));
        adapter.addFragment(new GIFFragment(), getString(R.string.gif));
        adapter.addFragment(new EmojiFragment(), getString(R.string.emoji));
        viewPager.setAdapter(adapter);
    }

    private void setBackground() {
        tabLayout.setBackground(getResources().getDrawable(R.drawable.side_nav_designer));
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.fabAddNew:
                startActivityForResult(new Intent(getActivity(), AddNewDesignActivity.class), 11);
                getActivity().overridePendingTransition(R.anim.activity_animation_enter, R.anim.activity_animation_exit);
                break;
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
                searchView.setQueryHint(getString(R.string.search) + " " +Utils.capitlizeText(getSelectedType()));
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


    /*@Override
    public boolean onQueryTextSubmit(String query) {
        //   Toast.makeText(getApplicationContext(),"wjcj",Toast.LENGTH_SHORT).show();

        String type = getSelectedType();
        if (apiResponseCall != null) {
            apiResponseCall.cancel();
        }
        //   searchApiCall(query, type);

        *//*searchResult(query);*//*

        Utils.hideKeyboard(getActivity());
        searchView.setIconified(true);
        searchView.clearFocus();
        searchView.setQuery("", false);
        MenuItemCompat.collapseActionView(item);
        return true;
    }*/

    /**
     * Method is used to get the type of posted product     *
     * @return rerurns the type
     */
    public String getSelectedType() {
        String type = DesignType.stickers.getType();
        if (tabLayout.getSelectedTabPosition() == 0){
            type = DesignType.stickers.getType();
        }
        else if (tabLayout.getSelectedTabPosition() == 1){
            type = DesignType.gif.getType().toUpperCase(Locale.US);
        }
        else if(tabLayout.getSelectedTabPosition() == 2){
            type = DesignType.emoji.getType();
        }
        return type;
    }
}
