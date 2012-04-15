package com.ifmo.optiks.menu;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 24.03.12
 */

public enum MenuItemType {

    LEVEL_CHOICE("Select Level", 0),
    LOAD_LEVELS("Load Levels", 1),
    QUIT("Exit", 2),
    BACK("Back", 3),
    LEVEL("Level", 4),
    GAME_INFO("Game Info", 5);

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
