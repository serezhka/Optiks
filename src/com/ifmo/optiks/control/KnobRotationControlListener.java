package com.ifmo.optiks.control;

import org.anddev.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.util.MathUtils;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 28.03.12
 */

public class KnobRotationControlListener implements KnobRotationControl.IKnobRotationControlListener {

    private Sprite sprite;

    public Sprite getSprite() {
        return sprite;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    @Override
    public void onControlChange(final BaseOnScreenControl pBaseOnScreenControl, final float pValueX, final float pValueY) {
        if (!(pValueX == 0 && pValueY == 0)) {
            sprite.setRotation(MathUtils.radToDeg((float) Math.atan2(pValueX, -pValueY)));
        }
    }

    @Override
    public void onControlClick(final KnobRotationControl pKnobRotationControl) {
        /* Nothing */
    }
}