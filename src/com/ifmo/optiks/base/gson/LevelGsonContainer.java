package com.ifmo.optiks.base.gson;

/**
 * Author: Aleksey Vladiev (Avladiev2@gmail.com)
 */

public class LevelGsonContainer {

    private final ObjectJsonContainer[] objects;

    public LevelGsonContainer(final int size) {
        this.objects = new ObjectJsonContainer[size];
    }

    public ObjectJsonContainer[] getObjects() {
        return objects;
    }
}
