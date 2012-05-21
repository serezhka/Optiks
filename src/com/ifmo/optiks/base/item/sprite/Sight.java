package com.ifmo.optiks.base.item.sprite;

import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

/**
 * Author: Dudko Alex (dududko@gmail.com)
 * Date: 21.05.12
 */
public class Sight extends AnimatedSprite {
    public final AnimatedSprite sightChild;
    private final SightLine[] sightLine = new SightLine[50];

    public Sight(final float pX, final float pY, final float pTileWidth, final float pTileHeight,
                 final TiledTextureRegion pTiledTextureRegion, final TiledTextureRegion emptyTexture) {
        super(pX, pY, pTileWidth, pTileHeight, pTiledTextureRegion);
        sightChild = new AnimatedSprite(0, 0, pTileWidth * 2, pTileHeight * 2, emptyTexture);
        attachChild(sightChild);
        sightChild.setPosition(-sightChild.getWidth() / 4, -sightChild.getHeight() / 4);
    }

    public void addSightLine(final TiledTextureRegion sightCircle, final float laserX, final float laserY) {
        sightLine[0] = new SightLine(-(mX + laserX) / 2, -(mY - laserY) / 2, sightCircle);
        attachChild(sightLine[0]);
//        sightLine[0].setPosition(-(mX + laserX) / 2, -(mY - laserY) / 2);
    }

    @Override
    public void setPosition(float pX, float pY) {
        super.setPosition(pX, pY);

    }

    private class SightLine extends AnimatedSprite {
        public SightLine(final float pX, final float pY, final TiledTextureRegion pTiledTextureRegion) {
            super(pX, pY, pTiledTextureRegion);
        }

        protected void onManagedUpdate(final float pSecondsElapsed){
                 //todo
        }
    }
}
