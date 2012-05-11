package com.ifmo.optiks.base.physics.joints;

import android.util.Log;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.ifmo.optiks.base.item.sprite.Mirror;
import org.anddev.andengine.entity.shape.IShape;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.extension.physics.box2d.util.Vector2Pool;
import org.anddev.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.anddev.andengine.input.touch.TouchEvent;

/**
 * Author: Aleksey Vladiev (Avladiev2@gmail.com)
 */
public class JointsManager {

    private final static String TAG = "JointsManagerTAG";

    private final static float MASS = 10000;
    private final PhysicsWorld physicsWorld;
    private final Body groundBody;
    private MouseJoint mouseJoint;
    private Joint rotateJoint;
    private boolean isRotates;
    private boolean isCreate;
    private Body body;
    public float dx;
    public float dy;

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
        body.setType(BodyDef.BodyType.DynamicBody);
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
            final Vector2 localPoint = Vector2Pool.obtain(touchAreaX /
                    PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, touchAreaY / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);

            createMouseJoint(localPoint, body);
            if (((Mirror) object).canRotate) {
                final float density = MASS / (object.getHeightScaled() * object.getWidthScaled());
                createRotateJoint(body, density);
            } else {
                body.setType(BodyDef.BodyType.StaticBody);
            }

            this.body = body;
            return true;
        }
        return false;
    }

    private void createMouseJoint(final Vector2 localPoint, final Body body) {
        body.setType(BodyDef.BodyType.DynamicBody);
        groundBody.setTransform(localPoint, 0);
        final MouseJointDef mouseJointDef = new MouseJointDef();
        mouseJointDef.bodyA = groundBody;
        mouseJointDef.bodyB = body;
        mouseJointDef.dampingRatio = 10f;
        mouseJointDef.frequencyHz = 30;
        mouseJointDef.maxForce = (100.0f * body.getMass());
        mouseJointDef.collideConnected = true;
        mouseJointDef.target.set(localPoint);
        Vector2Pool.recycle(localPoint);
        mouseJoint = (MouseJoint) physicsWorld.createJoint(mouseJointDef);
    }

    private void createRotateJoint(final Body body, final float density) {
        body.getFixtureList().get(0).setDensity(density);       //todo
        body.resetMassData();
        Log.d(TAG, "mass = " + body.getMass());

        final RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
        final Body anchorBody = physicsWorld.createBody(new BodyDef());
        anchorBody.setType(BodyDef.BodyType.StaticBody);
        anchorBody.setTransform(body.getWorldCenter(), 0);
        revoluteJointDef.initialize(anchorBody, body, anchorBody.getWorldCenter());
        rotateJoint = physicsWorld.createJoint(revoluteJointDef);
        isRotates = true;
    }
}
