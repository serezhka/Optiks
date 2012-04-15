package com.ifmo.optiks.menu;

import org.anddev.andengine.entity.scene.menu.item.IMenuItem;
import org.anddev.andengine.entity.text.ChangeableText;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 24.03.12
 */

public interface MenuItem extends IMenuItem {

    public String getName();

    public MenuItemType getType();

    public boolean hasContextMenu();

    public Menu getContextMenu();

    public void setContextMenu(final Menu menu);

    public void setText(final ChangeableText text);
}
