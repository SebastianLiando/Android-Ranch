package com.zetzaus.criminalintent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

/**
 * This fragment displays a larger photo of the crime's photo.
 */
public class PhotoFragment extends DialogFragment {

    private static final String EXTRA_FILE = "EXTRA_FILE";

    private ImageView mImage;
    private Button mButtonOK;
    private File mFileImage;

    /**
     * Returns a new instance of this fragment.
     *
     * @param imageFile the image file to be displayed.
     * @return a new instance of this fragment.
     */
    public static PhotoFragment newInstance(File imageFile) {

        Bundle args = new Bundle();
        args.putSerializable(EXTRA_FILE, imageFile);

        PhotoFragment fragment = new PhotoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Loads the image and setup the button.
     *
     * @param inflater           the layout inflater.
     * @param container          to be inflated.
     * @param savedInstanceState the saved system state.
     * @return the inflated layout of the fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_photo, container, false);

        mImage = v.findViewById(R.id.image_crime_photo);
        mButtonOK = v.findViewById(R.id.button_ok);
        mFileImage = (File) getArguments().getSerializable(EXTRA_FILE);

        // Display image
        if (mFileImage == null || !mFileImage.exists()) {
            return v;
        }

        Glide.with(getActivity()).load(mFileImage).into(mImage);

        // Setup button
        mButtonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return v;
    }
}
