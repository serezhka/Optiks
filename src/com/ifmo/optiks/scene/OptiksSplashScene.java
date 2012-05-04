package com.ifmo.optiks.scene;

import android.view.KeyEvent;
import com.ifmo.optiks.OptiksActivity;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.input.touch.TouchEvent;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 19.04.12
 */

public class OptiksSplashScene extends OptiksScene implements Scene.IOnSceneTouchListener {

    private final OptiksActivity optiksActivity;

    public OptiksSplashScene(final OptiksActivity optiksActivity, final Sprite splash) {
        super();
        this.optiksActivity = optiksActivity;
        this.attachChild(splash);
        this.setOnSceneTouchListener(this);
    }

    @Override
    public boolean onSceneTouchEvent(final Scene scene, final TouchEvent touchEvent) {
        if (touchEvent.isActionUp() && optiksActivity.loadComplete) {
            optiksActivity.setActiveScene(optiksActivity.scenes.get(OptiksScenes.MENU_SCENE));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
        return this.onSceneTouchEvent(this, new TouchEvent());
    }
}
