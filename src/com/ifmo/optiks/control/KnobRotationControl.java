package com.ifmo.optiks.control;

import android.util.FloatMath;
import com.badlogic.gdx.physics.box2d.Body;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
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
        onUpdateControlKnob(pX, pY);
        setVisible(false);
        //super.unregisterTouchArea(super.getControlBase());
        //super.registerTouchArea(super.getControlKnob());
    }

    public KnobRotationControl(final float pX, final float pY, final Camera pCamera, final TextureRegion pControlBaseTextureRegion, final TextureRegion pControlKnobTextureRegion, final float pTimeBetweenUpdates, final IKnobRotationControlListener pAnalogOnScreenControlListener) {
        super(pX, pY, pCamera, pControlBaseTextureRegion, pControlKnobTextureRegion, pTimeBetweenUpdates, pAnalogOnScreenControlListener);
        this.mClickDetector.setEnabled(true);
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
        if (pRelativeX == 0 && pRelativeY == 0) {
            return;
        }
        if (pRelativeX * pRelativeX + pRelativeY * pRelativeY == 0.20f) {
            super.onUpdateControlKnob(pRelativeX, pRelativeY);
        } else {
            final float angleRad = MathUtils.atan2(pRelativeY, pRelativeX);
            super.onUpdateControlKnob(FloatMath.cos(angleRad) * 0.35f, FloatMath.sin(angleRad) * 0.35f);
        }
    }

    @Override
    public void onClick(final ClickDetector clickDetector, final TouchEvent touchEvent) {
        /* Do nothing*/
    }

    @Override
    protected void onHandleControlKnobReleased() {
        /* Do nothing*/
    }

    public Body getTarget() {
        return listener.getBody();
    }

    public void setTarget(final Body target) {
        if (target != null) {
            listener.setBody(target);
            final float x = target.getPosition().x * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT - getControlBase().getWidth() / 2;
            final float y = target.getPosition().y * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT - getControlBase().getHeight() / 2;
            setPosition(x, y);
            final float angleRad = target.getAngle() - (float) Math.PI / 2;
            onUpdateControlKnob(FloatMath.cos(angleRad), FloatMath.sin(angleRad));
            listener.setBody(target);
        } else {
            listener.setBody(null);
        }
    }

    public interface IKnobRotationControlListener extends IOnScreenControlListener {
        public void onControlClick(final KnobRotationControl pKnobRotationControl);
    }

    @Override
    public void setPosition(final float x, final float y) {
        super.getControlBase().setPosition(x, y);
        super.getControlKnob().setPosition(x, y);
    }

    @Override
    public boolean isVisible() {
        return super.getControlKnob().isVisible();
    }

    @Override
    public void setVisible(final boolean pVisible) {
        super.getControlBase().setVisible(false);
        super.getControlKnob().setVisible(pVisible);
    }
}
