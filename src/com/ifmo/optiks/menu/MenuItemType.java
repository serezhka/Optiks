package com.ifmo.optiks.menu;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 24.03.12
 */

public enum MenuItemType {
    
    LEVEL_CHOICE("Выбор уровня", 0),
    GAME_INFO("Об игре", 1),
    QUIT("Выход", 2),
    BACK("Назад", 3),
    LEVEL("Уровень", 4);
    
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
