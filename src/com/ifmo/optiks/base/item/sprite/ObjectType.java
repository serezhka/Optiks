package com.ifmo.optiks.base.item.sprite;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 21.03.12
 */

public enum ObjectType {

    LASER("LASER"),
    MIRROR("MIRROR"),
    BARRIER("BARRIER"),
    AIM("AIM");

    final private String name;

    private ObjectType(final String name) {
        this.name = name;
    }

    public static ObjectType getType(final String type) {
        for (final ObjectType objectType : ObjectType.values()) {
            if (type.equalsIgnoreCase(objectType.name)) {
                return objectType;
            }
        }
        throw new IllegalArgumentException("strange type: " + type);
    }
}
