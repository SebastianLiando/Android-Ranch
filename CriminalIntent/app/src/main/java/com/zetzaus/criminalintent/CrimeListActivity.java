package com.zetzaus.criminalintent;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

/**
 * This activity displays the fragment <code>CrimeListFragment</code> on the screen.
 *
 * @see CrimeListFragment
 */
public class CrimeListActivity extends SingleFragmentActivity {

    private static final int REQUEST_PERMISSION_CONTACT = 1000;

    /**
     * Prompts user for permission.
     *
     * @param savedInstanceState the saved system state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Get permission from user
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    REQUEST_PERMISSION_CONTACT);
        }
        super.onCreate(savedInstanceState);
    }

    /**
     * Returns a <code>CrimeListFragment</code>.
     *
     * @return the fragment to display.
     */
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    /**
     * Informs user if the permission is not granted.
     *
     * @param requestCode  the request permission code.
     * @param permissions  the permissions prompted.
     * @param grantResults the result of the prompt.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CONTACT) {
            if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, R.string.toast_permission_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }
}