package com.zetzaus.photogallery;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import androidx.fragment.app.Fragment;

/**
 * This class is to be inherited by <code>Fragment</code> that will cancel notifications when the fragment is still alive.
 */
public class VisibleFragment extends Fragment {
    private static final String TAG = VisibleFragment.class.getSimpleName();

    private BroadcastReceiver mOnShowNotification = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Fragment is alive, cancel notification!
            Log.i(TAG, "Cancel notification");
            setResultCode(Activity.RESULT_CANCELED);
        }
    };

    /**
     * Registers the dynamic receiver.
     */
    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(mOnShowNotification, new IntentFilter(PollService.ACTION_SHOW_NOTIFICATION),
                PollService.NOTIFICATION_PERMISSION, null);
    }

    /**
     * unregisters the dynamic receiver
     */
    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(mOnShowNotification);
    }
}
