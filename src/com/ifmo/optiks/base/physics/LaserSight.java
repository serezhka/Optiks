package com.ifmo.optiks.base.physics;

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
        super.setRotationCenter(super.getX1(), super.getY1());
        super.setRotation(pRotation);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
