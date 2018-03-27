package com.sticker_android.utils;

import android.app.Dialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.sticker_android.R;

/**
 * Created by user on 27/3/18.
 */

public  class ShareOneTouchAlertNewBottom extends BottomSheetDialogFragment {

    DialogListener listener;
  public   ShareOneTouchAlertNewBottom(DialogListener listener){

      this.listener=listener;
    }

    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.forgot_password, null);
        dialog.setContentView(contentView);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent())
                .getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

        final EditText edtEmail = (EditText) contentView.findViewById(R.id.forgot_password_edt_email);
        Button sendMail = (Button) contentView.findViewById(R.id.sendMail);
        sendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.listener(dialog,edtEmail);
            }
        });
    }

   public interface DialogListener{
        public void listener(Dialog  dialog,EditText editText);
    }


}