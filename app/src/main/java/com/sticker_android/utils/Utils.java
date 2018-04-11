package com.sticker_android.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.sticker_android.R;
import com.sticker_android.model.interfaces.ImagePickerListener;
import com.sticker_android.utils.helper.PermissionManager;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TimeZone;
import java.util.TreeMap;

import static com.sticker_android.utils.helper.PermissionManager.Constant.READ_STORAGE_ACCESS_RQ;
import static com.sticker_android.utils.helper.PermissionManager.Constant.WRITE_STORAGE_ACCESS_RQ;

/**
 * Created by user on 23/3/18.
 */

public class Utils {
    /**
     * Method is used to hide the keyboard
     *
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
        return android_id;
    }

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

        try {
            if (view == null) {
                view = activity.getCurrentFocus();
            }

            InputMethodManager in = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * show message In Toast.
     *
     * @param context
     * @param string
     */
    public static void showToast(Context context, String string) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
    }


    public static void changeLanguage(String lang, Activity activity, Class<?> cls) {
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


    /**
     * This method prompt the dialog in order to provide option for selecting image
     */
    public static void showAlertDialogToGetPicFromFragment(final Activity activity, final ImagePickerListener pickerListener, final Fragment fragment) {

        final String[] items = new String[]{activity.getString(R.string.pick_gallery), activity.getString(R.string.take_photo)};
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(true);
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (items[which].equals(activity.getString(R.string.take_photo))) {
                    if (PermissionManager.checkWriteStoragePermissionInFragment(activity, fragment, WRITE_STORAGE_ACCESS_RQ)) {
                        pickerListener.captureFromCamera();
                    }
                } else if (items[which].equals(activity.getString(R.string.pick_gallery))) {
                    if (PermissionManager.checkReadPhoneStatePermissionInFragment(activity, fragment, READ_STORAGE_ACCESS_RQ)) {
                        pickerListener.pickFromGallery();
                    }
                }
            }
        });
        AlertDialog alertDialog = builder.show();
        alertDialog.setCanceledOnTouchOutside(true);
    }

    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();

    static {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "G");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }

    public static String format(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return format(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + format(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }


    public static String dateModify(String jobModifiedDate) {
        String dater = "";
        try {
            java.text.DateFormat formatter;
            Date date;
            formatter = new SimpleDateFormat("yyyy-MM-dd");
            date = (Date) formatter.parse(jobModifiedDate);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM,yyyy");
            dater = simpleDateFormat.format(date);

        } catch (Exception e) {
            //  e.printStackTrace();
        }

        return dater;
    }


    public static String capitlizeText(String name) {

        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static String convertToCurrentTimeZone(String Date) {
        String converted_date = "";
        try {

            DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date date = utcFormat.parse(Date);

            DateFormat currentTFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            currentTFormat.setTimeZone(TimeZone.getTimeZone(getCurrentTimeZone()));

            converted_date = currentTFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return converted_date;
    }


    //get the current time zone

    private static String getCurrentTimeZone() {
        TimeZone tz = Calendar.getInstance().getTimeZone();
        System.out.println(tz.getDisplayName());
        return tz.getID();
    }


    public static void deleteDialog(String message, Activity activity, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.AppThemeAddRenew);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.setPositiveButton(
                "Yes", listener);

        builder.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(alertDialog.BUTTON_NEGATIVE).setTextColor(activity.getResources().getColor(R.color.colorCorporateText));
        alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextColor(activity.getResources().getColor(R.color.colorCorporateText));
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP){
            // do something for phones running an SDK before lollipop
          //  alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }


    }


    /**
     * Method is used to convert String to date object
     *
     * @param dateSting
     * @return
     */
    public static Date convertStringToDate(String dateSting) {

        java.text.DateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Date date = null;
        try {
            date = format.parse(dateSting);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }


    public static Date changeDays(String date) {
        Date dtStartDate = convertStringToDate(date);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Calendar c = Calendar.getInstance();
        c.setTime(dtStartDate);
        c.add(Calendar.DATE, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
// number of days to add
        String dt = sdf.format(c.getTime());  // dt is now the new date
        return convertStringToDate(dt);
    }
}
