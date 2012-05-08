package com.ifmo.optiks.base.control;

/**
 * User: dududko@gmail.com
 * Date: 13.04.12
 */

public class ActionMoveFilter {

    private final static float MAX_DX = 10;
    private final static int MIN_TIME = 200;

    private float x;
    private float y;
    private long timeInit;
    private boolean wasMove;
    private boolean isCrossBorder;


    public ActionMoveFilter() {
        wasMove = true;
    }

    public void init(final float beginX, final float beginY) {
        this.x = beginX;
        this.y = beginY;
        timeInit = System.currentTimeMillis();
        wasMove = false;
        isCrossBorder = false;
    }

    public void destroy() {
        wasMove = true;
    }

    public boolean isMove() {
        return wasMove;   // было ли перемещение
    }

    public boolean isWaiting(final float x, final float y) {
        return isLocality(x, y) && !isTimer();
    }


    public boolean isDestroyRotate(final float x, final float y) {
        if (!wasMove) {
            if (isLocality(x, y) && isTimer()) {
                wasMove = true;
                return true;
            }
            return false;
        }
        return true;
    }

    private boolean isTimer() {
        return System.currentTimeMillis() - timeInit >= MIN_TIME;
    }

    private boolean isLocality(final float x, final float y) {
        if (!isCrossBorder) {
            isCrossBorder = Math.sqrt(Math.pow((this.x - x), 2) + Math.pow((this.y - y), 2)) > MAX_DX;
        }
        return !isCrossBorder;
    }
}
