package com.sticker_android.utils;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

/**
 * Created by root on 20/2/18.
 */

public class CommonSnackBar {

    public static void show(View view, String text, int duration) {
        final Snackbar snackbar = Snackbar.make(view, text, duration);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android

                .support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(12);
      /*  snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        */
        snackbar.show();
    }
}
