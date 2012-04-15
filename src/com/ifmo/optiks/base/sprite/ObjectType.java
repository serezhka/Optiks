package com.ifmo.optiks.base.sprite;

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
        if (type.equals(LASER.name)) {
            return LASER;
        } else if (type.equals(MIRROR.name)) {
            return MIRROR;
        } else if (type.equals(BARRIER.name)) {
            return BARRIER;
        }
        if (type.equals(AIM.name)) {
            return AIM;
        } else {
            throw new IllegalArgumentException("unicom type :" + type);
        }
    }
}
