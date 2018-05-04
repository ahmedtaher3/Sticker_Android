package com.sticker_android.controller.notification;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.sticker_android.R;
import com.sticker_android.application.StickerApp;
import com.sticker_android.controller.activities.base.AppBaseActivity;
import com.sticker_android.controller.activities.common.exit.ExitActivity;
import com.sticker_android.controller.activities.common.splash.SplashActivity;
import com.sticker_android.utils.sharedpref.AppPref;
import com.sticker_android.view.BadgeUtils;

import java.util.List;

public class LocalNotification {

    private AppPref mAppPref;
    private String message;


    public LocalNotification() {

    }

    public void setNotification(Context context, String notificationTitle, String textContent) {
        mAppPref = new AppPref(context);

        NotificationCompat.Builder notificationCompat = new NotificationCompat.Builder(context);
        notificationCompat.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(notificationTitle)
                .setContentText(textContent).setLights(Color.RED, 1000, 1000)
                .setVibrate(new long[]{0, 400, 250, 400})
                .setSound(RingtoneManager.getDefaultUri(
                        RingtoneManager.TYPE_NOTIFICATION))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND);

        int messageCount = mAppPref.getNewMessagesCount(0) + 1;
        mAppPref.saveNewMessagesCount(messageCount);
        if (messageCount > 1) {
            message = "You have received " + messageCount + " messages.";
            notificationCompat.setContentText(message);
        }

        BadgeUtils.setBadge(context, messageCount);
        NotificationManager notificationManager = (NotificationManager) context.
                getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, SplashActivity.class);
        intent.putExtra("data here", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (LocalNotification.isAppIsInBackground(context)) {
            ExitActivity.exitApplication(context);
            PendingIntent pendingIntent = PendingIntent.getActivity(context,
                    0, intent,
                    PendingIntent.FLAG_ONE_SHOT);
            notificationCompat.setContentIntent(pendingIntent);
            notificationManager.notify(0,
                    notificationCompat.build());
        } else {
            if (StickerApp.getInstance().getCurrentActivity() instanceof AppBaseActivity) {
                StickerApp.getInstance().getCurrentActivity().updateCallbackMessage();
            }

        }


    }

    // Clears notification tray messages
    public static void clearNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    /**
     * Method checks if the app is in background or not
     */
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }


}
