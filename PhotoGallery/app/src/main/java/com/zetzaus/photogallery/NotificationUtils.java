package com.zetzaus.photogallery;

import android.app.Activity;
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
    public static final String EXTRA_NOTIFICATION = "NOTIFICATION";
    public static final String EXTRA_REQUEST_CODE = "REQ_CODE";

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

        showBackgroundNotification(context, 0, notification);
    }

    /**
     * Sends a broadcast message that determine whether the notification is displayed to the user or not.
     *
     * @param context      the context.
     * @param requestCode  the request code.
     * @param notification the notification to show.
     */
    private static void showBackgroundNotification(Context context, int requestCode, Notification notification) {
        Intent intent = new Intent(PollService.ACTION_SHOW_NOTIFICATION);
        intent.putExtra(EXTRA_REQUEST_CODE, requestCode);
        intent.putExtra(EXTRA_NOTIFICATION, notification);
        context.sendOrderedBroadcast(intent, PollService.NOTIFICATION_PERMISSION,
                null, null, Activity.RESULT_OK, null, null);
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
