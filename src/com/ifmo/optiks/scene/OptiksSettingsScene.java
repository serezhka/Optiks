package com.ifmo.optiks.scene;

import android.view.KeyEvent;
import com.ifmo.optiks.OptiksActivity;
import org.anddev.andengine.entity.sprite.TiledSprite;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 22.04.12
 */

public class OptiksSettingsScene extends OptiksScene {

    private final OptiksActivity optiksActivity;

    private boolean musicEnabled;
    private boolean soundEnabled;
    private boolean vibrationEnabled;

    public OptiksSettingsScene(final OptiksActivity optiksActivity) {
        super();
        this.optiksActivity = optiksActivity;
        init();
    }

    @Override
    public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
        if (pKeyCode == KeyEvent.KEYCODE_BACK && pEvent.getAction() == KeyEvent.ACTION_DOWN) {
            optiksActivity.setActiveScene(optiksActivity.scenes.get(OptiksScenes.MENU_SCENE));
            return true;
        }
        return false;
    }

    private void init() {

        musicEnabled = optiksActivity.getOptiksSoundManager().isMusicEnabled();
        soundEnabled = optiksActivity.getOptiksSoundManager().isMusicEnabled();
        vibrationEnabled = optiksActivity.getOptiksSoundManager().isVibrationEnabled();

        /* Texture regions */
        final TiledTextureRegion musicTextureRegion = optiksActivity.getOptiksTextureManager().musicTextureRegion;
        final TiledTextureRegion soundTextureRegion = optiksActivity.getOptiksTextureManager().soundTextureRegion;
        final TiledTextureRegion vibrationTextureRegion = optiksActivity.getOptiksTextureManager().vibrationTextureRegion;

        /* Sound button */
        final TiledSprite sound = new TiledSprite(100, 200, soundTextureRegion) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX,
                                         final float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionUp()) {
                    setSoundEnabled(!soundEnabled);
                    final int tileIndex = soundEnabled ? 1 : 0;
                    setCurrentTileIndex(tileIndex);
                    return true;
                }
                return false;
            }
        };
        int tileIndex = soundEnabled ? 1 : 0;
        sound.setCurrentTileIndex(tileIndex);
        this.registerTouchArea(sound);
        this.attachChild(sound);

        /* Vibration button */
        final TiledSprite vibration = new TiledSprite(300, 200, vibrationTextureRegion) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX,
                                         final float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionUp()) {
                    setVibrationEnabled(!vibrationEnabled);
                    final int tileIndex = vibrationEnabled ? 1 : 0;
                    setCurrentTileIndex(tileIndex);
                    return true;
                }
                return false;
            }
        };
        tileIndex = vibrationEnabled ? 1 : 0;
        vibration.setCurrentTileIndex(tileIndex);
        this.registerTouchArea(vibration);
        this.attachChild(vibration);

        /* Music button */
        final TiledSprite music = new TiledSprite(500, 200, 100, 100, musicTextureRegion) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX,
                                         final float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionUp()) {
                    setMusicEnabled(!musicEnabled);
                    final int tileIndex = musicEnabled ? 1 : 0;
                    setCurrentTileIndex(tileIndex);
                    return true;
                }
                return false;
            }
        };
        tileIndex = musicEnabled ? 1 : 0;
        music.setCurrentTileIndex(tileIndex);
        this.registerTouchArea(music);
        this.attachChild(music);
    }

    private void setVibrationEnabled(final boolean enabled) {
        vibrationEnabled = enabled;
        optiksActivity.getOptiksSoundManager().setVibrationEnabled(enabled);
    }

    private void setSoundEnabled(final boolean enabled) {
        soundEnabled = enabled;
        optiksActivity.getOptiksSoundManager().setSoundEnabled(enabled);
    }

    private void setMusicEnabled(final boolean enabled) {
        musicEnabled = enabled;
        optiksActivity.getOptiksSoundManager().setMusicEnabled(enabled);
    }
}
