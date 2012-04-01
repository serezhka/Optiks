package com.ifmo.optiks.base.gson;

/**
 * Author: Aleksey Vladiev (Avladiev2@gmail.com)
 */

public class LevelGsonContainer {

    private final ObjectGsonContainer[] objects;

    public LevelGsonContainer(final int size) {
        this.objects = new ObjectGsonContainer[size];
    }

    public ObjectGsonContainer[] getObjects() {
        return objects;
    }
}
