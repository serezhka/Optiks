package com.ifmo.optiks.menu;

import com.ifmo.optiks.OptiksActivity;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

import java.util.Collection;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 19.04.12
 */

public class OptiksMenu implements Menu {

    private final OptiksActivity optiksActivity;

    private Menu menu;

    public OptiksMenu(final OptiksActivity optiksActivity) {
        this.optiksActivity = optiksActivity;
        createMenu();
    }

    @Override
    public boolean hasParent() {
        return menu.hasParent();
    }

    @Override
    public Menu getParent() {
        return menu.getParent();
    }

    @Override
    public void setParent(final Menu parent) {
        menu.setParent(parent);
    }

    @Override
    public int menuItemsCount() {
        return menu.menuItemsCount();
    }

    @Override
    public Collection<MenuItem> getMenuItems() {
        return menu.getMenuItems();
    }

    @Override
    public void addMenuItem(final MenuItem item) {
        menu.addMenuItem(item);
    }

    private void createMenu() {

        menu = new SimpleMenu();

        final TextureRegion menuItemTextureRegion = optiksActivity.getOptiksTextureManager().menuItemTextureRegion;

        menu.addMenuItem(new SimpleMenuItem(MenuItemType.SEASON_CHOICE, menuItemTextureRegion));
        menu.addMenuItem(new SimpleMenuItem(MenuItemType.SETTINGS, menuItemTextureRegion));
        menu.addMenuItem(new SimpleMenuItem(MenuItemType.GAME_INFO, menuItemTextureRegion));
        menu.addMenuItem(new SimpleMenuItem(MenuItemType.QUIT, menuItemTextureRegion));
    }
}
