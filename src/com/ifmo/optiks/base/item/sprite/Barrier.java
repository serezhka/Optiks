package com.ifmo.optiks.base.item.sprite;

import com.ifmo.optiks.base.gson.BaseObjectJsonContainer;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 27.03.12
 */

public class Barrier extends GameSprite {

    public Barrier(final BaseObjectJsonContainer ojc, final TiledTextureRegion tiledTextureRegion, final BodyForm bodyForm) {
        super(ojc, tiledTextureRegion, bodyForm);
    }

    public ObjectType getType() {
        return ObjectType.BARRIER;
    }
}
