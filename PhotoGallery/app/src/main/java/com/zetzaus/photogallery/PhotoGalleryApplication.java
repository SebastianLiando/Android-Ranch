package com.zetzaus.photogallery;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationManagerCompat;

public class PhotoGalleryApplication extends Application {

    public static final String NOTIFICATION_CHANNEL_ID = "Picture Channel";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel(getApplicationContext());
    }

    /**
     * Creates a notification channel for android O and above.
     *
     * @param context the context.
     */
    private static void createNotificationChannel(Context context) {
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.channel_title),
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(context.getString(R.string.channel_desc));
            channel.enableLights(true);
            manager.createNotificationChannel(channel);
        }
    }
}
