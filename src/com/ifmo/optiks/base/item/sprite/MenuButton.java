package com.ifmo.optiks.base.item.sprite;

import com.ifmo.optiks.base.gson.BaseObjectJsonContainer;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

/**
 * Author: Dudko Alex (dududko@gmail.com)
 * Date: 26.04.12
 */
public class MenuButton extends GameSprite{

    public MenuButton(final BaseObjectJsonContainer ojc, final TiledTextureRegion tiledTextureRegion, final BodyForm bodyForm) {
        super(ojc, tiledTextureRegion, bodyForm);
    }

    public ObjectType getType() {
        return ObjectType.MENU_BUTTON;
    }
}
