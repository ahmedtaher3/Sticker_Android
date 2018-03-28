package com.sticker_android.network;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.sticker_android.R;
import com.sticker_android.utils.Utils;


public class ErrorUtils {

    /***
     * custom alert dialog
     * @param mContext context
     * @param title notificationTitle of dialog
     * @param msg msg of dialog
     */
    public static void showMessageDialog(Context mContext, String title, String msg) {

        Utils.showToast(mContext,msg);
     /*   AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.layout_dialog, null);
        TextView txtTitle = (TextView) dialogView.findViewById(R.id.dialogTitle);
        TextView txtMsg = (TextView) dialogView.findViewById(R.id.dialogMsg);
        if (title == null) {
            txtTitle.setVisibility(View.GONE);
        }
        txtTitle.setText(title);
        txtMsg.setText(msg);
        Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);
        Button btnCancel = (Button) dialogView.findViewById(R.id.btnCancel);

        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        Activity activity = (Activity) mContext;
        if (!activity.isFinishing())
            alertDialog.show();*/
    }

    /***
     * @param context Instance of context
     * @param msg Toast Message
     */
    public static void showToast(Context context, String msg) {
      //  Utils.createLongToast(context, msg);
    }

}
