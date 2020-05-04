package com.zetzaus.locatr;

import android.app.Dialog;
import android.content.DialogInterface;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import androidx.fragment.app.Fragment;

public class LocatrActivity extends SingleFragmentActivity {

    private static final int REQUEST_ERROR = 0;

    @Override
    protected Fragment createFragment() {
        return LocatrFragment.newInstance();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Check availability
        GoogleApiAvailability availability = GoogleApiAvailability.getInstance();
        int result = availability.isGooglePlayServicesAvailable(this);

        if (result != ConnectionResult.SUCCESS) {
            Log.i("LocatrActivity", "Google Play Service API is not available");
            Dialog errorDialog = availability.getErrorDialog(this, result, 0,
                    new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            finish();
                        }
                    });

            errorDialog.show();
        }else{
            Log.i("LocatrActivity", "Google Play Service API is available");
        }
    }
}
