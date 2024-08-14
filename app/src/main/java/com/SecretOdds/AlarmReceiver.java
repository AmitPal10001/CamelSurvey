package com.SecretOdds;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.camelsurvey.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AlarmReceiver extends BroadcastReceiver {
    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent notificationIntent = new Intent(context, MainActivity.class);//on tap this activity will open
        notificationIntent.putExtra("JOBID", "alarm");
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);//getting the pendingIntent

        Notification.Builder builder = new Notification.Builder(context);//building the notification

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE HH", Locale.US);
        String datestr = sdf.format(new Date());
        Notification notification = builder.setContentTitle(context.getString(R.string.new_survey_available))
                .setContentText(context.getString(R.string.complete_this_survey_and_boost_your_earnings))
                .setTicker(context.getString(R.string.new_survey_available))
                .setSmallIcon(R.drawable.appicon)
                .setContentIntent(pendingIntent).build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(NOTIFICATION_ID);
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//below creating notification channel, because of androids latest update, O is Oreo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_ID,
                    "NotificationDemo",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }
      //  Notification notification1 = intent.getParcelableExtra(NOTIFICATION);
      //  int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        if(datestr.equalsIgnoreCase("Monday 13") || datestr.equalsIgnoreCase("Thursday 18"))
        notificationManager.notify(0, notification);
    }
}
