package com.ifmo.optiks.base.control;

/**
 * User: dududko@gmail.com
 * Date: 13.04.12
 */

public class ActionMoveFilter {

    private final float x;
    private final float y;

    @SuppressWarnings("FieldCanBeLocal")
    private final float maxDx = 10;

    private boolean wasMove = false;
    private boolean wasTimer = false;

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

    public boolean wasMove() {
        return wasMove;
    }

    public boolean wasTimer() {
        return wasTimer;
    }
}
