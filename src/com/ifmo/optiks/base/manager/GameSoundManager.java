package com.ifmo.optiks.base.manager;

import org.anddev.andengine.audio.music.Music;
import org.anddev.andengine.audio.music.MusicFactory;
import org.anddev.andengine.audio.sound.Sound;
import org.anddev.andengine.audio.sound.SoundFactory;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import java.io.IOException;

/**
 * Author: Aleksey Vladiev (Avladiev2@gmail.com)
 */

public class GameSoundManager {

    private static GameSoundManager ourInstance = new GameSoundManager();

    private final static int timeVibrate = 50;

    private final BaseGameActivity activity;

    public final Music musicBackGround;
    public final Sound shootLaser;

    private boolean playMusicBackGround = true;
    private boolean playShootLaser = true;
    private boolean vibrate = true;

    public static GameSoundManager getInstance(final BaseGameActivity gameActivity) {
        if (gameActivity != ourInstance.activity) {
            ourInstance = new GameSoundManager(gameActivity);
        }
        return ourInstance;
    }

    public boolean playBackGroundMusic() {
        if (playMusicBackGround) {
            musicBackGround.play();
            return true;
        } else {
            return false;
        }
    }

    public boolean playPlayShootLaser() {
        if (playShootLaser) {
            shootLaser.play();
            return true;
        }
        return false;
    }

    public void setPlayMusicBackGround(final boolean playMusicBackGround) {
        if (!playMusicBackGround && musicBackGround.isPlaying()) {
            musicBackGround.stop();
        }
        this.playMusicBackGround = playMusicBackGround;
    }

    public void setPlayShootLaser(final boolean playShootLaser) {
        this.playShootLaser = playShootLaser;
    }

    private GameSoundManager() {
        activity = null;
        musicBackGround = null;
        shootLaser = null;
    }

    private GameSoundManager(final BaseGameActivity gameActivity) {
        activity = gameActivity;
        MusicFactory.setAssetBasePath("mfx/");
        SoundFactory.setAssetBasePath("mfx/");
        try {
            musicBackGround = MusicFactory.createMusicFromAsset(activity.getEngine().getMusicManager(), activity, "back_ground.mp3");
            shootLaser = SoundFactory.createSoundFromAsset(activity.getEngine().getSoundManager(), activity, "laser.wav");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        musicBackGround.setLooping(true);
    }

    public void setVibrate(final boolean isVibrate) {
        vibrate = isVibrate;
    }

    public void vibrate() {
        if (vibrate) {
            activity.getEngine().vibrate(timeVibrate);
        }
    }
}
