package com.ifmo.optiks.menu;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 25.04.12
 */

public class SeasonMenuItem {

    private final int id;
    private final String name;
    private final String description;

    public SeasonMenuItem(final int id, final String name, final String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
