package com.zetzaus.beatbox;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SoundViewModelTest {

    private BeatBox mBeatBox;
    private Sound mSound;
    private SoundViewModel mSubject;    // Subject to be tested

    @Before
    public void setUp() throws Exception {
        mBeatBox = mock(BeatBox.class);
        mSound = new Sound("assetPath");
        mSubject = new SoundViewModel(mBeatBox);
        mSubject.setSound(mSound);
    }

    @Test
    public void equalSoundName(){
        assertThat(mSound.getName(), is(mSubject.getSoundName()));
    }

    @Test
    public void callsPlayMethodOnButtonClick(){
        mSubject.onButtonClicked();
        verify(mBeatBox).play(mSound);
    }

}
