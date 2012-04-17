package com.ifmo.optiks.base.primitive_game_scene_items.sprite;

import com.ifmo.optiks.base.gson.BaseObjectJsonContainer;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 27.03.12
 */

public class Barrier extends GameSprite {

    public Barrier(BaseObjectJsonContainer ojc, TextureRegion pTextureRegion, BodyForm bodyForm) {
        super(ojc, pTextureRegion, bodyForm);
    }

    public ObjectType getType() {
        return ObjectType.BARRIER;
    }
}
