package com.ifmo.optiks.base.physics;

import com.badlogic.gdx.physics.box2d.FixtureDef;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;

/**
 * User: dududko@gmail.com
 * Date: 29.03.12
 */

public class Fixtures {

    private static final short WALL = 1;
    private static final short LASER = 2;
    private static final short AIM_MIRROR_BARRIER = 4;
    private static final short BULLET = 8;

    private final FixtureDef wall = PhysicsFactory.createFixtureDef(0, 0f, 0f, false, WALL, (short) (WALL + AIM_MIRROR_BARRIER + BULLET + LASER), (short) 0);
    private final FixtureDef laser = PhysicsFactory.createFixtureDef(0, 0f, 0f, false, LASER, (short) (WALL + AIM_MIRROR_BARRIER + LASER), (short) 0);
    private final FixtureDef aimMirrorBarrier = PhysicsFactory.createFixtureDef(0, 0f, 0f, false, AIM_MIRROR_BARRIER, (short) (WALL + AIM_MIRROR_BARRIER + BULLET + LASER), (short) 0);
    private final FixtureDef bullet = PhysicsFactory.createFixtureDef(0, 1f, 0f, false, BULLET, (short) (WALL + AIM_MIRROR_BARRIER + BULLET), (short) 0);

    public FixtureDef getWall() {
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
    }
}
