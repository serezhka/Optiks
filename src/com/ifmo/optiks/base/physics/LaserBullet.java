package com.ifmo.optiks.base.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.extension.physics.box2d.util.Vector2Pool;

/**
 * User: dududko@gmail.com
 * Date: 29.03.12
 */

public class LaserBullet {

    Body body;

    public LaserBullet(final PhysicsWorld physicsWorld, final float x, final float y) {
        body = PhysicsFactory.createCircleBody(physicsWorld, x, y, 1, 0, BodyDef.BodyType.DynamicBody, new Fixtures().getBullet());
    }

    public Body getBody() {
        return body;
    }

    public Vector2 getPosition() {
        return body.getPosition();
    }

    public void setTransform(final Vector2 vector2) {
        body.setTransform(Vector2Pool.obtain(vector2), 0);
    }

    public void setLinearVelocity(final Vector2 vector2) {
        body.setLinearVelocity(Vector2Pool.obtain(vector2));
    }

    public void stop() {
        body.setLinearVelocity(0, 0);
    }
}
