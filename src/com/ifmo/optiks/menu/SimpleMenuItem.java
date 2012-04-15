package com.ifmo.optiks.menu;

import org.anddev.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 24.03.12
 */

public class SimpleMenuItem extends SpriteMenuItem implements MenuItem {

    private final MenuItemType type;

    private Menu contextMenu;

    public SimpleMenuItem(final MenuItemType type, final TextureRegion region) {
        super(type.getId(), region);
        this.type = type;
    }

    @Override
    public String getName() {
        return getType().toString();
    }

    @Override
    public MenuItemType getType() {
        return type;
    }

    @Override
    public boolean hasContextMenu() {
        return contextMenu != null;
    }

    @Override
    public Menu getContextMenu() {
        return contextMenu;
    }

    @Override
    public void setContextMenu(final Menu menu) {
        this.contextMenu = menu;
    }

    @Override
    public void setText(final ChangeableText text) {
        this.detachChildren();
        this.attachChild(text);
        text.setPosition(this.getWidth() / 2 - text.getWidth() / 2, this.getHeight() / 2 - text.getHeight() / 2);
    }
}
