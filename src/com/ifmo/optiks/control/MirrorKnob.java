package com.ifmo.optiks.control;

import android.util.Log;
import com.badlogic.gdx.physics.box2d.Body;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.util.MathUtils;

/**
 * User: dududko@gmail.com
 * Date: 06.04.12
 */
public class MirrorKnob extends Sprite {
    private final float r = 50f;
    private Body target;
    private KnobRotationControlListener listener;


    public MirrorKnob(final float pX, final float pY, final TextureRegion pTextureRegion) {
        super(pX, pY, pTextureRegion);
        super.setScale((float) 0.5);
        setVisible(false);
    }

    @Override
    public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
        switch (pSceneTouchEvent.getAction()) {
            case TouchEvent.ACTION_DOWN:
                break;
            case TouchEvent.ACTION_MOVE:
                rotate(pTouchAreaLocalX - super.getRotationCenterX(), pTouchAreaLocalY - super.getRotationCenterY());
                break;
            case TouchEvent.ACTION_UP:
        }
        return true;
    }

    public void setTarget(final Body target) {
        if (target != null) {
            this.target = target;
            final float x = target.getPosition().x * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT - super.getWidth() / 2;
            final float y = target.getPosition().y * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT - super.getHeight() / 2;
            setPosition(x + r, y);
            final float angleRad = target.getAngle() - (float) Math.PI / 2;
            setRotationCenter(super.getWidth() / 2 - r, super.getHeight() / 2);
            setRotation(MathUtils.radToDeg(angleRad));
            setVisible(true);
        }
    }

    public Body getTarget() {
        return target;
    }

    private void rotate(final float x, final float y) {
        Log.d("knob", "angle: " + MathUtils.radToDeg(MathUtils.atan2(x, y)));
        target.setTransform(target.getPosition(), (float) (Math.atan2(x, -y) + Math.PI / 2));
        final float angleRad = (float) (MathUtils.atan2(x, y));
        super.setRotation(-MathUtils.radToDeg(angleRad));
    }
}
