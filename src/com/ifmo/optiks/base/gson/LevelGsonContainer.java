package com.ifmo.optiks.base.gson;

/**
 * Author: Aleksey Vladiev (Avladiev2@gmail.com)
 */

public class LevelGsonContainer {

    private final BaseObjectJsonContainer[] objects;

    public LevelGsonContainer(final int size) {
        this.objects = new BaseObjectJsonContainer[size];
    }

    public BaseObjectJsonContainer[] getObjects() {
        return objects;
    }
}
