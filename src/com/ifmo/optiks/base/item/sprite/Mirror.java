package com.ifmo.optiks.base.item.sprite;

import com.ifmo.optiks.base.gson.BaseObjectJsonContainer;
import com.ifmo.optiks.base.gson.MirrorJsonContainer;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 27.03.12
 */

public class Mirror extends GameSprite {

    public final boolean canMove;
    public final boolean canRotate;

    public Mirror(final MirrorJsonContainer mjc, final TiledTextureRegion tiledTextureRegion, final BodyForm bodyForm) {
        super(mjc, tiledTextureRegion, bodyForm);
        canMove = mjc.canMove;
        canRotate = mjc.canRotate;
    }

    public BaseObjectJsonContainer getGsonContainer() {
        return new MirrorJsonContainer(this, canMove, canRotate, bodyForm);
    }

    public ObjectType getType() {
        return ObjectType.MIRROR;
    }
}
