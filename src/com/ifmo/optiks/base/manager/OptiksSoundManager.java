package com.ifmo.optiks.base.manager;

import android.content.Context;
import android.content.SharedPreferences;
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

    private static final int DEFAULT_VIBRATION_TIME = 50;
    private static final String SOUND_PREFERENCES = "sound_prefs";
    private static final String MUSIC = "Music";
    private static final String SOUNDS = "Sounds";
    private static final String VIBRATION = "Vibration";

    private final BaseGameActivity activity;

    private final SharedPreferences preferences;

    public final Music backgroundMusic;
    public final Sound shootLaser;

    private boolean musicEnabled;
    private boolean soundEnabled;
    private boolean vibrationEnabled;

    public OptiksSoundManager(final BaseGameActivity activity) {

        this.activity = activity;
        this.preferences = activity.getSharedPreferences(SOUND_PREFERENCES, Context.MODE_WORLD_WRITEABLE);

        /* Loading user's preferences */
        musicEnabled = (preferences.getInt(MUSIC, 0) > 0);
        soundEnabled = (preferences.getInt(SOUNDS, 0) > 0);
        vibrationEnabled = (preferences.getInt(VIBRATION, 0) > 0);

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
        //backgroundMusic.play();
        this.musicEnabled = musicEnabled;
        final SharedPreferences.Editor editor =  preferences.edit();
        if (musicEnabled) {
            backgroundMusic.play();
           editor.putInt(MUSIC, 1);
        } else {
            backgroundMusic.pause();
           editor.putInt(MUSIC, 0);
        }
        editor.apply();
    }

    public boolean isVibrationEnabled() {
        return vibrationEnabled;
    }

    public void setVibrationEnabled(final boolean vibrationEnabled) {
        this.vibrationEnabled = vibrationEnabled;
        final SharedPreferences.Editor editor =  preferences.edit();
        if (vibrationEnabled) {
            editor.putInt(VIBRATION, 1);
        } else {
            editor.putInt(VIBRATION, 0);
        }
        editor.apply();
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public void setSoundEnabled(final boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
        final SharedPreferences.Editor editor =  preferences.edit();
        if (soundEnabled) {
            editor.putInt(SOUNDS, 1);
        } else {
            editor.putInt(SOUNDS, 0);
        }
        editor.apply();
    }
}
