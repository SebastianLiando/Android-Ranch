package com.zetzaus.photogallery;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

/**
 * This class is used to send a notification about a new picture available.
 */
public class NotificationUtils {

    private static final String NOTIFICATION_CHANNEL_ID = "Picture Channel";

    /**
     * Sends a notification to the user.
     *
     * @param context the context.
     */
    public static void notifyNewPicture(Context context) {
        createNotificationChannel(context);

        Intent notifIntent = PhotoGalleryActivity.newIntent(context);
        PendingIntent notifPendingIntent = PendingIntent.getActivity(context, 1, notifIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setTicker(context.getString(R.string.notif_title_new))
                .setContentTitle(context.getString(R.string.notif_title_new))
                .setContentText(context.getString(R.string.notif_content_new))
                .setContentIntent(notifPendingIntent)
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .build();

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
        managerCompat.notify(0, notification);
    }

    /**
     * Creates a notification channel for android O and above.
     *
     * @param context the context.
     */
    private static void createNotificationChannel(Context context) {
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Pictures Notification", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(context.getString(R.string.channel_desc));
            channel.enableLights(true);
            manager.createNotificationChannel(channel);
        }
    }
}
