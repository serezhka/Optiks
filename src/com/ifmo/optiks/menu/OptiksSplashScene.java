package com.ifmo.optiks.menu;

import com.ifmo.optiks.OptiksActivity;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.input.touch.TouchEvent;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 19.04.12
 */

public class OptiksSplashScene extends Scene implements Scene.IOnSceneTouchListener {

    private final OptiksActivity optiksActivity;

    public OptiksSplashScene(final OptiksActivity optiksActivity, final Sprite splash) {
        super();
        this.optiksActivity = optiksActivity;
        this.attachChild(splash);
        this.setOnSceneTouchListener(this);
    }

    @Override
    public boolean onSceneTouchEvent(final Scene scene, final TouchEvent touchEvent) {
        if (optiksActivity.loadComplete) {
            optiksActivity.setActiveScene(optiksActivity.menuScene);
            return true;
        } else {
            return false;
        }
    }
}
