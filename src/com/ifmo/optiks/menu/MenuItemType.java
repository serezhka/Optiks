package com.ifmo.optiks.menu;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 24.03.12
 */

public enum MenuItemType {

    SEASON_CHOICE("Select Season", 0),
    LOAD_SEASONS("Load Seasons", 1),
    QUIT("Exit", 2),
    BACK("Back", 3),
    GAME_INFO("Game Info", 4),
    SETTINGS("Settings", 5);

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
