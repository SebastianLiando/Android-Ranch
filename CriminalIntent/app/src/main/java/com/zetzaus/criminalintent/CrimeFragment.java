package com.zetzaus.criminalintent;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

/**
 * This fragment displays the details of a crime.
 */
public class CrimeFragment extends Fragment {

    private static final String TAG_DATE_PICKER = DatePickerFragment.class.getSimpleName();
    private static final String TAG_TIME_PICKER = TimePickerFragment.class.getSimpleName();
    private static final String TAG_PHOTO = PhotoFragment.class.getSimpleName();

    private static final String ARG_UUID = BuildConfig.APPLICATION_ID + "EXTRA_UUID";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_SUSPECT = 2;
    private static final int REQUEST_PHOTO = 3;

    private Crime mCrime;
    private File mPhoto;    // Stores pointer to the image location
    private Callback mCallback;

    private EditText mEditTextTitle;
    private Button mDateButton;
    private Button mTimeButton;
    private Button mSendButton;
    private Button mSuspectButton;
    private ImageButton mCallButton;
    private ImageButton mCameraButton;
    private ImageView mImagePhoto;
    private CheckBox mCheckBoxSolved;
    private CheckBox mCheckBoxPolice;

    public interface Callback {
        void onCrimeUpdated(Crime crime);

        void onCrimeDeleted();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mCallback = (Callback) context;
    }

