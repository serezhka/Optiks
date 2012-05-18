package com.ifmo.optiks.scene;

import android.view.KeyEvent;
import com.ifmo.optiks.OptiksActivity;
import org.anddev.andengine.entity.Entity;
import org.anddev.andengine.entity.modifier.ColorModifier;
import org.anddev.andengine.entity.modifier.LoopEntityModifier;
import org.anddev.andengine.entity.modifier.SequenceEntityModifier;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.input.touch.TouchEvent;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 19.04.12
 */

public class OptiksSplashScene extends OptiksScene implements Scene.IOnSceneTouchListener {

    private final OptiksActivity optiksActivity;

    public OptiksSplashScene(final OptiksActivity optiksActivity, final Entity... sprites) {

        super();

        final Sprite splash = (Sprite) sprites[0];

        final AnimatedSprite mirror = (AnimatedSprite) sprites[1];
        mirror.animate(50);

        final Sprite tapToScreen = (Sprite) sprites[2];
        final LoopEntityModifier blinkModifier =
                new LoopEntityModifier(new SequenceEntityModifier(
                        new ColorModifier(1.0f, 1f, 0f, 1f, 0f, 1f, 0f),
                        new ColorModifier(1.0f, 0f, 1f, 0f, 1f, 0f, 1f)
                ),
                        Integer.MAX_VALUE
                );

        tapToScreen.registerEntityModifier(blinkModifier);

        this.optiksActivity = optiksActivity;
        this.attachChild(splash);
        this.attachChild(mirror);
        this.attachChild(tapToScreen);

        this.setOnSceneTouchListener(this);
    }

    @Override
    public boolean onSceneTouchEvent(final Scene scene, final TouchEvent touchEvent) {
        return touchEvent.isActionUp() && launchGame();
    }

    @Override
    public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
        return launchGame();
    }

    private boolean launchGame() {
        if (optiksActivity.loadComplete) {
            optiksActivity.setActiveScene(optiksActivity.scenes.get(OptiksScenes.MENU_SCENE));
            return true;
        }
        return false;
    }
}
