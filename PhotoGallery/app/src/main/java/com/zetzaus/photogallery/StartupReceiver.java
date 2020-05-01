package com.zetzaus.photogallery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class StartupReceiver extends BroadcastReceiver {

    private static final String TAG = StartupReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Received broadcast intent: " + intent.getAction());
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            PollService.setServiceAlarm(context, QueryPreferences.isAlarmOn(context));
        }
    }
}
