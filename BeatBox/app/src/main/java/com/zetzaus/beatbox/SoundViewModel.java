package com.zetzaus.beatbox;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

/**
 * This class is a view model class to bind data to the layout.
 */
public class SoundViewModel extends BaseObservable {
    private Sound mSound;

    private BeatBox mBeatBox;

    /**
     * Creates a <code>SoundViewModel</code>.
     *
     * @param beatBox the <code>BeatBox</code> object used.
     */
    public SoundViewModel(BeatBox beatBox) {
        mBeatBox = beatBox;
    }

    /**
     * Returns the current <code>Sound</code>.
     *
     * @return the current <code>Sound</code>.
     */
    public Sound getSound() {
        return mSound;
    }

    /**
     * Returns the title of the current <code>Sound</code>.
     *
     * @return the title of the current <code>Sound</code>.
     */
    @Bindable
    public String getSoundName() {
        return mSound.getName();
    }

    /**
     * Returns the playback rate of audio in percentage form.
     *
     * @return the playback rate of audio in percentage form.
     */
    @Bindable
    public String getStringPlaybackRate() {
        return (mBeatBox.getPlaybackRate() * 100) + "%";
    }

    /**
     * Sets the playback rate of audio.
     *
     * @param rate the playback rate of audio.
     */
    public void setPlaybackRate(float rate) {
        mBeatBox.setPlaybackRate(rate);
        notifyPropertyChanged(BR.stringPlaybackRate);
    }

    /**
     * Sets the current <code>Sound</code>.
     *
     * @param sound the new <code>Sound</code> to set.
     */
    public void setSound(Sound sound) {
        mSound = sound;
        notifyPropertyChanged(BR.soundName);
    }

    /**
     * Callback function for the <code>RecyclerView</code> items.
     */
    public void onButtonClicked() {
        mBeatBox.play(mSound, mBeatBox.getPlaybackRate());
    }
}

