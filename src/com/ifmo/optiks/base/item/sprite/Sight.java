package com.ifmo.optiks.base.item.sprite;

import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.util.MathUtils;

import java.util.LinkedList;

/**
 * Author: Dudko Alex (dududko@gmail.com)
 * Date: 21.05.12
 */
public class Sight extends AnimatedSprite {
    public final AnimatedSprite sightChild;
    private final LinkedList<AnimatedSprite> sightLine = new LinkedList<AnimatedSprite>();
    private float laserX;
    private float laserY;
    private final static float dr = 25;
    private final static float r = 5;

    public Sight(final float pX, final float pY, final float pTileWidth, final float pTileHeight,
                 final TiledTextureRegion pTiledTextureRegion, final TiledTextureRegion emptyTexture) {
        super(pX, pY, pTileWidth, pTileHeight, pTiledTextureRegion);
        sightChild = new AnimatedSprite(0, 0, pTileWidth * 2, pTileHeight * 2, emptyTexture);
        attachChild(sightChild);
        sightChild.setPosition(-sightChild.getWidth() / 4, -sightChild.getHeight() / 4);
    }

    public LinkedList<AnimatedSprite> addSightLine(final TiledTextureRegion sightCircle, final float laserX, final float laserY) {
        this.laserX = laserX;
        this.laserY = laserY;
        for (int i = 1; i < 35; i++) {
            final AnimatedSprite sprite = new AnimatedSprite(this.laserX + i * dr + r / 2, this.laserY  - r / 2, 5, 5, sightCircle);
            sightLine.add(sprite);
            sprite.setRotationCenterX(this.laserX - sprite.getX());
            sprite.setRotationCenterY(this.laserY - sprite.getY());
            sprite.setRotation((MathUtils.radToDeg((float) ((float) Math.atan2((laserX - getInitialX() - getWidth() / 2),
                    -(laserY - getInitialY() - getHeight() / 2)) + Math.PI / 2))));
        }
        return sightLine;
    }

    @Override
    public void setPosition(final float pX, final float pY) {
        super.setPosition(pX, pY);
        for (final AnimatedSprite sprite : sightLine) {
            sprite.setRotation((MathUtils.radToDeg((float) ((float) Math.atan2((laserX - getX() - getWidth() / 2),
                                -(laserY - getY() - getHeight() / 2)) + Math.PI / 2))));
        }
    }
}
