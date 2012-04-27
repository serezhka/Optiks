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
        super(ojc.pX, ojc.pY, tiledTextureRegion);
        this.bodyForm = bodyForm;
        scaleSprite(ojc.width, ojc.height, tiledTextureRegion.getTileWidth(), tiledTextureRegion.getTileHeight());
//        animate(200);
    }

    public BaseObjectJsonContainer getGsonContainer() {
        return new BaseObjectJsonContainer(this, bodyForm);
    }

    private void scaleSprite(final float width, final float height, final float currentWidth, final float currentHeight) {
        final float scaleX = width / currentWidth;
        final float scaleY = height / currentHeight;
        setScale(scaleX, scaleY);
    }

    public abstract ObjectType getType();
}
