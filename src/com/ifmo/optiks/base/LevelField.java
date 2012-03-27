package com.ifmo.optiks.base;

import com.ifmo.optiks.base.sprite.GameSprite;
import org.anddev.andengine.entity.sprite.Sprite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 27.03.12
 */

public class LevelField {

    int level;
    Collection<GameSprite> sprites;

    public int getLevel() {
        return level;
    }

    public void setLevel(final int level) {
        this.level = level;
    }

    public Collection<GameSprite> getSprites() {
        return sprites;
    }

    public void setSprites(final Collection<GameSprite> sprites) {
        this.sprites = sprites;
    }

    public Collection<? extends Sprite> getSpritesByType(final ObjectType type) {
        final Collection<Sprite> result = new LinkedList<Sprite>();
        for (final GameSprite sprite : sprites) {
            if (sprite.getType() == type) {
                result.add(sprite);
            }
        }
        return result;
    }

    public void addSprite(final GameSprite sprite) {
        this.sprites.add(sprite);
    }
    
}
