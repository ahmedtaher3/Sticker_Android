package com.sticker_android.controller.activities.fan.home;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sticker_android.R;
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.controller.activities.common.Refresh;
import com.sticker_android.controller.adaptors.VotesAdapter;
import com.sticker_android.model.NewResponse;
import com.sticker_android.model.User;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.UserTypeEnum;
import com.sticker_android.utils.sharedpref.AppPref;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyVotes extends AppBaseActivity implements Refresh {


    private AppPref appPref;
    private User user;
    private Toolbar toolbar;

    FloatingActionButton floatingActionButton;
    RecyclerView votes_recycler;
    VotesAdapter votesAdapter;
    SwipeRefreshLayout pullToRefresh;
    RecyclerView.LayoutManager layoutManager;
    String Flag = "MY_VOTES";


    private ImageView imv_nav_drawer_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_votes);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        init();
        setViewReferences();
        setViewListeners();
        setToolBarTitle();
        setToolbarBackground(toolbar);
        setSupportActionBar(toolbar);
        pullToRefresh.setRefreshing(true);
        downloadApi();
    }

    void init() {
        appPref = new AppPref(this);
        user = appPref.getUserInfo();
    }


    private void setToolbarBackground(Toolbar toolbar) {


        if (appPref.getLoginFlag(false)) {
            UserTypeEnum userTypeEnum = Enum.valueOf(UserTypeEnum.class, user.getUserType().toUpperCase());

            switch (userTypeEnum) {
                case FAN:
                    toolbar.setBackground(getResources().getDrawable(R.drawable.fan_header_xhdpi));
                    break;
                case DESIGNER:
                    toolbar.setBackground(getResources().getDrawable(R.drawable.gradient_bg_des_hdpi));

                    break;
                case CORPORATE:
                    toolbar.setBackground(getResources().getDrawable(R.drawable.gradient_bg_hdpi));

                    break;
            }
        } else {
            toolbar.setBackground(getResources().getDrawable(R.drawable.fan_header_xhdpi));

        }


    }

    private void setToolBarTitle() {
        TextView textView = (TextView) toolbar.findViewById(R.id.tvToolbar);
        textView.setText(getResources().getString(R.string.my_votes));
        toolbar.setTitle("");
        centerToolbarText(toolbar, textView);
    }

    private void centerToolbarText(final Toolbar toolbar, final TextView textView) {
        toolbar.postDelayed(new Runnable() {
            @Override
            public void run() {
                int maxWidth = toolbar.getWidth();
                int titleWidth = textView.getWidth();
                int iconWidth = maxWidth - titleWidth;

                if (iconWidth > 0) {
                    //icons (drawer, menu) are on left and right side
                    int width = maxWidth - iconWidth * 2;
                    textView.setMinimumWidth(width);
                    textView.getLayoutParams().width = width;
                }
            }
        }, 0);
    }


    @Override
    protected void setViewListeners() {

    }

    @Override
    protected void setViewReferences() {
        imv_nav_drawer_menu = (ImageView) findViewById(R.id.imv_nav_drawer_menu);
        pullToRefresh = (SwipeRefreshLayout) findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                downloadApi();
            }
        });
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fabAddNew);
        votes_recycler = (RecyclerView) findViewById(R.id.votes_recycler);
        layoutManager = new LinearLayoutManager(getActivity());
        votes_recycler.setLayoutManager(layoutManager);



        if (appPref.getLoginFlag(false)) {
            floatingActionButton.setVisibility(View.VISIBLE);
        } else {
            floatingActionButton.setVisibility(View.GONE);

        }
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), AddNewVote.class);
                startActivityForResult(intent, 0);

            }
        });

        imv_nav_drawer_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected boolean isValidData() {
        return false;
    }

    private void downloadApi() {


        Call<NewResponse> apiResponseCall = RestClient.getService().getMyVotes(appPref.getUserInfo().getId());
        apiResponseCall.enqueue(new Callback<NewResponse>() {
            @Override
            public void onResponse(Call<NewResponse> call, Response<NewResponse> response) {
                votesAdapter = new VotesAdapter(getActivity(), response.body().getPaylpad().getData(), appPref , Flag);
                votes_recycler.setAdapter(votesAdapter);

                try {
                    pullToRefresh.setRefreshing(false);
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(Call<NewResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "ERROR = " + t.getMessage(), Toast.LENGTH_SHORT).show();

                Log.i("ERRORRR", t.getMessage());

            }
        });


    }

    @Override
    public void refresh() {

        pullToRefresh.setRefreshing(true);
        downloadApi();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                pullToRefresh.setRefreshing(true);
                downloadApi();
                break;
        }
    }
}
