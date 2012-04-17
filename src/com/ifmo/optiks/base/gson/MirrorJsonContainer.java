package com.ifmo.optiks.base.gson;

import com.ifmo.optiks.base.primitive_game_scene_items.sprite.BodyForm;
import com.ifmo.optiks.base.primitive_game_scene_items.sprite.GameSprite;
import com.ifmo.optiks.base.primitive_game_scene_items.sprite.ObjectType;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * User: dududko@gmail.com
 * Date: 17.04.12
 */
public class MirrorJsonContainer extends BaseObjectJsonContainer {
    final public boolean canMove;
    final public boolean canRotate;

    public MirrorJsonContainer(final GameSprite gs, final boolean canMove, final boolean canRotate,final BodyForm bodyForm) {
        super(gs,bodyForm);
        this.canMove = canMove;
        this.canRotate = canRotate;
    }

    public MirrorJsonContainer(float pX, float pY, float width, final float height, float rotation, ObjectType type, BodyForm bodyForm, boolean canMove, boolean canRotate) {
        super(pX, pY, width, height, rotation, type, bodyForm);
        this.canMove = canMove;
        this.canRotate = canRotate;
    }

    public MirrorJsonContainer(final JSONObject jsonObject) throws JSONException {
        super(jsonObject);
        this.canMove = jsonObject.getBoolean(Constants.CAN_MOVE);
        this.canRotate = jsonObject.getBoolean(Constants.CAN_ROTATE);
    }



}
