package com.ifmo.optiks.control;

import android.util.Log;
import com.badlogic.gdx.physics.box2d.Body;
import org.anddev.andengine.engine.camera.hud.controls.BaseOnScreenControl;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 28.03.12
 */

public class KnobRotationControlListener implements KnobRotationControl.IKnobRotationControlListener {

    private Body body;

    public Body getBody() {
        return body;
    }

    public void setBody(final Body body) {
        this.body = body;
    }

    @Override
    public void onControlChange(final BaseOnScreenControl pBaseOnScreenControl, final float pValueX, final float pValueY) {
        Log.d("knob", "ok");
        if (body != null) {
            if (!(pValueX == 0 && pValueY == 0)) {
                // body.setType(BodyDef.BodyType.DynamicBody);
                body.setTransform(body.getPosition(), (float) Math.atan2(pValueX, -pValueY));
                // body.setType(BodyDef.BodyType.StaticBody);
            }
        }
    }

    @Override
    public void onControlClick(final KnobRotationControl pKnobRotationControl) {
        /* Nothing */
    }
}