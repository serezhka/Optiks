package com.ifmo.optiks.base.item.line;

import org.anddev.andengine.entity.primitive.Line;

/**
 * User: dududko@gmail.com
 * Date: 07.04.12
 */

public class LaserSight extends Line {

    public LaserSight(final float pX1, final float pY1, final float pX2, final float pY2) {
        super(pX1, pY1, pX2, pY2);
        super.setColor(1, 0, 0);
    }

    @Override
    public void setRotation(final float pRotation) {
        super.setRotationCenter(getX1(), getY1());
        super.setRotation(pRotation);
    }
}
