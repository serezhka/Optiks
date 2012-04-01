package com.ifmo.optiks.menu;

import org.anddev.andengine.opengl.texture.region.TextureRegion;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 26.03.12
 */

public class LevelMenuItem extends SimpleMenuItem implements MenuItem {

    final int level;

    public LevelMenuItem(final MenuItemType type, final int level, final TextureRegion region) {
        super(type, region);
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
