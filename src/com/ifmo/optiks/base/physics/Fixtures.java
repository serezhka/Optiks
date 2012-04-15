package com.ifmo.optiks.base.physics;

import com.badlogic.gdx.physics.box2d.FixtureDef;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;

/**
 * User: dududko@gmail.com
 * Date: 29.03.12
 */

public class Fixtures {

    private static final short WALL_CAT = 1;
    private static final short LASER_CAT = 2;
    private static final short AIM_MIRROR_BARRIER_CAT = 4;
    private static final short BULLET_CAT = 8;

    public static final FixtureDef WALL = PhysicsFactory.createFixtureDef(0, 0f, 0f, false, WALL_CAT, (short) (WALL_CAT + AIM_MIRROR_BARRIER_CAT + BULLET_CAT + LASER_CAT), (short) 0);
    public static final FixtureDef LASER = PhysicsFactory.createFixtureDef(0, 0f, 0f, false, LASER_CAT, (short) (WALL_CAT + AIM_MIRROR_BARRIER_CAT + LASER_CAT), (short) 0);
    public static final FixtureDef AIM_MIRROR_BARRIER = PhysicsFactory.createFixtureDef(10, 0f, 0f, false, AIM_MIRROR_BARRIER_CAT, (short) (WALL_CAT + AIM_MIRROR_BARRIER_CAT + BULLET_CAT + LASER_CAT), (short) 0);
    public static final FixtureDef BULLET = PhysicsFactory.createFixtureDef(10000000, 1f, 0f, false, BULLET_CAT, (short) (WALL_CAT + AIM_MIRROR_BARRIER_CAT + BULLET_CAT), (short) 0);

    /*public FixtureDef getWall() {
        return wall;
    }

    public FixtureDef getLaser() {
        return laser;
    }

    public FixtureDef getAimMirrorBarrier() {
        return aimMirrorBarrier;
    }

    public FixtureDef getBullet() {
        return bullet;
    }*/
}
