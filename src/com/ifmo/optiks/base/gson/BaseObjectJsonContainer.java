package com.ifmo.optiks.base.gson;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.ifmo.optiks.base.item.sprite.BodyForm;
import com.ifmo.optiks.base.item.sprite.GameSprite;
import com.ifmo.optiks.base.item.sprite.ObjectType;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.util.MathUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Author: Aleksey Vladiev (Avladiev2@gmail.com)
 */

public class BaseObjectJsonContainer {

    public final float pX;
    public final float pY;
    public final float width;
    public final float height;
    public final float rotation;
    public final ObjectType type;
    public final BodyForm bodyForm;

    public BaseObjectJsonContainer(final Body body) {
        final Vector2 v = body.getPosition();
        this.pX = v.x * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT;
        this.pY = v.y * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT;
        final GameSprite gs = (GameSprite) body.getUserData() ;
        this.width = gs.getWidth();
        this.height = gs.getHeight();
        rotation =  MathUtils.radToDeg( body.getAngle());
        this.type = gs.getType();
        this.bodyForm = gs.bodyForm;
    }

    public BaseObjectJsonContainer(final float pX, final float pY, final float width,
                                   final float height, final float rotation,
                                   final ObjectType type, final BodyForm bodyForm) {
        this.pX = pX;
        this.pY = pY;
        this.width = width;
        this.height = height;
        this.rotation = rotation;
        this.type = type;
        this.bodyForm = bodyForm;
    }

    public BaseObjectJsonContainer(final JSONObject jsonObject) throws JSONException {
        this.pX = (float) jsonObject.getDouble(Constants.P_X);
        this.pY = (float) jsonObject.getDouble(Constants.P_Y);
        this.width = (float) jsonObject.getDouble(Constants.WIDTH);
        this.height = (float) jsonObject.getDouble(Constants.HEIGHT);
        this.rotation = (float) jsonObject.getDouble(Constants.ROTATION);
        this.type = ObjectType.getType(jsonObject.getString(Constants.TYPE));
        this.bodyForm = BodyForm.getType(jsonObject.getString(Constants.BODY_FORM));
    }
}
