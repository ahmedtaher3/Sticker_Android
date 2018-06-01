package com.sticker_android.controller.fragment.fan.fanhome;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.google.gson.Gson;
import com.sticker_android.R;
import com.sticker_android.controller.activities.filter.FanFilterActivity;
import com.sticker_android.controller.fragment.base.BaseFragment;
import com.sticker_android.controller.fragment.fan.FilterFragment;
import com.sticker_android.model.User;
import com.sticker_android.model.corporateproduct.Category;
import com.sticker_android.model.enums.DesignType;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.AppLogger;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.sharedpref.AppPref;
import com.sticker_android.view.BottomSheetFragment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class FanHomeFragment extends BaseFragment implements SearchView.OnQueryTextListener {

    private final String TAG = FanHomeFragment.class.getSimpleName();
    private TabLayout tabLayout;
    private AppPref appPref;
    private User mUserdata;
    private RelativeLayout rlFragmentContainer;
    private int index;
    private Call<ApiResponse> apiResponseCall;
    private SearchView searchView;
    private MenuItem item;
    private FragmentManager mFragmentManager;
    private MenuItem itemFilter;
    private ArrayList<Category> categoryList = new ArrayList<>();

    private FilterFragment mFilterFragment = new FilterFragment();;


    public FanHomeFragment() {
        // Required empty public constructor
        AppLogger.debug(TAG, "FanHomeFragment new instance created");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fan_home, container, false);
        init();
        getuserInfo();
        setViewReferences(view);
        setViewListeners();
        mFragmentManager = getChildFragmentManager();
        addTabsDynamically();
        setSelectedTabColor();
        setBackground();
        tabLayout.setMinimumWidth(200);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabLayout.setSelectedTabIndicatorColor(Color.TRANSPARENT);
        setHasOptionsMenu(true);

        replaceFragment(mFilterFragment);
        fetchCategoryApi();
        return view;
    }


    private void fetchCategoryApi() {

        Call<ApiResponse> apiResponseCall = RestClient.getService().apiCorporateCategoryList(mUserdata.getLanguageId(), mUserdata.getAuthrizedKey()
                , mUserdata.getId(), "corporate_category");

        apiResponseCall.enqueue(new ApiCall(getActivity()) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.status) {
                    categoryList = apiResponse.paylpad.corporateCategories;

                }

            }

            @Override
            public void onFail(Call<ApiResponse> call, Throwable t) {

            }
        });

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
                if (item != null)
                    MenuItemCompat.collapseActionView(item);
                if (itemFilter != null) {
                    itemFilter.setVisible(true);
                }

                switch (tab.getPosition()) {

                    case 0:
                        replaceFragment(mFilterFragment);
                        if (itemFilter != null) {
                            itemFilter.setVisible(false);
                        }
                        if (item != null)
                            item.setVisible(false);
                        break;
                    case 1:
                        if (itemFilter != null) {
                            itemFilter.setVisible(false);
                        }
                        setItemVisible();
                        replaceFragment(new FanContestFragment());
                        break;
                    case 2:
                        if (itemFilter != null) {
                            itemFilter.setVisible(false);
                        }
                        setItemVisible();
                        replaceFragment(new FanHomeAllFragment());
                        break;

                    case 3:
                        setItemVisible();
                        replaceFragment(new FanHomeStickerFragment());
                        break;
                    case 4:
                        setItemVisible();
                        replaceFragment(new FanHomeGifFragment());
                        break;
                    case 5:
                        setItemVisible();
                        replaceFragment(new FanHomeEmojiFragment());
                        break;
                    case 6:
                        setItemVisible();
                        replaceFragment(new FanHomeAdsFragment());
                        break;
                    case 7:
                        setItemVisible();
                        replaceFragment(new FanHomeProductsFragment());
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

    private void setItemVisible() {
        if (item != null)
            item.setVisible(true);
    }

    @Override
    protected void setViewReferences(View view) {
        tabLayout = (TabLayout) view.findViewById(R.id.act_landing_tab);
        rlFragmentContainer = (RelativeLayout) view.findViewById(R.id.rlFragmentContainer);

    }

    @Override
    protected boolean isValidData() {
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.fan_home_screen, menu);
        item = menu.findItem(R.id.search);
        itemFilter = menu.findItem(R.id.filter);
        itemFilter.setVisible(false);
        item.setVisible(false);
        searchView = (SearchView) MenuItemCompat.getActionView(item);
        //  setSearchTextColour(searchView);
        setSearchIcons(searchView);
        searchView.setOnQueryTextListener(this);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        Configuration config = getResources().getConfiguration();
        final boolean isLeftToRight;
        isLeftToRight = config.getLayoutDirection() != View.LAYOUT_DIRECTION_RTL;
        if (!isLeftToRight) {
            View xIcon = ((ViewGroup) searchView.getChildAt(0)).getChildAt(2);
            xIcon.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setQueryHintText(searchView);
                //searchView.setQueryHint("Search " + Utils.capitlizeText(getSelectedType()) + " by name");

            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                return false;
            }
        });

        searchViewExpandListener(item);

    }

    private void setQueryHintText(SearchView searchView) {
        if (tabLayout.getSelectedTabPosition() == 0) {
            searchView.setQueryHint(getString(R.string.txt_search_filter_by_name));
            //type = DesignType.filter.getType();
        } else if (tabLayout.getSelectedTabPosition() == 1) {
            searchView.setQueryHint(getString(R.string.txt_search_contest_by_name));

        }else if (tabLayout.getSelectedTabPosition() == 2) {
            searchView.setQueryHint(getString(R.string.search));

        }

        else if (tabLayout.getSelectedTabPosition() == 3) {
            searchView.setQueryHint(getString(R.string.txt_search_stickers_by_name));

        } else if (tabLayout.getSelectedTabPosition() == 4) {
            //type = DesignType.gif.getType().toUpperCase(Locale.US);
            searchView.setQueryHint(getString(R.string.txt_search_gif_by_name));

        } else if (tabLayout.getSelectedTabPosition() == 5) {
            searchView.setQueryHint(getString(R.string.txt_search_emoji_by_name));

        } else if (tabLayout.getSelectedTabPosition() == 6) {
            searchView.setQueryHint(getString(R.string.txt_search_ad_by_name));

        } else if (tabLayout.getSelectedTabPosition() == 7) {
            searchView.setQueryHint(getString(R.string.search_product_by_name));

        }
    }

    private void searchViewExpandListener(MenuItem item) {

        MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                if (itemFilter != null) {
                    itemFilter.setVisible(false);
                }

                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Write your code here
                if (itemFilter != null) {
                    itemFilter.setVisible(true);
                }
                Fragment fragment = getChildFragmentManager().findFragmentById(R.id.container_fan_home);
                if (fragment instanceof FanContestFragment) {
                    if (itemFilter != null)
                        itemFilter.setVisible(false);
                }
                if (fragment instanceof FanHomeAllFragment) {
                    if (itemFilter != null)
                        itemFilter.setVisible(false);
                    ((FanHomeAllFragment) fragment).refreshApi();

                }

                if (fragment instanceof FanHomeStickerFragment) {
                    ((FanHomeStickerFragment) fragment).refreshApi();
                } else if (fragment instanceof FanHomeEmojiFragment)
                    ((FanHomeEmojiFragment) fragment).refreshApi();
                else if (fragment instanceof FanHomeGifFragment)
                    ((FanHomeGifFragment) fragment).refreshApi();

                else if (fragment instanceof FanHomeAdsFragment)
                    ((FanHomeAdsFragment) fragment).refreshApi();
                else if (fragment instanceof FanHomeProductsFragment)
                    ((FanHomeProductsFragment) fragment).refreshApi();

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
            type = DesignType.filter.getType();
        } else if (tabLayout.getSelectedTabPosition() == 1) {
            type = DesignType.contest.getType();
        } else if (tabLayout.getSelectedTabPosition() == 2) {
            type = DesignType.stickers.getType();
        } else if (tabLayout.getSelectedTabPosition() == 3) {
            type = DesignType.gif.getType().toUpperCase(Locale.US);
        } else if (tabLayout.getSelectedTabPosition() == 4) {
            type = DesignType.emoji.getType();
        } else if (tabLayout.getSelectedTabPosition() == 5) {
            type = DesignType.ads.getType();
        } else if (tabLayout.getSelectedTabPosition() == 6) {
            type = DesignType.products.getType();
        }
        return type;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:

                break;
            case R.id.filter:
                Intent intent = new Intent(getActivity(), FanFilterActivity.class);
                intent.putExtra("type", "" + tabLayout.getSelectedTabPosition());
                startActivityForResult(intent, 131);
                getActivity().overridePendingTransition(R.anim.activity_animation_enter,
                        R.anim.activity_animation_exit);

                //  showBottomSheetDialogFragment();

