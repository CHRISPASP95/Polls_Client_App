package com.example.christospaspalieris.polls_client_app;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.support.v7.app.NotificationCompat;

/**
 * Created by Christos Paspalieris on 21-Oct-17.
 */

class MyNotificationManager {

    private Context ctx;


    public static final int NOTIFICATION_ID = 234;
    public MyNotificationManager(Context ctx){
        this.ctx = ctx;
    }



    public void showNotification(String title, String notification, Intent intent){

        PendingIntent pendingIntent = PendingIntent.getActivity(
            ctx,
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        );


        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx);

        Notification mNotification = builder.setSmallIcon(R.mipmap.info_logo)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(notification)
                .setLargeIcon(BitmapFactory.decodeResource(ctx.getResources(),R.mipmap.info_logo))
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .setLights(Color.RED, 3000, 3000)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .build();

        mNotification.flags = Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationmanager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationmanager.notify(NOTIFICATION_ID,mNotification);
    }
}
