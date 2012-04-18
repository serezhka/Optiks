package com.ifmo.optiks.base.item.sprite;

import com.ifmo.optiks.base.gson.BaseObjectJsonContainer;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 27.03.12
 */

public abstract class GameSprite extends Sprite {

    protected final BodyForm bodyForm;

    public GameSprite(final BaseObjectJsonContainer ojc, final TextureRegion pTextureRegion, final BodyForm bodyForm) {
        super(ojc.pX, ojc.pY, pTextureRegion);
        this.bodyForm = bodyForm;
        scaleSprite(ojc.width, ojc.height);
    }

    public BaseObjectJsonContainer getGsonContainer() {
        return new BaseObjectJsonContainer(this, bodyForm);
    }

    private void scaleSprite(final float width, final float height) {
        final float scaleX = width / getTextureRegion().getWidth();
        final float scaleY = height / getTextureRegion().getHeight();
        setScale(scaleX, scaleY);
    }

    public abstract ObjectType getType();
}
