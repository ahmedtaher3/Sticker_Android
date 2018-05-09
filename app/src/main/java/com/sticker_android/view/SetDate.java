package com.sticker_android.view;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.sticker_android.utils.Utils;

import java.util.Calendar;

/**
 * Created by ankit on 30/1/17
 */

public class SetDate implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private TextView view;
    private Context ctx;
    private Calendar myCalendar = Calendar.getInstance();
    private int y = myCalendar.get(Calendar.YEAR);
    private int m = myCalendar.get(Calendar.MONTH);
    private int d = myCalendar.get(Calendar.DAY_OF_MONTH);
    private DatePickerDialog pickerDialog;

    public SetDate(TextView view, Context ctx, int style) {
        this.view = view;
        this.ctx = ctx;
        this.view.setOnClickListener(this);
        myCalendar = Calendar.getInstance();
        y = myCalendar.get(Calendar.YEAR);
        m = myCalendar.get(Calendar.MONTH);
        d = myCalendar.get(Calendar.DAY_OF_MONTH);

        pickerDialog = new DatePickerDialog(ctx, style, this, y, m, d) {
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
                    // do something for phones running an SDK before lollipop
                    getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }
            }

        };
        //  this.view.setText(Utils.formatDate(ctx, y, m, d));
    }

    @Override
    public void onClick(View v) {
        Utils.hideKeyboard((Activity) ctx);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        pickerDialog.setTitle("");
        pickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        pickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, monthOfYear);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        cal.set(Calendar.YEAR, year);
        if (cal.before(myCalendar)) {
            y = myCalendar.get(Calendar.YEAR);
            m = myCalendar.get(Calendar.MONTH);
            d = myCalendar.get(Calendar.DAY_OF_MONTH);
            this.view.setText(Utils.dateModify(y + "-" + (m + 1) + "-" + d));

        }else {
            y = year;
            m = monthOfYear;
            d = dayOfMonth;
            this.view.setText(Utils.dateModify(y + "-" + (m + 1) + "-" + d));
        }
    }

    public String getChosenDate() {
        return y + "-" + (m + 1) + "-" + d;
    }

    public void setDate(String dateQualification) {
        //2017-05-08
        y = Integer.parseInt(dateQualification.split("-")[0]);
        m = Integer.parseInt(dateQualification.split("-")[1]) - 1;
        d = Integer.parseInt(dateQualification.split("-")[2]);
        pickerDialog.updateDate(y, m, d);
        this.view.setText(Utils.dateModify(y + "-" + (m + 1) + "-" + d));

    }

    public void setMinDate(String minDate) {
        //2017-05-08
        y = Integer.parseInt(minDate.split("-")[0]);
        m = Integer.parseInt(minDate.split("-")[1]) - 1;
        d = Integer.parseInt(minDate.split("-")[2]);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(y, m, d);
        //   pickerDialog.getDatePicker().setMinDate(Utils.convertStringToDate(minDate).getTime() - 1000);
        pickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            // do something for phones running an SDK before lollipop
            pickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }


    public static boolean validatePastDate(Context mContext, int day, int month, int year) {
        final Calendar c = Calendar.getInstance();
        int currentYear = c.get(Calendar.YEAR);
        int currentMonth = c.get(Calendar.MONTH) + 1;
        int currentDay = c.get(Calendar.DAY_OF_MONTH);
        if (day > currentDay && year == currentYear && month == currentMonth) {
            Toast.makeText(mContext, "Please select valid date", Toast.LENGTH_LONG).show();
            return false;
        } else if (month > currentMonth && year == currentYear) {
            Toast.makeText(mContext, "Please select valid month", Toast.LENGTH_LONG).show();
            return false;
        } else if (year > currentYear) {
            Toast.makeText(mContext, "Please select valid year", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
}
