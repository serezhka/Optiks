package com.ifmo.optiks.base.physics.joints;

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
        this.body.getFixtureList().get(0).setDensity(10f);
        this.body.resetMassData();
        final Body anchorBody = physicsWorld.createBody(new BodyDef());
        anchorBody.setType(BodyDef.BodyType.StaticBody);
        anchorBody.setTransform(body.getWorldCenter(), 0);
        super.initialize(anchorBody, body, anchorBody.getWorldCenter());
        joint = physicsWorld.createJoint(this);
    }

    public void destroyJoint(final PhysicsWorld physicsWorld) {
        this.body.getFixtureList().get(0).setDensity(0f);
        this.body.resetMassData();
        physicsWorld.destroyJoint(joint);
    }
}
