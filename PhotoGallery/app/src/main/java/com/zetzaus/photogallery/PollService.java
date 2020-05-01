package com.zetzaus.photogallery;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.util.Log;

import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;

/**
 * This class is a background service that checks for new images in Flickr. The class uses the AlarmManager, to be used
 * below Android L.
 */
public class PollService extends IntentService {

    private static final String TAG = PollService.class.getSimpleName();

    private static final long POLL_INTERVAL_MS = TimeUnit.MINUTES.toMillis(15);

    /**
     * Returns an <code>Intent</code> to the service.
     *
     * @param context the calling <code>Context</code>.
     * @return an <code>Intent</code> to the service.
     */
    public static Intent newIntent(Context context) {
        return new Intent(context, PollService.class);
    }

    /**
     * Creates a <code>PollService</code>.
     */
    public PollService() {
        super(TAG);
    }

    /**
     * Fetches images list from the Flickr website.
     *
     * @param intent not used.
     */
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (!isNetworkConnected()) {
            return;
        }

        String query = QueryPreferences.getStoredQuery(this);
        String lastResultId = QueryPreferences.getLastResultId(this);
        List<GalleryItem> galleryItems;

        if (query == null) {
            galleryItems = new FlickrFetchr().fetchRecentPhotos(1);
        } else {
            galleryItems = new FlickrFetchr().searchPhotos(query, 1);
        }

        if (galleryItems.size() == 0) return;

        String resultId = galleryItems.get(0).getId();
        if (resultId.equals(lastResultId)) {
            Log.i(TAG, "Fetched an old one");
        } else {
            Log.i(TAG, "Fetched a new one");
            NotificationUtils.notifyNewPicture(this);
        }

        QueryPreferences.setLastResultId(this, resultId);
    }

    /**
     * Returns <code>true</code> if the device is connected to the internet.
     *
     * @return <code>true</code> if the device is connected to the internet.
     */
    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        if (connectivityManager.getActiveNetworkInfo() == null) {
            return false;
        }

        return connectivityManager.getActiveNetworkInfo().isConnected();
    }

    /**
     * Turns on or off the alarm.
     *
     * @param context the context used.
     * @param isOn    true if the alarm is to be set.
     */
    public static void setServiceAlarm(Context context, boolean isOn) {
        Intent intent = PollService.newIntent(context);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (isOn) {
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),
                    POLL_INTERVAL_MS, pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

    /**
     * <code>True</code> if the alarm is on.
     *
     * @param context the context.
     * @return <code>True</code> if the alarm is on.
     */
    public static boolean isAlarmOn(Context context) {
        Intent intent = PollService.newIntent(context);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_NO_CREATE);
        return pendingIntent != null;
    }
}
