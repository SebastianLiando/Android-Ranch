package com.zetzaus.beatbox;

/**
 * This class is responsible for holding information about the sound.
 */
public class Sound {
    private String mName;
    private String mPath;
    private Integer mSoundId;

    /**
     * Creates a <code>Sound</code>.
     *
     * @param path the audio file path.
     */
    public Sound(String path) {
        mPath = path;

        // Clean file name
        String[] subs = path.split("__");
        String rawName = subs[subs.length - 1];
        String cleaned = rawName.replace("-", " ").substring(6);
        mName = cleaned.replace(".wav", "");

    }

    /**
     * Returns the name of the audio.
     *
     * @return the name of the audio.
     */
    public String getName() {
        return mName;
    }

    /**
     * Sets the name of the audio.
     *
     * @param name the name of the audio.
     */
    public void setName(String name) {
        mName = name;
    }

    /**
     * Returns the path to the audio file.
     *
     * @return the path to the audio file.
     */
    public String getPath() {
        return mPath;
    }

    /**
     * Sets the audio path to the file.
     *
     * @param path the audio path to the file.
     */
    public void setPath(String path) {
        mPath = path;
    }

    /**
     * Returns the sound id after it is loaded in the memory.
     *
     * @return the sound id after it is loaded in the memory.
     */
    public int getSoundId() {
        return mSoundId;
    }

    /**
     * Sets the sound id when the sound has been loaded to the memory.
     *
     * @param soundId the sound id.
     */
    public void setSoundId(int soundId) {
        mSoundId = soundId;
    }

}
