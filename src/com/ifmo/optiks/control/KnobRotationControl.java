package com.ifmo.optiks.control;

import android.util.FloatMath;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.input.touch.detector.ClickDetector;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.util.MathUtils;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 28.03.12
 */

public class KnobRotationControl extends BaseOnScreenControl implements ClickDetector.IClickDetectorListener {

    private KnobRotationControlListener listener;

    private final ClickDetector mClickDetector = new ClickDetector(this);

    public KnobRotationControl(final float pX, final float pY, final Camera pCamera, final TextureRegion pControlBaseTextureRegion, final TextureRegion pControlKnobTextureRegion, final float pTimeBetweenUpdates) {
        super(pX, pY, pCamera, pControlBaseTextureRegion, pControlKnobTextureRegion, pTimeBetweenUpdates, new KnobRotationControlListener());
        this.mClickDetector.setEnabled(false);
        this.listener = (KnobRotationControlListener) super.getOnScreenControlListener();
        onUpdateControlKnob(0, 0);
    }

    public KnobRotationControl(final float pX, final float pY, final Camera pCamera, final TextureRegion pControlBaseTextureRegion, final TextureRegion pControlKnobTextureRegion, final float pTimeBetweenUpdates, final IKnobRotationControlListener pAnalogOnScreenControlListener) {
        super(pX, pY, pCamera, pControlBaseTextureRegion, pControlKnobTextureRegion, pTimeBetweenUpdates, pAnalogOnScreenControlListener);
        this.mClickDetector.setEnabled(false);
        onUpdateControlKnob(0, 0);
    }

    @Override
    public IKnobRotationControlListener getOnScreenControlListener() {
        return (IKnobRotationControlListener) super.getOnScreenControlListener();
    }

    public void setOnControlClickEnabled(final boolean pOnControlClickEnabled) {
        this.mClickDetector.setEnabled(pOnControlClickEnabled);
    }

    public void setOnControlClickMaximumMilliseconds(final long pOnControlClickMaximumMilliseconds) {
        this.mClickDetector.setTriggerClickMaximumMilliseconds(pOnControlClickMaximumMilliseconds);
    }

    @Override
    protected boolean onHandleControlBaseTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
        this.mClickDetector.onSceneTouchEvent(null, pSceneTouchEvent);
        return super.onHandleControlBaseTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
    }

    @Override
    protected void onUpdateControlKnob(final float pRelativeX, final float pRelativeY) {
        if (pRelativeX * pRelativeX + pRelativeY * pRelativeY == 0.25f) {
            super.onUpdateControlKnob(pRelativeX, pRelativeY);
        } else {
            final float angleRad = MathUtils.atan2(pRelativeY, pRelativeX);
            super.onUpdateControlKnob(FloatMath.cos(angleRad) * 0.5f, FloatMath.sin(angleRad) * 0.5f);
        }
    }

    @Override
    public void onClick(ClickDetector clickDetector, TouchEvent touchEvent) {

    }

    @Override
    protected void onHandleControlKnobReleased() {

    }

    public Sprite getTarget() {
        return listener.getSprite();
    }

    public void setTarget(Sprite target) {
        final float angleRad = (float) Math.toRadians(target.getAlpha());
        onUpdateControlKnob(FloatMath.cos(angleRad) * 0.5f, FloatMath.sin(angleRad) * 0.5f);
        listener.setSprite(target);
    }

    public interface IKnobRotationControlListener extends IOnScreenControlListener {
        public void onControlClick(final KnobRotationControl pKnobRotationControl);
    }
}