    /**
     * Creates a new instance of the <code>CrimeFragment</code>.
     *
     * @param crimeId the crime id to be used.
     * @return this fragment.
     */
    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_UUID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    /**
     * Creates the fragment, binds the correct crime.
     *
     * @param savedInstanceState the saved system state.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get crime
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_UUID);
        mCrime = CrimeLab.getInstance(getActivity()).getCrime(crimeId);
        mPhoto = CrimeLab.getInstance(getActivity()).getCrimePhoto(mCrime);
        // Tell about menu
        setHasOptionsMenu(true);
    }

    /**
     * Inflates the layout and sets up the functionality.
     *
     * @param inflater           the layout inflater.
     * @param container          the container to be inflated.
     * @param savedInstanceState the saved system state.
     * @return the view to display.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parent = inflater.inflate(R.layout.fragment_crime, container, false);

        // Setup edit text
        mEditTextTitle = parent.findViewById(R.id.edit_text_title);
        mEditTextTitle.setText(mCrime.getTitle());
        mEditTextTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
                updateCrime();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Set the date button
        mDateButton = parent.findViewById(R.id.button_crime_date);
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getResources().getBoolean(R.bool.isTablet)) {
                    FragmentManager manager = getFragmentManager();
                    PickerFragment fragment = DatePickerFragment.newInstance(mCrime.getDate());
                    fragment.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                    fragment.show(manager, TAG_DATE_PICKER);
                } else {
                    Intent intent = DatePickerActivity.newIntent(getActivity(), mCrime.getId());
                    startActivityForResult(intent, REQUEST_DATE);
                }
            }
        });

        // Setup time button
        mTimeButton = parent.findViewById(R.id.button_crime_time);
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getResources().getBoolean(R.bool.isTablet)) {
                    FragmentManager manager = getFragmentManager();
                    PickerFragment fragment = TimePickerFragment.newInstance(mCrime.getDate());
                    fragment.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                    fragment.show(manager, TAG_TIME_PICKER);
                } else {
                    Intent intent = TimePickerActivity.newIntent(getActivity(), mCrime.getId());
                    startActivityForResult(intent, REQUEST_TIME);
                }
            }
        });

        updateDateTime();

        // Setup solved check box
        mCheckBoxSolved = parent.findViewById(R.id.checkbox_crime_solved);
        mCheckBoxSolved.setChecked(mCrime.isSolved());
        mCheckBoxSolved.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
                updateCrime();
            }
        });

        // Setup requires police check box
        mCheckBoxPolice = parent.findViewById(R.id.checkbox_requires_police);
        mCheckBoxPolice.setChecked(mCrime.isRequiresPolice());
        mCheckBoxPolice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setRequiresPolice(isChecked);
                updateCrime();
            }
        });

        //Setup send report button
        mSendButton = parent.findViewById(R.id.button_report);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareCompat.IntentBuilder
                        .from(getActivity())
                        .setType("text/plain")
                        .setText(getCrimeReport())
                        .setSubject(getString(R.string.crime_report_subject))
                        .setChooserTitle(R.string.implicit_send_report)
                        .startChooser();
            }
        });

        // Setup suspect button
        final Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = parent.findViewById(R.id.button_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS)
                            == PackageManager.PERMISSION_GRANTED) {
                        startActivityForResult(intent, REQUEST_SUSPECT);
                    } else {
                        Toast.makeText(getActivity(), R.string.toast_no_permission, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), R.string.toast_no_contact, Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        mCallButton = parent.findViewById(R.id.image_button_call);
        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri callUri = Uri.parse("tel:" + mCrime.getSuspectNum());
                Intent callIntent = new Intent(Intent.ACTION_DIAL, callUri);
                startActivity(callIntent);
            }
        });

        // Setup crime photo
        mImagePhoto = parent.findViewById(R.id.image_crime_photo);

        // Setup camera button
        mCameraButton = parent.findViewById(R.id.image_button_camera);
        final Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (mPhoto == null || cameraIntent.resolveActivity(getActivity().getPackageManager()) == null)
            mCameraButton.setEnabled(false);
        else mCameraButton.setEnabled(true);

        mCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri targetUri = FileProvider.getUriForFile(getActivity()
                        , "com.zetzaus.criminalintent.fileprovider",
                        mPhoto);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, targetUri);

                //Grant permission for camera apps
                List<ResolveInfo> activities = getActivity().getPackageManager().queryIntentActivities(
                        cameraIntent, PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo activity : activities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName, targetUri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }

                startActivityForResult(cameraIntent, REQUEST_PHOTO);
            }
        });

        updatePhoto();

        return parent;
    }

    /**
     * Saves crime details to the database when the activity enters pause mode.
     */
    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.getInstance(getActivity()).updateCrime(mCrime);
    }

    /**
     * Handles reply from another activity/fragment to update the crime's data.
     *
     * @param requestCode the request code.
     * @param resultCode  the result code.
     * @param data        the new data.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) return;

        if (requestCode == REQUEST_SUSPECT) {
            // Query suspect name
            Uri contactUri = data.getData();
            String[] queryFields = new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME};
            Cursor contactQuery = getActivity().getContentResolver()
                    .query(contactUri, queryFields, null, null, null);

            try {
                // Check no data
                if (contactQuery.getCount() == 0) return;

                contactQuery.moveToFirst();
                mCrime.setSuspect(contactQuery.getString(1));
                mSuspectButton.setText(mCrime.getSuspect());

                // Query phone number
                String contactId = contactQuery.getString(0);
                Cursor numberQuery = getActivity().getContentResolver()
                        .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{contactId}, null);

                try {
                    numberQuery.moveToFirst();
                    String phoneNumber = numberQuery.getString(0);
                    mCrime.setSuspectNum(phoneNumber);
                    updateCrime();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    numberQuery.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                contactQuery.close();
            }
        } else if (requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "com.zetzaus.criminalintent.fileprovider", mPhoto);
            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            updatePhoto();
            updateCrime();
        } else {
            if (data != null) {
                // Get new date
                Date newDate = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                Calendar newCalendar = Calendar.getInstance();
                newCalendar.setTime(newDate);

                // Get old date
                Date oldDate = mCrime.getDate();
                Calendar oldCalendar = Calendar.getInstance();
                oldCalendar.setTime(oldDate);

                // Set new date or time
                if (requestCode == REQUEST_DATE) {
                    oldCalendar.set(Calendar.YEAR, newCalendar.get(Calendar.YEAR));
                    oldCalendar.set(Calendar.MONTH, newCalendar.get(Calendar.MONTH));
                    oldCalendar.set(Calendar.DAY_OF_MONTH, newCalendar.get(Calendar.DAY_OF_MONTH));
                } else if (requestCode == REQUEST_TIME) {
                    oldCalendar.set(Calendar.HOUR_OF_DAY, newCalendar.get(Calendar.HOUR_OF_DAY));
                    oldCalendar.set(Calendar.MINUTE, newCalendar.get(Calendar.MINUTE));
                }

                mCrime.setDate(oldCalendar.getTime());
                updateDateTime();
                updateCrime();
            }
        }
    }

    /**
     * Inflates menu to the toolbar.
     *
     * @param menu     the menu to be inflated.
     * @param inflater the inflater.
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }

    /**
     * Handles action when a menu item is selected.
     *
     * @param item the selected menu item.
     * @return true if the action is handled.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_menu_delete:
                deleteCrime();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    /**
     * Returns the currently used <code>Crime</code> object.
     *
     * @return the currently used <code>Crime</code> object.
     */
    public Crime getCrime() {
        return mCrime;
    }

    /**
     * Updates the buttons that displays date and time.
     */
    private void updateDateTime() {
        mDateButton.setText(mCrime.getDateString());
        mTimeButton.setText(mCrime.getTimeString());
    }

    /**
     * Creates a crime report from the crime's information.
     *
     * @return a crime report.
     */
    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSolved()) solvedString = getString(R.string.crime_report_solved);
        else solvedString = getString(R.string.crime_report_unsolved);

        String suspectString = null;
        if (mCrime.getSuspect() == null)
            suspectString = getString(R.string.crime_report_no_suspect);
        else suspectString = getString(R.string.crime_report_suspect, mCrime.getSuspect());

        String dateString = mCrime.getDateString() + " " + mCrime.getTimeString();

        return getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspectString);
    }

    /**
     * Updates <code>ImageView</code> for the photo of the crime.
     */
    private void updatePhoto() {
        if (mPhoto == null || !mPhoto.exists()) {
            mImagePhoto.setImageDrawable(null);
            // Not clickable
            mImagePhoto.setOnClickListener(null);
        } else {
            ViewTreeObserver imageObserver = mImagePhoto.getViewTreeObserver();
            imageObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
//                    Bitmap image = PictureUtils.getAccurateBitmap(mPhoto.getPath(), mImagePhoto);
//                    mImagePhoto.setImageBitmap(image);
                    if (getActivity() != null) {
                        Glide.with(getActivity()).load(mPhoto).into(mImagePhoto);
                    }
                }
            });

            mImagePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PhotoFragment fragment = PhotoFragment.newInstance(mPhoto);
                    fragment.show(getFragmentManager(), TAG_PHOTO);
                }
            });
        }
    }

    /**
     * Updates crime and the corresponding list. Used to support tablet implementation.
     */
    private void updateCrime() {
        CrimeLab.getInstance(getActivity()).updateCrime(mCrime);
        mCallback.onCrimeUpdated(mCrime);
    }

    /**
     * Removes the crime and invoke the activity's callback.
     */
    private void deleteCrime() {
        CrimeLab.getInstance(getActivity()).deleteCrime(mCrime.getId());
        mCallback.onCrimeDeleted();
    }

}
