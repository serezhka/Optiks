package com.ifmo.optiks.base.physics;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;

/**
 * User: dududko@gmail.com
 * Date: 10.04.12
 */

// coppy to physycs
public class RevoluteJointOptiks extends RevoluteJointDef {
    private Body body;
    private Joint joint;

    public RevoluteJointOptiks() {
        super();
    }

    public void initialize(final Body body, final PhysicsWorld physicsWorld) {
        this.body = body;
        final Body anchorBody = physicsWorld.createBody(new BodyDef());
        anchorBody.setType(BodyDef.BodyType.StaticBody);
        anchorBody.setTransform(body.getWorldCenter(), 0);

        super.initialize(anchorBody, body, anchorBody.getWorldCenter());
        joint = physicsWorld.createJoint(this);
    }

    public void destroyJoint(final PhysicsWorld physicsWorld) {
        physicsWorld.destroyJoint(joint);
    }
}
