package com.sticker_android.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.sticker_android.R;
import com.sticker_android.model.interfaces.ImagePickerListener;
import com.sticker_android.utils.helper.PermissionManager;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

import static com.sticker_android.utils.helper.PermissionManager.Constant.READ_STORAGE_ACCESS_RQ;
import static com.sticker_android.utils.helper.PermissionManager.Constant.WRITE_STORAGE_ACCESS_RQ;

/**
 * Created by user on 23/3/18.
 */

public class Utils {
    /**
     * Method is used to hide the keyboard
     * @param activity
     */
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    //to get the device id of current device
    public static String getDeviceId(Context context) {
        String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        // LogUtils.error("Unique generated id  : " + android_id);
        return android_id;    }

    /**
     * This method prompt the dialog in order to provide option for selecting image
     */
    public static void showAlertDialogToGetPic(final Activity activity, final ImagePickerListener pickerListener) {

        final String[] items = new String[]{activity.getString(R.string.pick_gallery), activity.getString(R.string.take_photo)};
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(true);
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (items[which].equals(activity.getString(R.string.take_photo))) {
                    if (PermissionManager.checkWriteStoragePermission(activity, WRITE_STORAGE_ACCESS_RQ)) {
                        pickerListener.captureFromCamera();
                    }
                } else if (items[which].equals(activity.getString(R.string.pick_gallery))) {
                    if (PermissionManager.checkReadStoragePermission(activity, READ_STORAGE_ACCESS_RQ)) {
                        pickerListener.pickFromGallery();
                    }
                }
            }
        });
        AlertDialog alertDialog = builder.show();
        alertDialog.setCanceledOnTouchOutside(true);
    }

    /**
     * This will return  the path of custom folder
     */
    public static File getCustomImagePath(Context context, String fileName) {

        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {

            String filePath = Environment.getExternalStorageDirectory().getPath();

            File myDir = new File(filePath, "buddsup");
            myDir.mkdirs();

            String fname = null;

            if (fileName != null)
                fname = fileName + ".png";
            else
                fname = Calendar.getInstance().getTimeInMillis() + ".png";

            File file = new File(myDir, fname);

            if (file.exists()) file.delete();

            //return (file.getAbsolutePath());
            return file;
        } else {
            Toast.makeText(context, "Sd Card is not mounted", Toast.LENGTH_LONG).show();
        }
        return null;
    }

    public static String getGalleryImagePath(Context context, Uri uri) {

        String imagePath = null;

        try {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (cursor.moveToFirst()) {
                imagePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
            }

            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return imagePath;
    }

    /**
     * to hide the Keyboard
     *
     * @return
     */
    public static void hideSoftKeyboard(Activity activity, View view) {

        try{
            if (view == null) {
                view = activity.getCurrentFocus();
            }

            InputMethodManager in = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }


    /**
     * show message In Toast.
     * @param context
     * @param string
     */
    public static void showToast(Context context, String string) {
            Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
    }


    public  static void changeLanguage(String lang,Activity activity,Class<?>cls){
        Locale myLocale = new Locale(lang);
        Resources res = activity.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent intent = new Intent(activity, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.finish();
    }

    /**
     * will show the alert for the phone call
     *
     * @param context
     * @param phoneNo
     */
    public static void showPhoneCallPopup(final Context context, final String phoneNo) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(phoneNo);
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.txt_call, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNo));
                if (ActivityCompat.checkSelfPermission(((Activity) context), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                context.startActivity(intent);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

}
