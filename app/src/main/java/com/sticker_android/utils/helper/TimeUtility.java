package com.sticker_android.utils.helper;

import android.content.Context;
import android.content.res.Resources;

import com.sticker_android.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by user on 4/4/18.
 */

public class TimeUtility {


    public String covertTimeToText(String dataDate,Context context) {

        String convTime = null;
        long dateDiff = 0;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
           // SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            Date pasTime = dateFormat.parse(dataDate);

            Date nowTime = new Date();

            dateDiff = nowTime.getTime() - pasTime.getTime();


        } catch (ParseException e) {
            e.printStackTrace();

        }
        //return convTime;
        Resources r = context.getResources();

        String prefix = r.getString(R.string.time_ago_prefix);
        String suffix = r.getString(R.string.time_ago_suffix);

        double seconds = Math.abs(dateDiff) / 1000;
        double minutes = seconds / 60;
        double hours = minutes / 60;
        double days = hours / 24;
        double years = days / 365;

        String words;

        if (seconds < 45) {
            //  words = r.getString(R.string.time_ago_seconds)+""+ Math.round(seconds);
            words =  ""+Math.round(seconds)+" second ";

        } else if (seconds < 90) {
            words = r.getString(R.string.time_ago_minute)+" ";
        } else if (minutes < 45) {
            words = r.getString(R.string.time_ago_minutes, Math.round(minutes));
        } else if (minutes < 90) {
            words = r.getString(R.string.time_ago_hour)+" "+1;
        } else if (hours < 24) {
            words = r.getString(R.string.time_ago_hours, Math.round(hours));
        } else if (hours < 42) {
            words = r.getString(R.string.time_ago_day)+" "+1;
        } else if (days < 30) {
            words = r.getString(R.string.time_ago_days, Math.round(days));
        } else if (days < 45) {
            words = r.getString(R.string.time_ago_month)+" "+1;
        } else if (days < 365) {
            words = r.getString(R.string.time_ago_months, Math.round(days / 30));
        } else if (years < 1.5) {
            words = r.getString(R.string.time_ago_year)+" "+1;
        } else {
            words = r.getString(R.string.time_ago_years, Math.round(years));
        }

        StringBuilder sb = new StringBuilder();

        if (prefix != null && prefix.length() > 0) {
            sb.append(prefix).append(" ");
        }

        sb.append(words);

        if (suffix != null && suffix.length() > 0) {
            sb.append(" ").append(suffix);
        }

        return sb.toString().trim();
    }


}
