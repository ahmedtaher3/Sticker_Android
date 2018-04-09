package com.sticker_android.view;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.sticker_android.utils.Utils;

import java.util.Calendar;
import java.util.Date;

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

    public SetDate(TextView view, Context ctx) {
        this.view = view;
        this.ctx = ctx;
        this.view.setOnClickListener(this);
        myCalendar = Calendar.getInstance();
        y = myCalendar.get(Calendar.YEAR);
        m = myCalendar.get(Calendar.MONTH);
        d = myCalendar.get(Calendar.DAY_OF_MONTH);
        pickerDialog = new DatePickerDialog(ctx, this, y, m, d);
        //  this.view.setText(Utils.formatDate(ctx, y, m, d));
    }

    @Override
    public void onClick(View v) {
        Utils.hideKeyboard((Activity) ctx);
        pickerDialog.getDatePicker().setMinDate(new Date().getTime() - 10000);
        pickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        y = year;
        m = monthOfYear;
        d = dayOfMonth;
        this.view.setText(Utils.dateModify(y+"-"+(m+1)+"-"+d));
    }

    public String getChosenDate() {
        return y + "-" + (m+1) + "-" + d;
    }

    public void setDate(String dateQualification) {
        //2017-05-08
        y = Integer.parseInt(dateQualification.split("-")[0]);
        m = Integer.parseInt(dateQualification.split("-")[1]) - 1;
        d = Integer.parseInt(dateQualification.split("-")[2]);
        pickerDialog.updateDate(y, m, d);
        this.view.setText(Utils.dateModify(y+"-"+(m+1)+"-"+d));
    }
    public void setMinDate(String minDate) {
        //2017-05-08
        y = Integer.parseInt(minDate.split("-")[0]);
        m = Integer.parseInt(minDate.split("-")[1]) - 1;
        d = Integer.parseInt(minDate.split("-")[2]);
        pickerDialog.getDatePicker().setMinDate(m/d/y);

    }
}
