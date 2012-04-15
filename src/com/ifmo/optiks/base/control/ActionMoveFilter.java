package com.ifmo.optiks.base.control;

/**
 * User: dududko@gmail.com
 * Date: 13.04.12
 */
public class ActionMoveFilter {
    public final float x;
    public final float y;
    public final float maxDx = 10;
    public boolean wasMove = false;
    public boolean wasTimer = false;

    public ActionMoveFilter(final float x, final float y) {
        this.x = x;
        this.y = y;
    }

    public boolean notMoving(final float x, final float y) {
        if (wasTimer) {
            return wasTimer;
        }
        if (!wasMove) {
            if (Math.sqrt(Math.pow((this.x - x), 2) + Math.pow((this.y - y), 2)) < maxDx) {
                return true;
            } else {
                wasMove = true;
            }
        }
        return false;
    }

    public void setByTimer() {
        wasTimer = true;
    }
}
