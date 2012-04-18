package com.ifmo.optiks.base.control;

import org.anddev.andengine.input.touch.TouchEvent;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 18.04.12
 */

public class OptiksSurfaceScrollDetector extends OptiksScrollDetector {

    public OptiksSurfaceScrollDetector(final float pTriggerScrollMinimumDistance, final IScrollDetectorListener pScrollDetectorListener) {
        super(pTriggerScrollMinimumDistance, pScrollDetectorListener);
    }

    public OptiksSurfaceScrollDetector(final IScrollDetectorListener pScrollDetectorListener) {
        super(pScrollDetectorListener);
    }

    @Override
    protected float getX(final TouchEvent pTouchEvent) {
        return pTouchEvent.getMotionEvent().getX();
    }

    @Override
    protected float getY(final TouchEvent pTouchEvent) {
        return pTouchEvent.getMotionEvent().getY();
    }
}
