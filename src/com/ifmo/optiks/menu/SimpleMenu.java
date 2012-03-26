package com.ifmo.optiks.menu;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 24.03.12
 */

public class SimpleMenu implements Menu {

    private Menu parent;
    private final Collection<MenuItem> menuItems;

    public SimpleMenu() {
        this.menuItems = new ArrayList<MenuItem>();
    }

    public SimpleMenu(final Collection<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    @Override
    public boolean hasParent() {
        return parent != null;
    }

    @Override
    public Menu getParent() {
        return parent;
    }

    @Override
    public void setParent(final Menu parent) {
        this.parent = parent;
    }

    @Override
    public int menuItemsCount() {
        return menuItems.size();
    }

    @Override
    public Collection<MenuItem> getMenuItems() {
        return menuItems;
    }

    @Override
    public void addMenuItem(final MenuItem item) {
        menuItems.add(item);
    }
}
