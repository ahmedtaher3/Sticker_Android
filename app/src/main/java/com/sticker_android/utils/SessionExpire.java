package com.sticker_android.utils;


import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;


/**
 * Created by user on 4/5/18.
 */

public class SessionExpire {

    public static AlertDialog showDialog(Context ctx, String title, String msg, OnClickListener listener) {
        Builder builder = new Builder(ctx);
        builder.setMessage(msg).setCancelable(false).setPositiveButton(android.R.string.ok, listener);
        builder.setTitle(title);
        AlertDialog alert = builder.create();
        alert.show();
        return alert;
    }
}
