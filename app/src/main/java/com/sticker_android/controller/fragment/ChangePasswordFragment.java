package com.sticker_android.controller.fragment;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.sticker_android.R;
import com.sticker_android.controller.fragment.base.BaseFragment;
import com.sticker_android.model.UserData;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.CommonSnackBar;
import com.sticker_android.utils.ProgressDialogHandler;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.sharedpref.AppPref;

import retrofit2.Call;

public class ChangePasswordFragment extends BaseFragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    EditText oldPassword,newPassword,confirmPassword;
    private Button buttonSubmit;
    private AppPref appPref;
    private UserData userData;
    private UserData mUserData;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    public static ChangePasswordFragment newInstance(String param1, String param2) {
        ChangePasswordFragment fragment = new ChangePasswordFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=   inflater.inflate(R.layout.fragment_change_password, container, false);
        init();
        setViewReferences(view);
        setViewListeners();
        setBackground();
        oldPassword.setText("");
        confirmPassword.setText("");
        newPassword.setText("");

        return view;
    }

    private void init() {
        appPref=new AppPref(getActivity());
        mUserData=appPref.getUserInfo();
    }


    @Override
    protected void setViewListeners() {
        buttonSubmit.setOnClickListener(this);
    }

    @Override
    protected void setViewReferences(View view) {

        oldPassword= (EditText) view.findViewById(R.id.change_password_edt_old_pass);
        newPassword= (EditText) view.findViewById(R.id.change_password_edt_new_pass);
        confirmPassword= (EditText) view.findViewById(R.id.change_password_edt_confirm_pass);
        buttonSubmit= (Button) view.findViewById(R.id.change_password_submit);
    }


    private void setBackground() {
        switch (mUserData.getUserType()){
            case "fan":
                buttonSubmit.setBackground(getResources().getDrawable(R.drawable.fan_btn_background));
                break;
            case "designer":
                buttonSubmit.setBackground(getResources().getDrawable(R.drawable.designer_btn_background));
                break;
            case "corporate":
                buttonSubmit.setBackground(getResources().getDrawable(R.drawable.corporate_btn_background));

                break;
        }
    }

    @Override
    protected boolean isValidData() {
        String oldPass=oldPassword.getText().toString();
        String newPass=newPassword.getText().toString();
        String confirmPass=confirmPassword.getText().toString();
        if(oldPass.isEmpty()){
            oldPassword.requestFocus();
            Utils.showToast(getActivity(),"Please enter old password.");

            //  CommonSnackBar.show(oldPassword,"Old password cannot be empty", Snackbar.LENGTH_SHORT);
            return false;
        }/*else if(oldPass.length()<8){
            oldPassword.requestFocus();
            Utils.showToast(getActivity(),getActivity().getString(R.string.old_password_cannot_be_less));
            //  CommonSnackBar.show(oldPassword,getActivity().getString(R.string.password_cannot_be_less), Snackbar.LENGTH_SHORT);
            return false;

        }*/else if(newPass.isEmpty()){
            newPassword.requestFocus();
            Utils.showToast(getActivity(),getActivity().getString(R.string.please_enter_new_password));
            //   CommonSnackBar.show(oldPassword,"New password cannot be empty", Snackbar.LENGTH_SHORT);
            return false;
        }else if(newPass.length()<8){
            newPassword.requestFocus();
            Utils.showToast(getActivity(),getActivity().getString(R.string.new_password_cannot_be_less));

            //  CommonSnackBar.show(oldPassword,getActivity().getString(R.string.password_cannot_be_less), Snackbar.LENGTH_SHORT);
            return false;

        }else if(confirmPass.isEmpty()){
            confirmPassword.requestFocus();
            Utils.showToast(getActivity(),"Please enter confirm password.");

            // CommonSnackBar.show(oldPassword,"Confiem password cannot be empty", Snackbar.LENGTH_SHORT);
            return false;
        }/*else if(confirmPass.length()<8){
            confirmPassword.requestFocus();
            Utils.showToast(getActivity(),getActivity().getString(R.string.confirm_password_cannot_be_less));

            //  CommonSnackBar.show(oldPassword,getActivity().getString(R.string.password_cannot_be_less), Snackbar.LENGTH_SHORT);
            return false;

        }*/else if(!newPass.equals(confirmPass)){
            Utils.showToast(getActivity(),getActivity().getString(R.string.old_password_not_match));
            return false;
        }else if(!mUserData.getPasssword().equals(oldPass)){
            Utils.showToast(getActivity(),"Old Password Not match");
            return false;
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        Utils.hideKeyboard(getActivity());
        switch (v.getId()){
            case R.id.change_password_submit:
                if(isValidData())
                    changePasswordApi();
                break;

        }
    }

    private void changePasswordApi() {
        final ProgressDialogHandler progressDialogHandler=new ProgressDialogHandler(getActivity());
        progressDialogHandler.show();
        Log.d("jhcjdsc",mUserData.getId());
        Call<ApiResponse> apiResponseCall= RestClient.getService().changePassword(mUserData.getId(),confirmPassword.getText().toString(),"");
        apiResponseCall.enqueue(new ApiCall(getActivity()) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                progressDialogHandler.hide();
                if(apiResponse.status)
                    if(apiResponse.status) {
                        CommonSnackBar.show(oldPassword,"Password updated successfully", Snackbar.LENGTH_SHORT);
                        appPref.saveUserObject(null);
                       UserData userData=new UserData();
                        userData=mUserData;
                        userData.setPasssword(newPassword.getText().toString());
                        appPref.saveUserObject(userData);
                        oldPassword.setText("");
                        confirmPassword.setText("");
                        newPassword.setText("");
                    }else {
                        Utils.showToast(getActivity(),apiResponse.error.message);
                        //CommonSnackBar.show(oldPassword, apiResponse.error.message, Snackbar.LENGTH_SHORT);
                    }}
            @Override
            public void onFail(Call<ApiResponse> call, Throwable t) {
                progressDialogHandler.hide();
            }
        });
    }

    public void clearField(){

        if(oldPassword!=null&&confirmPassword!=null&&newPassword!=null) {

            oldPassword.setText("");
            confirmPassword.setText("");
            newPassword.setText("");
            Utils.hideKeyboard(getActivity());
        }
    }

}

