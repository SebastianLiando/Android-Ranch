package com.zetzaus.beatbox;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class SoundViewModel extends BaseObservable {
    private Sound mSound;

    private BeatBox mBeatBox;

    public SoundViewModel(BeatBox beatBox) {
        mBeatBox = beatBox;
    }

    public Sound getSound() {
        return mSound;
    }

    @Bindable
    public String getSoundName() {
        return mSound.getName();
    }

    public void setSound(Sound sound) {
        mSound = sound;
        notifyPropertyChanged(BR.soundName);
    }

    public void onButtonClicked() {
        mBeatBox.play(mSound);
    }
}

