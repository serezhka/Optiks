package com.ifmo.optiks.base.physics.joints;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import org.anddev.andengine.entity.shape.IShape;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.extension.physics.box2d.util.Vector2Pool;
import org.anddev.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.anddev.andengine.input.touch.TouchEvent;

/**
 * Author: Aleksey Vladiev (Avladiev2@gmail.com)
 */
public class JointsManager {
    private final PhysicsWorld physicsWorld;
    private final Body groundBody;
    private MouseJoint mouseJoint;
    private Joint rotateJoint;
    private boolean isRotates;
    private boolean isCreate;
    private Body body;



    public JointsManager(final PhysicsWorld physicsWorld) {
        this.physicsWorld = physicsWorld;
        groundBody = physicsWorld.createBody(new BodyDef());
    }

    public boolean setTarget(final TouchEvent touchEvent) {
        if (isCreate) {
            final Vector2 vec = Vector2Pool.obtain(touchEvent.getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, touchEvent.getY() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
            mouseJoint.setTarget(vec);
            Vector2Pool.recycle(vec);
            return true;
        }
        return false;
    }

    public boolean isRotates() {
        return isRotates;
    }

    public boolean destroyRotate() {
        if (isRotates) {
            isRotates = false;
            body.getFixtureList().get(0).setDensity(0);
            body.resetMassData();
            physicsWorld.destroyJoint(rotateJoint);
            return true;
        }
        return false;
    }


    public boolean destroyJoints() {
        if (isCreate) {
            isCreate = false;
            destroyRotate();
            physicsWorld.destroyJoint(mouseJoint);
            body.setType(BodyDef.BodyType.StaticBody);
            return true;
        }
        return false;
    }

    public boolean createJoints(final IShape object, final float touchAreaX, final float touchAreaY) {
        if (!isCreate) {
            isCreate = true;
            final Body body = (Body) object.getUserData();
            body.setType(BodyDef.BodyType.DynamicBody);
            final Vector2 localPoint = Vector2Pool.obtain((touchAreaX - object.getWidth() * 0.5f) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (touchAreaY - object.getHeight() * 0.5f) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
            groundBody.setTransform(localPoint, 0);
            final MouseJointDef mouseJointDef = new MouseJointDef();
            mouseJointDef.bodyA = groundBody;
            mouseJointDef.bodyB = body;
            mouseJointDef.dampingRatio = 10f;
            mouseJointDef.frequencyHz = 30;
            mouseJointDef.maxForce = (100.0f * body.getMass());
            mouseJointDef.collideConnected = true;
            mouseJointDef.target.set(body.getWorldPoint(localPoint));
            Vector2Pool.recycle(localPoint);
            mouseJoint = (MouseJoint) physicsWorld.createJoint(mouseJointDef);


            //===
            body.getFixtureList().get(0).setDensity(2);       //todo
            body.resetMassData();
            final RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
            final Body anchorBody = physicsWorld.createBody(new BodyDef());
            anchorBody.setType(BodyDef.BodyType.StaticBody);
            anchorBody.setTransform(body.getWorldCenter(), 0);
            revoluteJointDef.initialize(anchorBody, body, anchorBody.getWorldCenter());
            rotateJoint = physicsWorld.createJoint(revoluteJointDef);
            isRotates = true;
            this.body = body;
            return true;
        }
        return false;
    }
}