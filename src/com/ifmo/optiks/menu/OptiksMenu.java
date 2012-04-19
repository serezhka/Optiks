package com.ifmo.optiks.menu;

import com.ifmo.optiks.OptiksActivity;

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

        final MenuItem seasonChoice = new SimpleMenuItem(MenuItemType.SEASON_CHOICE, optiksActivity.menuItemTextureRegion);

        final MenuItem loadSeasons = new SimpleMenuItem(MenuItemType.LOAD_SEASONS, optiksActivity.menuItemTextureRegion);

        final MenuItem settings = new SimpleMenuItem(MenuItemType.SETTINGS, optiksActivity.menuItemTextureRegion);

        final MenuItem gameInfo = new SimpleMenuItem(MenuItemType.GAME_INFO, optiksActivity.menuItemTextureRegion);

        final MenuItem quit = new SimpleMenuItem(MenuItemType.QUIT, optiksActivity.menuItemTextureRegion);

        menu.addMenuItem(seasonChoice);
        menu.addMenuItem(loadSeasons);
        menu.addMenuItem(settings);
        menu.addMenuItem(gameInfo);
        menu.addMenuItem(quit);
    }
}
