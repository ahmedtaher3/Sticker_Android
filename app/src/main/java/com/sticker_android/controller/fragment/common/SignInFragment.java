package com.sticker_android.controller.fragment.common;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sticker_android.R;
import com.sticker_android.controller.fragment.base.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends BaseFragment {

    public  SignUpCallback mSignUpCallback;
    private TextView tvSignUp;

    public SignInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_sign_in, container, false);
       setViewReferences(view);
        view.findViewById(R.id.imvLogo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSignUpCallback.isClicked();
            }
        });
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SignInFragment.SignUpCallback) {
            mSignUpCallback = (SignInFragment.SignUpCallback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    protected void setViewListeners() {
      tvSignUp.setOnClickListener(new SignUpListener());
    }

    @Override
    protected void setViewReferences(View view) {
      //  tvSignUp=view.findViewById(R.id.tvSignUp);
    }

    @Override
    protected boolean isValidData() {
        return false;
    }

    public interface SignUpCallback{
        public void isClicked();
    }

    class SignUpListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            mSignUpCallback.isClicked();
        }
    }

}
