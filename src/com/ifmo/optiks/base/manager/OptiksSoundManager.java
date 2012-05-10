package com.ifmo.optiks.base.manager;

import org.anddev.andengine.audio.music.Music;
import org.anddev.andengine.audio.music.MusicFactory;
import org.anddev.andengine.audio.sound.Sound;
import org.anddev.andengine.audio.sound.SoundFactory;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import java.io.IOException;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 10.05.12
 */

public class OptiksSoundManager {

    private final static int DEFAULT_VIBRATION_TIME = 50;

    private final BaseGameActivity activity;

    public final Music backgroundMusic;
    public final Sound shootLaser;

    private boolean musicEnabled;
    private boolean soundEnabled;
    private boolean vibrationEnabled;

    public OptiksSoundManager(final BaseGameActivity activity) {

        this.activity = activity;

        // TODO load user settings
        musicEnabled = true;
        soundEnabled = true;
        vibrationEnabled = true;

        /* Musics & sounds assets paths */
        MusicFactory.setAssetBasePath("mfx/");
        SoundFactory.setAssetBasePath("mfx/");

        try {
            backgroundMusic = MusicFactory.createMusicFromAsset(activity.getEngine().getMusicManager(), activity, "back_ground.mp3");
            backgroundMusic.setLooping(true);
            shootLaser = SoundFactory.createSoundFromAsset(activity.getEngine().getSoundManager(), activity, "laser.wav");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public void playBackgroundMusic() {
        if (musicEnabled) {
            backgroundMusic.play();
        }
    }

    public void playLaserShoot() {
        if (soundEnabled) {
            shootLaser.play();
        }
    }

    public void vibrate() {
        vibrate(DEFAULT_VIBRATION_TIME);
    }

    public void vibrate(final int milliseconds) {
        if (vibrationEnabled) {
            activity.getEngine().vibrate(milliseconds);
        }
    }

    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    public void setMusicEnabled(final boolean musicEnabled) {
        this.musicEnabled = musicEnabled;
        if (musicEnabled) {
            backgroundMusic.play();
        } else {
            backgroundMusic.stop();
        }
    }

    public boolean isVibrationEnabled() {
        return vibrationEnabled;
    }

    public void setVibrationEnabled(final boolean vibrationEnabled) {
        this.vibrationEnabled = vibrationEnabled;
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public void setSoundEnabled(final boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
    }
}
