package com.ifmo.optiks.base.item.sprite;

import com.badlogic.gdx.physics.box2d.Body;
import com.ifmo.optiks.base.gson.BaseObjectJsonContainer;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 27.03.12
 */

public abstract class GameSprite extends AnimatedSprite {

   public  final BodyForm bodyForm;

    public GameSprite(final BaseObjectJsonContainer ojc, final TiledTextureRegion tiledTextureRegion, final BodyForm bodyForm) {
        super(0, 0, ojc.width, ojc.height, tiledTextureRegion);
        this.bodyForm = bodyForm;
    }

    public BaseObjectJsonContainer getGsonContainer() {
        return new BaseObjectJsonContainer((Body)getUserData());
    }

    public abstract ObjectType getType();

}
