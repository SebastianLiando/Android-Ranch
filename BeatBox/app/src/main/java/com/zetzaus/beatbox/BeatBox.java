package com.zetzaus.beatbox;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * This class is the main class for loading and playing the audio.
 */
public class BeatBox {
    private static final String TAG = "BeatBox";

    private static final String SOUNDS_FOLDER = "sounds";

    private static final int MAX_PLAYBACK = 5;

    private AssetManager mManager;
    private List<Sound> mSounds;
    private Queue<Integer> mQueue;
    private SoundPool mSoundPool;
    private float mPlaybackRate = 1f;

    /**
     * Creates a <code>BeatBox</code>.
     *
     * @param context the caller <code>Activity</code>.
     */
    public BeatBox(Context context) {
        mManager = context.getAssets();
        mSounds = new ArrayList<>();
        mQueue = new LinkedList<>();
        mSoundPool = new SoundPool(MAX_PLAYBACK, AudioManager.STREAM_MUSIC, 0);
        loadSounds();
    }

    /**
     * Load all sounds from the assets.
     */
    private void loadSounds() {
        String[] names;
        try {
            names = mManager.list(SOUNDS_FOLDER);
            Log.i(TAG, "Found " + names.length + " files");
            // Load
            for (String name : names) {
                Sound sound = new Sound(SOUNDS_FOLDER + "/" + name);
                try {
                    load(sound);
                } catch (IOException e) {
                    Log.e(TAG, "Could not load file " + sound.getName(), e);
                }
                mSounds.add(sound);
            }

        } catch (IOException e) {
            Log.e(TAG, "Cannot find files!");
            e.printStackTrace();
        }
    }

    /**
     * Returns a list of all sounds.
     *
     * @return a list of all sounds.
     */
    public List<Sound> getSounds() {
        return mSounds;
    }

    /**
     * Loads the sound into the memory.
     *
     * @param sound the sound to be loaded.
     * @throws IOException if the sound cannot be loaded.
     */
    private void load(Sound sound) throws IOException {
        AssetFileDescriptor descriptor = mManager.openFd(sound.getPath());
        int soundId = mSoundPool.load(descriptor, 1);
        sound.setSoundId(soundId);
    }

    /**
     * Plays the sound.
     *
     * @param sound the sound.
     * @param rate  the playback rate.
     */
    public void play(Sound sound, float rate) {
        Integer soundId = sound.getSoundId();

        // Make sure it is loaded in memory
        if (soundId == null) return;

        // Play
        int streamId = mSoundPool.play(soundId, 1f, 1f, 1, 0, rate);

        // Add to play history
        if (mQueue.size() >= MAX_PLAYBACK)
            mQueue.remove();
        mQueue.add(streamId);
    }

    /**
     * Change playback rate of the streaming audio. Does no effect if there is nothing playing.
     * The rate value is from 0.5 to 2.0.
     */
    private void changeRate(float rate) {
        // Clamp the rate
        if (rate < 0.5f) rate = 0.5f;
        if (rate > 2f) rate = 2f;

        for (Integer id : mQueue) {
            mSoundPool.setRate(id, rate);
        }
    }

    /**
     * Frees memory by releasing <code>SoundPool</code>.
     */
    public void release() {
        mSoundPool.release();
    }

    /**
     * Get the audio playback rate.
     *
     * @return the audio playback rate.
     */
    public float getPlaybackRate() {
        return mPlaybackRate;
    }

    /**
     * Change the playback rate of the playing audio and incoming audio.
     *
     * @param playbackRate the new playback rate.
     */
    public void setPlaybackRate(float playbackRate) {
        mPlaybackRate = playbackRate;
        changeRate(playbackRate);
    }
}
