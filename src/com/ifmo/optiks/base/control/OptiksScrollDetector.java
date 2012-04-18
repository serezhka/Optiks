package com.ifmo.optiks.base.control;

import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.input.touch.detector.BaseDetector;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 18.04.12
 */

public class OptiksScrollDetector extends BaseDetector {

    private static final float TRIGGER_SCROLL_MINIMUM_DISTANCE_DEFAULT = 10;

    private float mTriggerScrollMinimumDistance;
    private final IScrollDetectorListener mScrollDetectorListener;

    private int mPointerID = -1;

    private boolean mTriggering;

    private float mLastX;
    private float mLastY;

    public OptiksScrollDetector(final IScrollDetectorListener pScrollDetectorListener) {
        this(OptiksScrollDetector.TRIGGER_SCROLL_MINIMUM_DISTANCE_DEFAULT, pScrollDetectorListener);
    }

    public OptiksScrollDetector(final float pTriggerScrollMinimumDistance, final IScrollDetectorListener pScrollDetectorListener) {
        this.mTriggerScrollMinimumDistance = pTriggerScrollMinimumDistance;
        this.mScrollDetectorListener = pScrollDetectorListener;
    }

    public float getTriggerScrollMinimumDistance() {
        return this.mTriggerScrollMinimumDistance;
    }

    public void setTriggerScrollMinimumDistance(final float pTriggerScrollMinimumDistance) {
        this.mTriggerScrollMinimumDistance = pTriggerScrollMinimumDistance;
    }

    @Override
    public boolean onManagedTouchEvent(final TouchEvent pSceneTouchEvent) {
        final float touchX = this.getX(pSceneTouchEvent);
        final float touchY = this.getY(pSceneTouchEvent);

        switch(pSceneTouchEvent.getAction()) {
            case TouchEvent.ACTION_DOWN:
                this.prepareScroll(pSceneTouchEvent.getPointerID(), touchX, touchY);
                return true;
            case TouchEvent.ACTION_MOVE:
                if(this.mPointerID == -1) {
                    this.prepareScroll(pSceneTouchEvent.getPointerID(), touchX, touchY);
                    return true;
                } else if(this.mPointerID == pSceneTouchEvent.getPointerID()) {
                    final float distanceX = touchX - this.mLastX;
                    final float distanceY = touchY - this.mLastY;

                    final float triggerScrollMinimumDistance = this.mTriggerScrollMinimumDistance;
                    if(this.mTriggering || Math.abs(distanceX) > triggerScrollMinimumDistance || Math.abs(distanceY) > triggerScrollMinimumDistance) {
                        if(!this.mTriggering) {
                            this.triggerOnScrollStarted(distanceX, distanceY);
                        } else {
                            this.triggerOnScroll(distanceX, distanceY);
                        }

                        this.mLastX = touchX;
                        this.mLastY = touchY;
                        this.mTriggering = true;
                    }
                    return true;
                } else {
                    return false;
                }
            case TouchEvent.ACTION_UP:
            case TouchEvent.ACTION_CANCEL:
                if(this.mPointerID == pSceneTouchEvent.getPointerID()) {
                    final float distanceX = touchX - this.mLastX;
                    final float distanceY = touchY - this.mLastY;

                    if(this.mTriggering) {
                        this.triggerOnScrollFinished(distanceX, distanceY);
                    }

                    this.mPointerID = -1;
                }
                return true;
            default:
                return false;
        }
    }

    private void prepareScroll(final int pPointerID, final float pTouchX, final float pTouchY) {
        this.mLastX = pTouchX;
        this.mLastY = pTouchY;
        this.mTriggering = false;
        this.mPointerID = pPointerID;
    }

    private void triggerOnScrollStarted(final float pDistanceX, final float pDistanceY) {
        if(this.mPointerID != -1) {
            this.mScrollDetectorListener.onScrollStarted(this, this.mPointerID, pDistanceX, pDistanceY);
        }
    }

    private void triggerOnScroll(final float pDistanceX, final float pDistanceY) {
        if(this.mPointerID != -1) {
            this.mScrollDetectorListener.onScroll(this, this.mPointerID, pDistanceX, pDistanceY);
        }
    }

    private void triggerOnScrollFinished(final float pDistanceX, final float pDistanceY) {
        this.mTriggering = false;

        if(this.mPointerID != -1) {
            this.mScrollDetectorListener.onScrollFinished(this, this.mPointerID, pDistanceX, pDistanceY);
        }
    }

    protected float getX(final TouchEvent pTouchEvent) {
        return pTouchEvent.getX();
    }

    protected float getY(final TouchEvent pTouchEvent) {
        return pTouchEvent.getY();
    }

    public static interface IScrollDetectorListener {
        public void onScrollStarted(final OptiksScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY);
        public void onScroll(final OptiksScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY);
        public void onScrollFinished(final OptiksScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY);
    }
}
