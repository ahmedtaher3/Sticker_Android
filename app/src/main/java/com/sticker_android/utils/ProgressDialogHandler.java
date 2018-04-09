package com.sticker_android.utils;

import android.app.ProgressDialog;
import android.content.Context;

import com.sticker_android.R;


/**
 * Created by user on 17/8/17.
 */

public class ProgressDialogHandler {

    private Context mContext;
    private ProgressDialog mProgressDialog = null;
//    private Dialog dialog;

    public ProgressDialogHandler(Context context) {
        mContext   = context;
        this.initProgressBar();
    }

    public void show() {
        if(mProgressDialog != null && !mProgressDialog.isShowing()){
            mProgressDialog.show();
//            dialog.show();
        }
    }

    public void hide() {
        if(mProgressDialog != null && mProgressDialog.isShowing()){
            mProgressDialog.hide();
//            dialog.hide();
        }
        assert mProgressDialog != null;
        mProgressDialog.dismiss();
//        dialog.dismiss();
    }


    private void initProgressBar(){
        mProgressDialog = new ProgressDialog(mContext, R.style.MyTheme);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        show();
        /*dialog = new Dialog(mContext);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.progress_bar);
        dialog.getWindow().addFlags(0);
        dialog.setCancelable(false);
        dialog.show();*/
    }

    public ProgressDialog getmProgressDialog() {
        return mProgressDialog;
    }

//    public Dialog getDialog() {
//        return dialog;
//    }
}