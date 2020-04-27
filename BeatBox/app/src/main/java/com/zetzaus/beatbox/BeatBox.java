package com.zetzaus.beatbox;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import java.io.IOException;
import java.util.List;

public class BeatBox {
    private static final String TAG = "BeatBox";

    private static final String SOUNDS_FOLDER = "sounds";

    private static final int MAX_PLAYBACK = 5;

    private AssetManager mManager;
    private List<Sound> mSounds;
    private SoundPool mSoundPool;

    public BeatBox(Context context) {
        mManager = context.getAssets();
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
     */
    public void play(Sound sound) {
        Integer soundId = sound.getSoundId();

        // Make sure it is loaded in memory
        if (soundId == null) return;

        // Play
        mSoundPool.play(soundId, 1f, 1f, 1, 0, 1);
    }

}
