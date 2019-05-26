package com.sticker_android.controller.fragment.fan.fanhome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sticker_android.R;
import com.sticker_android.controller.activities.fan.home.AddNewVote;
import com.sticker_android.controller.adaptors.VotesAdapter;
import com.sticker_android.controller.fragment.base.BaseFragment;
import com.sticker_android.model.NewResponse;
import com.sticker_android.model.User;
import com.sticker_android.model.corporateproduct.Product;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.sharedpref.AppPref;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VotesFragment extends BaseFragment {

    private User mLoggedUser;
    private AppPref appPref;
    FloatingActionButton floatingActionButton;
    RecyclerView votes_recycler;
    VotesAdapter votesAdapter;

    RecyclerView.LayoutManager layoutManager;

    public VotesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_votes, container, false);
        init();
        setViewReferences(view);
        downloadApi();
        return view;

    }

    private void init() {
        mLoggedUser = new AppPref(getActivity()).getUserInfo();
        appPref = new AppPref(getActivity());

    }

    @Override
    protected void setViewListeners() {

    }

    @Override
    protected void setViewReferences(View view) {

        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fabAddNew);
        votes_recycler = (RecyclerView) view.findViewById(R.id.votes_recycler);
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
    }

    @Override
    protected boolean isValidData() {
        return false;
    }


    private void downloadApi() {

        if (appPref.getLoginFlag(false))
        {
            Call<NewResponse> apiResponseCall = RestClient.getService().getvotes(appPref.getUserInfo().getId());
            apiResponseCall.enqueue(new Callback<NewResponse>() {
                @Override
                public void onResponse(Call<NewResponse> call, Response<NewResponse> response) {
                    votesAdapter = new VotesAdapter(getActivity(), response.body().getPaylpad().getData(),appPref);
                    votes_recycler.setAdapter(votesAdapter);


                }

                @Override
                public void onFailure(Call<NewResponse> call, Throwable t) {
                    Toast.makeText(getActivity(), "ERROR = " + t.getMessage(), Toast.LENGTH_SHORT).show();

                    Log.i("ERRORRR", t.getMessage());

                }
            });

        }
        else {
            Call<NewResponse> apiResponseCall = RestClient.getService().getvotes("");
            apiResponseCall.enqueue(new Callback<NewResponse>() {
                @Override
                public void onResponse(Call<NewResponse> call, Response<NewResponse> response) {
                    votesAdapter = new VotesAdapter(getActivity(), response.body().getPaylpad().getData(),appPref);
                    votes_recycler.setAdapter(votesAdapter);


                }

                @Override
                public void onFailure(Call<NewResponse> call, Throwable t) {
                    Toast.makeText(getActivity(), "ERROR = " + t.getMessage(), Toast.LENGTH_SHORT).show();

                    Log.i("ERRORRR", t.getMessage());

                }
            });


        }




    }
}
