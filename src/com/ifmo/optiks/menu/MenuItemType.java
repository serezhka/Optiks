package com.ifmo.optiks.menu;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 24.03.12
 */

public enum MenuItemType {

    SEASON_CHOICE("Select Season", 0),
    QUIT("Exit", 1),
    BACK("Back", 2),
    GAME_INFO("Game Info", 3),
    SETTINGS("Settings", 4);

    private final String name;
    private final int id;

    private MenuItemType(final String name, final int id) {
        this.name = name;
        this.id = id;
    }

    public String toString() {
        return name;
    }

    public int getId() {
        return id;
    }
}
