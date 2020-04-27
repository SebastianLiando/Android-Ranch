package com.zetzaus.beatbox;

/**
 * This class is responsible for holding information about the sound.
 */
public class Sound {
    private String mName;
    private String mPath;
    private Integer mSoundId;

    public Sound(String path) {
        mPath = path;

        // Clean file name
        String[] subs = path.split("__");
        String rawName = subs[subs.length - 1];
        String cleaned = rawName.replace("-", " ").substring(6);
        mName = cleaned.replace(".wav", "");

    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        mPath = path;
    }

    public int getSoundId() {
        return mSoundId;
    }

    public void setSoundId(int soundId) {
        mSoundId = soundId;
    }
}
