package com.ifmo.optiks.base.gson;

import com.ifmo.optiks.base.primitive_game_scene_items.sprite.BodyForm;
import com.ifmo.optiks.base.primitive_game_scene_items.sprite.GameSprite;
import com.ifmo.optiks.base.primitive_game_scene_items.sprite.ObjectType;
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
    public final BodyForm   bodyForm;

    public BaseObjectJsonContainer(final GameSprite gs,final BodyForm bodyForm) {
        this.pX = gs.getX();
        this.pY = gs.getY();
        this.width = gs.getWidth();
        this.height = gs.getHeight();
        rotation = gs.getRotation();
        this.type = gs.getType();
        this.bodyForm = bodyForm;
    }


    public BaseObjectJsonContainer(final float pX, final float pY, final float width,
                                   final float height, final float rotation,
                                   final ObjectType type,final BodyForm bodyForm) {
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
