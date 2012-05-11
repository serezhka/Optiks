package com.ifmo.optiks.base.physics.joints;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import org.anddev.andengine.entity.shape.IShape;
import org.anddev.andengine.extension.physics.box2d.util.Vector2Pool;
import org.anddev.andengine.extension.physics.box2d.util.constants.PhysicsConstants;

/**
 * User: dududko@gmail.com
 * Date: 29.03.12
 */

@Deprecated
public abstract class MouseJointOptiks extends MouseJointDef {

    private final Body body = super.bodyB;

    public MouseJointOptiks(final IShape object, final Body groundBody, final float touchAreaLocalX, final float touchAreaLocalY) {
        super();

        final Body body = (Body) object.getUserData();
        body.setType(BodyDef.BodyType.DynamicBody);
        final Vector2 localPoint = Vector2Pool.obtain((touchAreaLocalX - object.getWidth() * 0.5f) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (touchAreaLocalY - object.getHeight() * 0.5f) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
        groundBody.setTransform(localPoint, 0);

        super.bodyA = groundBody;
        super.bodyB = body;
        super.dampingRatio = 10f;
        super.frequencyHz = 30;
        super.maxForce = (100.0f * body.getMass());
        super.collideConnected = true;

        super.target.set(body.getWorldPoint(localPoint));
        Vector2Pool.recycle(localPoint);
    }

    public Body getBody() {
        return body;
    }

    public abstract MouseJoint getMouseJoint();
}
