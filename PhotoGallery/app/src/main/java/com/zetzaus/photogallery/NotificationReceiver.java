package com.zetzaus.photogallery;

import android.app.Activity;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationManagerCompat;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String TAG = NotificationReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (getResultCode() != Activity.RESULT_OK) return;

        int requestCode = intent.getIntExtra(NotificationUtils.EXTRA_REQUEST_CODE, 0);
        Notification notification = intent.getParcelableExtra(NotificationUtils.EXTRA_NOTIFICATION);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
        managerCompat.notify(requestCode, notification);

    }
}
