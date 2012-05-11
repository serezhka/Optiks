package com.ifmo.optiks.base.item.sprite;

import com.ifmo.optiks.base.gson.BaseObjectJsonContainer;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

/**
 * Author: Dudko Alex (dududko@gmail.com)
 * Date: 10.05.12
 */
public class AntiMirrorWall extends GameSprite{

    public AntiMirrorWall(final BaseObjectJsonContainer ojc, final TiledTextureRegion tiledTextureRegion, final BodyForm bodyForm) {
        super(ojc, tiledTextureRegion, bodyForm);
    }

    public ObjectType getType() {
        return ObjectType.ANTI_MIRROR_WALL;
    }
}
