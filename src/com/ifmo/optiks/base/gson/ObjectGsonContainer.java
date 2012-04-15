package com.ifmo.optiks.base.gson;

import com.ifmo.optiks.base.sprite.GameSprite;
import com.ifmo.optiks.base.sprite.ObjectType;

/**
 * Author: Aleksey Vladiev (Avladiev2@gmail.com)
 */

public class ObjectGsonContainer {

    public final float pX;
    public final float pY;
    public final float width;
    public final float height;
    public final float rotation;
    public final ObjectType type;

    public ObjectGsonContainer(final GameSprite gs) {
        this.pX = gs.getX();
        this.pY = gs.getY();
        this.width = gs.getWidth();
        this.height = gs.getHeight();
        rotation = gs.getRotation();
        this.type = gs.getType();

    }

    public ObjectGsonContainer(final float pX, final float pY, final float width, final float height, final float rotation, final ObjectType type) {
        this.pX = pX;
        this.pY = pY;
        this.width = width;
        this.height = height;
        this.rotation = rotation;
        this.type = type;
    }
}
