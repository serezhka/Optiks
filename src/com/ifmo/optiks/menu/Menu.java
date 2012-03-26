package com.ifmo.optiks.menu;

import org.anddev.andengine.entity.scene.menu.MenuScene;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;

import java.util.Collection;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 24.03.12
 */

public interface Menu {

    public boolean hasParent();
    
    public Menu getParent();
    
    public void setParent(final Menu parent);
    
    public int menuItemsCount();

    public Collection<MenuItem> getMenuItems();

    public void addMenuItem(final MenuItem item);
}
