package com.zetzaus.beatbox;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load sounds
        mBeatBox = new BeatBox(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentBeatBoxBinding root =
                DataBindingUtil.inflate(inflater, R.layout.fragment_beat_box, container, false);


        // Setup recycler view
        root.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        root.recyclerView.setAdapter(new SoundAdapter(mBeatBox.getSounds()));

        return root.getRoot();
    }

    private class SoundAdapter extends RecyclerView.Adapter<SoundAdapter.ViewHolder> {

        private List<Sound> mSounds;

        public SoundAdapter(List<Sound> sounds) {
            mSounds = sounds;
        }

        @NonNull
        @Override
        public SoundAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            ItemButtonBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_button, parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull SoundAdapter.ViewHolder holder, int position) {
            holder.bind(mSounds.get(position));
        }

        @Override
        public int getItemCount() {
            return mSounds.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {

            ItemButtonBinding mBinding;

            public ViewHolder(@NonNull ItemButtonBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
                mBinding.setViewModel(new SoundViewModel(mBeatBox));
            }

            public void bind(Sound sound){
                // Add data
                mBinding.getViewModel().setSound(sound);
                // Update UI
                mBinding.executePendingBindings();
            }
        }
    }
}
