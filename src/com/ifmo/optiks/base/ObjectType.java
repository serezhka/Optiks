package com.ifmo.optiks.base;

import static com.ifmo.optiks.base.BodyType.*;

public enum ObjectType {
    LASER(CIRCLE),
    MIRROR(BOX),
    BARRIER(BOX),
    AIM(CIRCLE);
    
    private final BodyType type;
    
    ObjectType(final BodyType type) {
        this.type = type;
    }


    public BodyType getBodyType() {
        return type;
    }
}
