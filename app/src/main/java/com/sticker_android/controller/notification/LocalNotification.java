package com.sticker_android.controller.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.sticker_android.R;
import com.sticker_android.utils.sharedpref.AppPref;

public class LocalNotification {

    private AppPref mAppPref;
    private String message;

   public LocalNotification (){

    }

    public void setNotification(Context context,String notificationTitle,String textContent){
        mAppPref=new AppPref(context);

        NotificationCompat.Builder notificationCompat=new NotificationCompat.Builder(context);
        notificationCompat.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(notificationTitle)
                .setContentText(textContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND);

        int messageCount=mAppPref.getNewMessagesCount(0)+1;
        mAppPref.saveNewMessagesCount(messageCount);
        if (messageCount > 1) {
            message = "You have received " + messageCount + " messages.";
            notificationCompat.setContentText(message);
        }

        NotificationManager notificationManager = (NotificationManager) context.
                getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent =new Intent();
        intent.putExtra("data here", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0, intent,
                PendingIntent.FLAG_ONE_SHOT);
        notificationCompat.setContentIntent(pendingIntent);
        notificationManager.notify(0,
                notificationCompat.build());
    }

}
