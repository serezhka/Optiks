package com.ifmo.optiks.base.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.ifmo.optiks.base.item.line.LaserBeam;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.extension.physics.box2d.util.Vector2Pool;
import org.anddev.andengine.extension.physics.box2d.util.constants.PhysicsConstants;

/**
 * User: dududko@gmail.com
 * Date: 29.03.12
 */

public class LaserBullet implements ContactListener {

    final static float INF = 1000000000;

    private final Body body;
    private final Body laserBody;
    private final AnimatedSprite sight;
    private final LaserBeam laserBeam;
    private final CollisionHandler collisionHandler;
    private final float sightXBegin;
    private final float sightYBegin;

    private boolean isMoving = false;

    public LaserBullet(final Body bullet, final Body laser,
                       final AnimatedSprite sight, final LaserBeam laserBeam, final CollisionHandler collisionHandler) {
        this.laserBody = laser;
        body = bullet;
        this.sight = sight;
        sightXBegin = laser.getPosition().x;
        sightYBegin = laser.getPosition().y;

        this.laserBeam = laserBeam;
        this.collisionHandler = collisionHandler;
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
        isMoving = false;
        body.setLinearVelocity(0, 0);
    }


    public void cleanScreen() {
        laserBeam.resetBeam();
    }

    public void shoot() {
        laserBeam.resetBeam();
        isMoving = true;
        final float xTo = sight.getX() + sight.getWidth() / 2;
        final float yTo = sight.getY() + sight.getHeight() / 2;
        body.setTransform(laserBody.getPosition(), 0);
        final Vector2 pos = body.getPosition();
        final Vector2 vec = Vector2Pool.obtain(INF * (xTo / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT - pos.x), INF *
                (yTo / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT - pos.y));
        body.setLinearVelocity(vec);
    }

    @Override
    public void beginContact(final Contact contact) {
        if (contact.getFixtureB().getBody() == body) {
            final Body bodyA = contact.getFixtureA().getBody();
            collisionHandler.handle(contact, this, bodyA);
        }
    }

    @Override
    public void endContact(final Contact contact) {
    }

    @Override
    public void preSolve(final Contact contact, final Manifold manifold) {
    }

    @Override
    public void postSolve(final Contact contact, final ContactImpulse contactImpulse) {
    }

    public LaserBeam getLaserBeam() {
        return laserBeam;
    }

    public void AddLineToLaserBeam(final float x, final float y) {
        laserBeam.addLine(x, y);

    }

    public boolean isMoving() {
        return isMoving;
    }
}
