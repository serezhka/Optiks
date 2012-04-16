package com.ifmo.optiks.base.primitive_scene_items.sprite;

import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.vertex.RectangleVertexBuffer;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 27.03.12
 */

public class Aim extends GameSprite {

    public Aim(final float pX, final float pY, final TextureRegion pTextureRegion) {
        super(pX, pY, pTextureRegion);
    }

    public Aim(final float pX, final float pY, final float pWidth, final float pHeight, final TextureRegion pTextureRegion) {
        super(pX, pY, pWidth, pHeight, pTextureRegion);
    }

    public Aim(final float pX, final float pY, final TextureRegion pTextureRegion, final RectangleVertexBuffer pRectangleVertexBuffer) {
        super(pX, pY, pTextureRegion, pRectangleVertexBuffer);
    }

    public Aim(final float pX, final float pY, final float pWidth, final float pHeight, final TextureRegion pTextureRegion, final RectangleVertexBuffer pRectangleVertexBuffer) {
        super(pX, pY, pWidth, pHeight, pTextureRegion, pRectangleVertexBuffer);
    }

    public ObjectType getType() {
        return ObjectType.AIM;
    }
}
