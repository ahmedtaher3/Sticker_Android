package com.sticker_android.controller.fragment.corporate;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sticker_android.R;
import com.sticker_android.controller.activities.corporate.addnew.AddNewCorporateActivity;
import com.sticker_android.controller.adaptors.ViewPagerAdapter;
import com.sticker_android.controller.fragment.ChangePasswordFragment;
import com.sticker_android.controller.fragment.base.BaseFragment;
import com.sticker_android.controller.fragment.corporate.ad.AdsFragment;
import com.sticker_android.controller.fragment.corporate.product.ProductsFragment;
import com.sticker_android.model.User;
import com.sticker_android.model.corporateproduct.Product;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.ProgressDialogHandler;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.fragmentinterface.UpdateToolbarTitle;
import com.sticker_android.utils.sharedpref.AppPref;

import java.lang.reflect.Field;
import java.util.ArrayList;

import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class CorporateHomeFragment extends BaseFragment implements View.OnClickListener, SearchView.OnQueryTextListener{

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
    private UpdateToolbarTitle mUpdateToolbarCallback;



    public static CorporateHomeFragment newInstance() {
        CorporateHomeFragment f = new CorporateHomeFragment();

        return f;
    }
    public CorporateHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_corporate_home, container, false);
        init();
        getuserInfo();
        setViewReferences(view);
        setViewListeners();
        setupViewPager();

        Utils.setTabLayoutDivider(tabLayout, getActivity());
        addFragmentToTab();
        setSelectedTabColor();
        setBackground();
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        setHasOptionsMenu(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //   mUpdateToolbarCallback.updateToolbarTitle(getResources().getString(R.string.txt_home));
            }
        }, 300);
        swipeListener();
        return view;

    }

    private void swipeListener() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int i, final float v, final int i2) {

            }
            @Override
            public void onPageSelected(final int i) {
                if(i==0) {
                    AdsFragment fragment = (AdsFragment) adapter.instantiateItem(viewPager, i);
                    if (fragment != null) {
                        fragment.onRefresh();
                    }
                }else{
                    ProductsFragment fragment = (ProductsFragment) adapter.instantiateItem(viewPager, i);
                    if (fragment != null) {
                        fragment.onRefresh();
                    }
                }
            }
            @Override
            public void onPageScrollStateChanged(final int i) {
            }
        });

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        tabLayout.addOnTabSelectedListener(new TabListeners(viewPager));
    }

    @Override
    protected void setViewReferences(View view) {
        fabAddNew = (FloatingActionButton) view.findViewById(R.id.fabAddNew);
        viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        tabLayout = (TabLayout) view.findViewById(R.id.act_landing_tab);
    }

    private void setSelectedTabColor() {
        tabLayout.setTabTextColors(Color.parseColor("#AAFFFFFF"), Color.WHITE);
    }

    private void setupViewPager() {
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected boolean isValidData() {
        return false;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.fabAddNew:

                startActivityForResult(new Intent(getActivity(), AddNewCorporateActivity.class), 11);
                getActivity().overridePendingTransition(R.anim.activity_animation_enter, R.anim.activity_animation_exit);
                break;
        }
    }

    private void addFragmentToTab() {
        adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new AdsFragment(), getString(R.string.txt_my_ads_frag));
        adapter.addFragment(new ProductsFragment(), getString(R.string.txt_my_products_frag));
        viewPager.setAdapter(adapter);

    }

    private void setBackground() {
        tabLayout.setBackground(getResources().getDrawable(R.drawable.side_nav_corporate));
    }

    /**
     * Method is used to get the type of posted product
     *
     * @return rerurns the type
     */
    public String getSelectedType() {
        String type = "ads";
        if (tabLayout.getSelectedTabPosition() == 1)
            type = "product";
        return type;
    }


    public void clearSearch() {
        if(searchView!=null)
        MenuItemCompat.collapseActionView(item);
    }


    public class TabListeners implements TabLayout.OnTabSelectedListener {

        private ViewPager viewPager;

        public TabListeners(ViewPager viewPager) {
            this.viewPager = viewPager;
        }

        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            Utils.hideKeyboard(getActivity());
            viewPager.setCurrentItem(tab.getPosition());
            Fragment fragment = adapter.getItem(tab.getPosition());
            if (fragment instanceof ChangePasswordFragment) {
                ((ChangePasswordFragment) fragment).clearField();
            }
            if (searchView != null) {
                MenuItemCompat.collapseActionView(item);
            }
        }


        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.corporate_menu, menu);

        item = menu.findItem(R.id.search);
        searchView = (SearchView) MenuItemCompat.getActionView(item);
      //  setSearchTextColour(searchView);
        setSearchIcons(searchView);
        searchView.setOnQueryTextListener(this);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setQueryHint("Search "+Utils.capitlizeText(getSelectedType())+" by name");

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
                int tab = tabLayout.getSelectedTabPosition();
                Fragment fragment = adapter.getItem(tab);
                if (fragment instanceof AdsFragment) {
                    ((AdsFragment) fragment).refreshApi();
                } else if (fragment instanceof ProductsFragment)
                    ((ProductsFragment) fragment).refreshApi();

                return true;
            }
        });
    }

    private void setSearchTextColour(SearchView searchView) {

        /*EditText searchBox = ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
        searchBox.setTextColor(getActivity().getResources().getColor(R.color.colorWhiteTransparent));
        searchBox.setHintTextColor(getActivity().getResources().getColor(R.color.colorTabSearchHint));
        searchBox.setTextColor(Color.WHITE);
        searchBox.setText(getSelectedType());
        searchView.setQueryHint(getSelectedType());
<<<<<<< HEAD
        ImageView searchButton = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_button);
        searchButton.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        searchButton.setImageResource(R.drawable.ic_search);
        ImageView searchIcon = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_mag_icon);
        searchIcon.setImageResource(R.drawable.ic_search);
        SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchAutoComplete.setHintTextColor(Color.WHITE);
        searchAutoComplete.setTextColor(Color.WHITE);*/
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


    @Override
    public boolean onQueryTextSubmit(String query) {
        //   Toast.makeText(getApplicationContext(),"wjcj",Toast.LENGTH_SHORT).show();

        Utils.hideKeyboard(getActivity());
        String type = getSelectedType();
        if (apiResponseCall != null) {
            apiResponseCall.cancel();
        }
        //   searchApiCall(query, type);

        if (query.isEmpty()) {
            Utils.showToast(getActivity(), "Search cannot be empty.");
        } else
            searchResult(query);
        searchView.setIconified(false);
        searchView.clearFocus();
        //   MenuItemCompat.collapseActionView(item);
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (item != null)
            MenuItemCompat.collapseActionView(item);
    }

    /**
     * Method is used to pass the search result to the fragment
     *
     * @param query
     */
    private void searchResult(String query) {

        int tab = tabLayout.getSelectedTabPosition();
        Fragment fragment = adapter.getItem(tab);
        if (fragment instanceof AdsFragment) {
            ((AdsFragment) fragment).searchProduct(query);
        } else if (fragment instanceof ProductsFragment)
            ((ProductsFragment) fragment).searchProduct(query);


    }

    @Override
    public boolean onQueryTextChange(String newText) {

        //  Toast.makeText(getApplicationContext(),"on text change",Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    /**
     * Method is used for searching the product
     *
     * @param query
     * @param type
     */
    public void searchApiCall(String query, String type) {
        final ProgressDialogHandler progressDialogHandler = new ProgressDialogHandler(getActivity());
        progressDialogHandler.show();

        apiResponseCall = RestClient.getService().apiSearchProduct(mUserdata.getLanguageId(),
                mUserdata.getAuthrizedKey(), mUserdata.getId(), index, 10, type, query, "product_list");

        apiResponseCall.enqueue(new ApiCall(getActivity()) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                progressDialogHandler.hide();
                if (apiResponse.status) {

                    showSearchResultApi(apiResponse.paylpad.productList);
                }
            }

            @Override
            public void onFail(Call<ApiResponse> call, Throwable t) {
                progressDialogHandler.hide();

            }
        });


    }

    private void showSearchResultApi(ArrayList<Product> product) {

        int tab = tabLayout.getSelectedTabPosition();
        Fragment fragment = adapter.getItem(tab);
        if (fragment instanceof AdsFragment) {
            ((AdsFragment) fragment).searchProduct(product);
        } else if (fragment instanceof ProductsFragment)
            ((ProductsFragment) fragment).searchProduct(product);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 11:
                int tab = tabLayout.getSelectedTabPosition();
                Fragment fragment = adapter.getItem(tab);
                if (fragment instanceof AdsFragment) {
                    ((AdsFragment) fragment).refreshList();
                } else if (fragment instanceof ProductsFragment)
                    ((ProductsFragment) fragment).refreshList();


                break;

        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mUpdateToolbarCallback = (UpdateToolbarTitle) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }


    public void refreshSearch(){



    }

}
