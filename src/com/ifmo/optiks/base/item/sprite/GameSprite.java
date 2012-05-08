package com.ifmo.optiks.base.item.sprite;

import com.ifmo.optiks.base.gson.BaseObjectJsonContainer;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 27.03.12
 */

public abstract class GameSprite extends AnimatedSprite {

    protected final BodyForm bodyForm;

    public GameSprite(final BaseObjectJsonContainer ojc, final TiledTextureRegion tiledTextureRegion, final BodyForm bodyForm) {
        super(0, 0, ojc.width, ojc.height, tiledTextureRegion);
        this.bodyForm = bodyForm;
    }

    public BaseObjectJsonContainer getGsonContainer() {
        return new BaseObjectJsonContainer(this, bodyForm);
    }

    public abstract ObjectType getType();
}
