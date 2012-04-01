package com.ifmo.optiks.base.sprite;

import com.ifmo.optiks.base.ObjectType;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.vertex.RectangleVertexBuffer;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 27.03.12
 */

public class Barrier extends GameSprite {

    public Barrier(final float pX, final float pY, final TextureRegion pTextureRegion) {
        super(pX, pY, pTextureRegion);
    }

    public Barrier(final float pX, final float pY, final float pWidth, final float pHeight, final TextureRegion pTextureRegion) {
        super(pX, pY, pWidth, pHeight, pTextureRegion);
    }

    public Barrier(final float pX, final float pY, final TextureRegion pTextureRegion, final RectangleVertexBuffer pRectangleVertexBuffer) {
        super(pX, pY, pTextureRegion, pRectangleVertexBuffer);
    }

    public Barrier(final float pX, final float pY, final float pWidth, final float pHeight, final TextureRegion pTextureRegion, final RectangleVertexBuffer pRectangleVertexBuffer) {
        super(pX, pY, pWidth, pHeight, pTextureRegion, pRectangleVertexBuffer);
    }

    public ObjectType getType() {
        return ObjectType.BARRIER;
    }
}
