package com.zetzaus.beatbox;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.zetzaus.beatbox.databinding.FragmentBeatBoxBinding;
import com.zetzaus.beatbox.databinding.ItemButtonBinding;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BeatBoxFragment extends Fragment {

    private BeatBox mBeatBox;

    /**
     * Initializes <code>BeatBox</code> and make the fragment retained.
     *
     * @param savedInstanceState the saved system state.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load sounds
        mBeatBox = new BeatBox(getActivity());

        // Prevent stopping due to rotation
        setRetainInstance(true);
    }

    /**
     * Inflates the layout and setup the <code>View</code>s.
     *
     * @param inflater           the layout inflater.
     * @param container          the container to be inflated.
     * @param savedInstanceState the saved system state.
     * @return the inflated layout.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final FragmentBeatBoxBinding root =
                DataBindingUtil.inflate(inflater, R.layout.fragment_beat_box, container, false);
        root.setViewModel(new SoundViewModel(mBeatBox));

        // Setup recycler view
        root.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        root.recyclerView.setAdapter(new SoundAdapter(mBeatBox.getSounds()));

        root.seekBarRate.setProgress(100);
        root.seekBarRate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 50) {
                    seekBar.setProgress(50);
                    Toast.makeText(getActivity(), R.string.toast_min_value, Toast.LENGTH_SHORT).show();
                }

                // All view models always refer to the same BeatBox, hence, setting the rate of one BeatBox
                // will set all view models' rate.
                root.getViewModel().setPlaybackRate(seekBar.getProgress() / 100f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return root.getRoot();
    }

    /**
     * Release sound resources.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mBeatBox.release();
    }

    /**
     * This class is a custom adapter for <code>RecyclerView</code>.
     */
    private class SoundAdapter extends RecyclerView.Adapter<SoundAdapter.ViewHolder> {

        private List<Sound> mSounds;

        /**
         * Creates a <code>SoundAdapter</code>.
         *
         * @param sounds the collections of sounds loaded.
         */
        public SoundAdapter(List<Sound> sounds) {
            mSounds = sounds;
        }

        /**
         * Creates a <code>ViewHolder</code>.
         *
         * @param parent   the parent.
         * @param viewType the type of view. Currently it is not used.
         * @return a <code>ViewHolder</code>.
         */
        @NonNull
        @Override
        public SoundAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            ItemButtonBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_button, parent, false);
            return new ViewHolder(binding);
        }

        /**
         * Binds the layout with the data.
         *
         * @param holder   holds the <code>View</code> to be bound.
         * @param position the position of the <code>ViewHolder</code>.
         */
        @Override
        public void onBindViewHolder(@NonNull SoundAdapter.ViewHolder holder, int position) {
            holder.bind(mSounds.get(position));
        }

        /**
         * Returns the amount of sounds loaded.
         *
         * @return the amount of sounds loaded.
         */
        @Override
        public int getItemCount() {
            return mSounds.size();
        }

        /**
         * This class holds the <code>View</code>s item in a single row.
         */
        private class ViewHolder extends RecyclerView.ViewHolder {

            ItemButtonBinding mBinding;

            /**
             * Creates a <code>ViewHolder</code>.
             *
             * @param binding the binding class.
             */
            public ViewHolder(@NonNull ItemButtonBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
                mBinding.setViewModel(new SoundViewModel(mBeatBox));
            }

            /**
             * Binds the data of a <code>Sound</code> to the layout.
             *
             * @param sound the <code>Sound</code> data.
             */
            public void bind(Sound sound) {
                // Add data
                mBinding.getViewModel().setSound(sound);
                // Update UI
                mBinding.executePendingBindings();
            }
        }
    }
}