/*
                if (tabLayout.getSelectedTabPosition() == 0) {
                    Fragment fragment = getChildFragmentManager().findFragmentById(R.id.container_fan_home);
                    if (fragment instanceof FanHomeStickerFragment) {
                        Toast.makeText(getActivity(), "Under development", Toast.LENGTH_SHORT).show();
                        //
                        // ((FanHomeStickerFragment) fragment).searchData();
                    }*//*


                }*/
                break;
        }
        return true;
    }

    public void showBottomSheetDialogFragment() {

        BottomSheetFragment bottomSheetFragment = new BottomSheetFragment(categoryList, new BottomSheetFragment.IFilter() {
            @Override
            public void selectedCategory(String categories, String filterdata) {
                AppLogger.debug("vfdjvnjf", "nvd,fv=== " + categories);
                filterData(categories, filterdata);
            }
        }, getActivity());
        bottomSheetFragment.show(getChildFragmentManager(), "filter data");
    }

    private void filterData(String categories, String filterdata) {

        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.container_fan_home);
        if (fragment instanceof FanHomeStickerFragment) {
            ((FanHomeStickerFragment) fragment).filterData(categories, filterdata);
        }
        if (fragment instanceof FanHomeEmojiFragment) {
            ((FanHomeEmojiFragment) fragment).filterData(categories, filterdata);
        }
        if (fragment instanceof FanHomeGifFragment) {
            ((FanHomeGifFragment) fragment).filterData(categories, filterdata);
        }
        if (fragment instanceof FanHomeAdsFragment) {
            ((FanHomeAdsFragment) fragment).filterData(categories, filterdata);
        }
        if (fragment instanceof FanHomeProductsFragment) {
            ((FanHomeProductsFragment) fragment).filterData(categories, filterdata);
        }

    }

    private void setSelectedTabColor() {
        tabLayout.setTabTextColors(Color.parseColor("#77FFFFFF"), Color.WHITE);
    }

    private void setBackground() {
        tabLayout.setBackground(getResources().getDrawable(R.drawable.side_nav_fan));
    }

    public void addTabsDynamically() {


        TabLayout.Tab filterTab = tabLayout.newTab();
        filterTab.setText(R.string.txt_filter); // set the Text for the first Tab
        tabLayout.addTab(filterTab);

        TabLayout.Tab contestTab = tabLayout.newTab();
        contestTab.setText(R.string.txt_contest); // set the Text for the first Tab
        tabLayout.addTab(contestTab);

        TabLayout.Tab contestTabAll = tabLayout.newTab();
        contestTabAll.setText(R.string.txt_all_fan_home); // set the Text for the first Tab
        tabLayout.addTab(contestTabAll);

        TabLayout.Tab stickerTab = tabLayout.newTab();
        stickerTab.setText(getString(R.string.stickers)); // set the Text for the first Tab
        tabLayout.addTab(stickerTab);

        TabLayout.Tab gifTab = tabLayout.newTab();
        gifTab.setText(getString(R.string.gif)); // set the Text for the first Tab
        tabLayout.addTab(gifTab);

        TabLayout.Tab emojiTab = tabLayout.newTab();
        emojiTab.setText(getString(R.string.emoji)); // set the Text for the first Tab
        tabLayout.addTab(emojiTab);

        TabLayout.Tab AdsTab = tabLayout.newTab();
        AdsTab.setText(getString(R.string.txt_ads_frag)); // set the Text for the first Tab
        tabLayout.addTab(AdsTab);

        TabLayout.Tab productsTab = tabLayout.newTab();
        productsTab.setText(getString(R.string.txt_products_frag)); // set the Text for the first Tab
        tabLayout.addTab(productsTab);

        Utils.setTabLayoutDivider(tabLayout, getActivity());
    }

    /**
     * replace existing fragment of container
     *
     * @param fragment
     */
    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction =
                getChildFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container_fan_home,
                fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        //   Toast.makeText(getApplicationContext(),"wjcj",Toast.LENGTH_SHORT).show();
        if (query.isEmpty()) {
            Utils.showToast(getActivity(), "Search cannot be empty.");
        } else {
            searchResult(query);
        }
        searchView.setIconified(false);
        searchView.clearFocus();
        //    MenuItemCompat.collapseActionView(item);

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    private void searchResult(String query) {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.container_fan_home);
        if (fragment instanceof FanHomeStickerFragment) {
            ((FanHomeStickerFragment) fragment).searchData(query.trim());
        }

        if (fragment instanceof FanHomeAllFragment) {
            ((FanHomeAllFragment) fragment).searchData(query.trim());
        }
        if (fragment instanceof FanHomeEmojiFragment) {
            ((FanHomeEmojiFragment) fragment).searchData(query.trim());
        }
        if (fragment instanceof FanHomeGifFragment) {
            ((FanHomeGifFragment) fragment).searchData(query.trim());
        }
        if (fragment instanceof FanHomeAdsFragment) {
            ((FanHomeAdsFragment) fragment).searchData(query.trim());
        }
        if (fragment instanceof FanHomeProductsFragment) {
            ((FanHomeProductsFragment) fragment).searchData(query.trim());
        }
        if (fragment instanceof FanContestFragment) {
            ((FanContestFragment) fragment).searchData(query.trim());
        }


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment f = getChildFragmentManager().findFragmentById(R.id.container_fan_home);

        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == 131) {
                if (data.getExtras() != null) {
                    Bundle b = data.getExtras();
                    String filterByName = b.getString("filterBy");
                    ArrayList<Category> categoryList = b.getParcelableArrayList("categoryList");
                    if (categoryList != null) {
                        filterData(filterListdata(categoryList), filterByName);
                    }
                }
            } else if (f instanceof FilterFragment) {
                ((FilterFragment) f).onActivityResult(requestCode, resultCode, data);
            }


        }
        if (requestCode == 333)
            AppLogger.debug("Fan home", "on activity result called inside 333 fan home");
        Fragment f1 = getChildFragmentManager().findFragmentById(R.id.container_fan_home);
        if (f1 instanceof FanHomeAdsFragment) {
            ((FanHomeAdsFragment) f).onActivityResult(requestCode, resultCode, data);
        }
        if (f1 instanceof FanHomeEmojiFragment) {
            ((FanHomeEmojiFragment) f).onActivityResult(requestCode, resultCode, data);
        }
        if (f1 instanceof FanHomeGifFragment) {
            ((FanHomeGifFragment) f).onActivityResult(requestCode, resultCode, data);
        }
        if (f1 instanceof FanHomeProductsFragment) {
            ((FanHomeProductsFragment) f).onActivityResult(requestCode, resultCode, data);
        }
        if (f1 instanceof FanHomeStickerFragment) {
            ((FanHomeStickerFragment) f).onActivityResult(requestCode, resultCode, data);
        }
        if (f1 instanceof FanHomeAllFragment) {
            ((FanHomeAllFragment) f).onActivityResult(requestCode, resultCode, data);
        }
      /*  for (Fragment fragment : getChildFragmentManager().getFragments()) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }*/

    }

    private String filterListdata(ArrayList<Category> categoryList) {
        ArrayList<Integer> categoryArray = new ArrayList<>();
        AppLogger.debug("vfdjvnjf", "nvd,fv");

        for (Category category : categoryList
                ) {
            if (category.isChecked) {
                categoryArray.add(category.categoryId);
            }
        }
        Gson gson = new Gson();
        String jsonNames = gson.toJson(categoryArray);
        return jsonNames;

    }

    public void closeSearch() {
        if (item != null)
            MenuItemCompat.collapseActionView(item);
    }
}
