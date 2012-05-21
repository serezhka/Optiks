package com.ifmo.optiks.base.item.sprite;

import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

/**
 * Author: Dudko Alex (dududko@gmail.com)
 * Date: 21.05.12
 */
public class Sight extends AnimatedSprite {
    public final AnimatedSprite sightChild;
    private AnimatedSprite[] sightLine;

    public Sight(final float pX, final float pY, final float pTileWidth, final float pTileHeight, final TiledTextureRegion pTiledTextureRegion, final TiledTextureRegion emptyTexture) {
        super(pX, pY, pTileWidth, pTileHeight, pTiledTextureRegion);
        sightChild = new AnimatedSprite(0, 0, pTileWidth * 2, pTileHeight * 2, emptyTexture);
        attachChild(sightChild);
        sightChild.setPosition(-sightChild.getWidth() / 4, -sightChild.getHeight() / 4);

    }
}
