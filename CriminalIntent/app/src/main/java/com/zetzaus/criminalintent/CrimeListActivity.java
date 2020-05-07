package com.zetzaus.criminalintent;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

/**
 * This activity displays the fragment <code>CrimeListFragment</code> on the screen.
 *
 * @see CrimeListFragment
 */
public class CrimeListActivity extends SingleFragmentActivity implements CrimeListFragment.Callback, CrimeFragment.Callback {

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

    /**
     * Returns the layout id of the alias layout.
     *
     * @return the layout id of the alias layout.
     */
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    /**
     * Handles when a crime in the <code>RecyclerView</code> is selected.
     *
     * @param crime the selected crime.
     */
    @Override
    public void onCrimeSelected(Crime crime) {
        if (getResources().getBoolean(R.bool.isTablet)) {
            Fragment fragment = CrimeFragment.newInstance(crime.getId());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_container_detail, fragment)
                    .commit();
        } else {
            Intent intent = CrimePagerActivity.newIntent(this, crime.getId());
            startActivity(intent);
        }
    }

    /**
     * Close the details fragment if the swiped item is the current crime.
     *
     * @param id the id of the crime.
     * @deprecated
     */
    @Override
    public void onSwipeRemove(UUID id) {
        CrimeFragment fragment = (CrimeFragment) getSupportFragmentManager().findFragmentById(R.id.frame_container_detail);
        if (fragment.getCrime().getId().equals(id)) {
            getSupportFragmentManager().beginTransaction()
                    .remove(fragment)
                    .commit();
        }
    }

    /**
     * Handles updating UI when the crime is updated.
     *
     * @param crime the updated crime.
     * @deprecated
     */
    @Override
    public void onCrimeUpdated(Crime crime) {
        CrimeListFragment fragment = (CrimeListFragment) getSupportFragmentManager().findFragmentById(R.id.frame_container);
        fragment.updateAdapter();
    }

    /**
     * Removes the fragment when a crime is deleted.
     */
    @Override
    public void onCrimeDeleted() {
        getSupportFragmentManager().beginTransaction()
                .remove(getSupportFragmentManager().findFragmentById(R.id.frame_container_detail))
                .commit();
        onCrimeUpdated(null);
    }
}
