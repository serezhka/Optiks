package com.ifmo.optiks.base.gson;

import com.badlogic.gdx.physics.box2d.Body;
import com.ifmo.optiks.base.item.sprite.BodyForm;
import com.ifmo.optiks.base.item.sprite.GameSprite;
import com.ifmo.optiks.base.item.sprite.ObjectType;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * User: dududko@gmail.com
 * Date: 17.04.12
 */

public class MirrorJsonContainer extends BaseObjectJsonContainer {

    final public boolean canMove;
    final public boolean canRotate;

    public MirrorJsonContainer(final GameSprite gs, final boolean canMove, final boolean canRotate, final BodyForm bodyForm) {
        super((Body)gs.getUserData());
        this.canMove = canMove;
        this.canRotate = canRotate;
    }

    public MirrorJsonContainer(final float pX, final float pY, final float width, final float height, final float rotation, final ObjectType type, final BodyForm bodyForm, final boolean canMove, final boolean canRotate) {
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
