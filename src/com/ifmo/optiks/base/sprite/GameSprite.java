package com.ifmo.optiks.base.sprite;


import com.ifmo.optiks.base.ObjectType;
import com.ifmo.optiks.base.gson.ObjectGsonContainer;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.vertex.RectangleVertexBuffer;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 27.03.12
 */

public abstract class GameSprite extends Sprite {

    public GameSprite(final float pX, final float pY, final TextureRegion pTextureRegion) {
        super(pX, pY, pTextureRegion);
    }

    public GameSprite(final float pX, final float pY, final float pWidth, final float pHeight, final TextureRegion pTextureRegion) {
        super(pX, pY, pWidth, pHeight, pTextureRegion);
    }

    public GameSprite(final float pX, final float pY, final TextureRegion pTextureRegion, final RectangleVertexBuffer pRectangleVertexBuffer) {
        super(pX, pY, pTextureRegion, pRectangleVertexBuffer);
    }

    public GameSprite(final float pX, final float pY, final float pWidth, final float pHeight, final TextureRegion pTextureRegion, final RectangleVertexBuffer pRectangleVertexBuffer) {
        super(pX, pY, pWidth, pHeight, pTextureRegion, pRectangleVertexBuffer);
    }

    public ObjectGsonContainer getGsonContainer() {
        return new ObjectGsonContainer(this);
    }

    public abstract ObjectType getType();
}
