package com.zetzaus.locatr;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

/**
 * A simple {@link SupportMapFragment} subclass.
 */
public class LocatrFragment extends SupportMapFragment {

    private static final String TAG = LocatrFragment.class.getSimpleName();
    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private static final int REQUEST_LOCATION = 0;

    private Bitmap mImageBitmap;
    private GalleryItem mImageItem;
    private Location mCurrentLocation;
    private GoogleMap mGoogleMap;

//    private ImageView mImageView;
//    private ProgressBar mProgressBar;

    private FusedLocationProviderClient mClient;

    /**
     * Returns a {@link LocatrFragment}.
     *
     * @return a {@link LocatrFragment}.
     */
    public static LocatrFragment newInstance() {
        return new LocatrFragment();
    }

    /**
     * Tells that the {@link Fragment} has options and get reference to {@link FusedLocationProviderClient}.
     *
     * @param savedInstanceState the saved system state.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mClient = LocationServices.getFusedLocationProviderClient(getActivity());

        // Fetch map
        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
                updateUI();
            }
        });
    }

    /**
     * Inflates the toolbar with action.
     *
     * @param menu     the menu to be inflated.
     * @param inflater the menu inflater.
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_locatr, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
    }

    /**
     * Handles clicked menu item.
     *
     * @param item the clicked menu item.
     * @return true (handled).
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                if (hasPermission()) {
                    findImage();
//                    mProgressBar.setVisibility(View.VISIBLE);
                } else {
                    getPermission();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    /**
//     * Inflates the {@link Fragment}'s layout.
//     *
//     * @param inflater           the layout inflater.
//     * @param container          the container to be inflated.
//     * @param savedInstanceState the saved system state.
//     * @return the inflated {@link Fragment}.
//     */
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View v = inflater.inflate(R.layout.fragment_locatr, container, false);
//
//        mImageView = v.findViewById(R.id.image_view);
//        mProgressBar = v.findViewById(R.id.progress_download);
//
//        return v;
//    }

    /**
     * Gets the user's location and find images from the location in Flickr.
     */
    private void findImage() {
        final LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(0);
        request.setNumUpdates(1);

        LocationSettingsRequest.Builder settings = new LocationSettingsRequest.Builder()
                .addLocationRequest(request);
        SettingsClient settingsClient = LocationServices.getSettingsClient(getActivity());

        Task<LocationSettingsResponse> settingsResponseTask = settingsClient.checkLocationSettings(settings.build());
        settingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Log.i(TAG, "Successfully used the LocationRequest");
                mClient.requestLocationUpdates(request, new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                super.onLocationResult(locationResult);
                                for (Location location : locationResult.getLocations()) {
                                    Log.i(TAG, "Got location at: " + location);
                                    new SearchTask().execute(location);
                                }
                            }
                        }, Looper.getMainLooper()
                ).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), R.string.toast_failed, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        settingsResponseTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "Failed to use the LocationRequest");
                if (e instanceof ResolvableApiException) {
                    Log.i(TAG, "Try to resolve");
                    ResolvableApiException exception = (ResolvableApiException) e;
                    try {
                        exception.startResolutionForResult(getActivity(), 0);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * Checks if the user has granted the required permission.
     *
     * @return true if the user has granted the permission.
     */
    private boolean hasPermission() {
        return ContextCompat.checkSelfPermission(getActivity(), PERMISSIONS[0])
                == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION) {
            if (hasPermission()) {
                findImage();
            }
        }
    }

    /**
     * Requests permission on runtime with or without rationale.
     */
    private void getPermission() {
        if (shouldShowRequestPermissionRationale(PERMISSIONS[0])) {
            showRationaleDialog();
        } else {
            requestPermissions(PERMISSIONS, REQUEST_LOCATION);
        }
    }

    /**
     * Shows rationale for the request permission in an {@link AlertDialog}.
     */
    private void showRationaleDialog() {
        new AlertDialog.Builder(getActivity())
                .setMessage(getString(R.string.dialog_message_rationale))
                .setNegativeButton(android.R.string.ok, null)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(PERMISSIONS, REQUEST_LOCATION);
                    }
                }).show();
    }

    /**
     * Displays the user's location and the image location on the map.
     */
    private void updateUI() {
        if (mGoogleMap == null || mImageBitmap == null) return;

        LatLng imagePoint = new LatLng(mImageItem.getLatitude(), mImageItem.getLongitude());
        LatLng userPoint = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

        LatLngBounds mapBound = new LatLngBounds.Builder()
                .include(imagePoint)
                .include(userPoint)
                .build();

        int margin = getResources().getDimensionPixelSize(R.dimen.margin_inset_map);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(mapBound, margin);
        mGoogleMap.animateCamera(cameraUpdate);

        BitmapDescriptor image = BitmapDescriptorFactory.fromBitmap(mImageBitmap);
        MarkerOptions imageMarker = new MarkerOptions()
                .position(imagePoint)
                .icon(image);
        MarkerOptions userMarker = new MarkerOptions().position(userPoint);
        mGoogleMap.clear();
        mGoogleMap.addMarker(imageMarker);
        mGoogleMap.addMarker(userMarker);

    }

    /**
     * A custom {@link AsyncTask} for downloading the image and displaying it.
     */
    private class SearchTask extends AsyncTask<Location, Void, Void> {

        private GalleryItem mGalleryItem;
        private Bitmap mBitmap;
        private Location mLocation;

        /**
         * Downloads the image into a bitmap.
         *
         * @param locations the user's location.
         * @return null.
         */
        @Override
        protected Void doInBackground(Location... locations) {
            mLocation = locations[0];

            FlickrFetchr fetchr = new FlickrFetchr();

            List<GalleryItem> result = fetchr.searchPhotos(mLocation);

            if (result.isEmpty()) return null;

            Random random = new Random();
            mGalleryItem = result.get(random.nextInt(result.size()));

            try {
                byte[] data = fetchr.getUrlBytes(mGalleryItem.getURL());
                mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Displays the image in the {@link ImageView}.
         *
         * @param aVoid null.
         */
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mImageBitmap = mBitmap;
            mImageItem = mGalleryItem;
            mCurrentLocation = mLocation;
            updateUI();
//            mImageView.setImageBitmap(mBitmap);
//            mProgressBar.setVisibility(View.GONE);
        }
    }
}
