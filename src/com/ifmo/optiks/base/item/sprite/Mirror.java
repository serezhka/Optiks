package com.ifmo.optiks.base.item.sprite;

import com.ifmo.optiks.base.gson.BaseObjectJsonContainer;
import com.ifmo.optiks.base.gson.MirrorJsonContainer;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 27.03.12
 */

public class Mirror extends GameSprite {

    public final boolean canMove;
    public final boolean canRotate;

    public Mirror(final MirrorJsonContainer mjc, final TextureRegion pTextureRegion, final BodyForm bodyForm
    ) {
        super(mjc, pTextureRegion, bodyForm);
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
