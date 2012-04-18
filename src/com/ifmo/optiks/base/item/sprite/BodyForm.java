package com.ifmo.optiks.base.item.sprite;

/**
 * User: dududko@gmail.com
 * Date: 17.04.12
 */

public enum BodyForm {

    CIRCLE("CIRCLE"),
    RECTANGLE("RECTANGLE"),
    DEFAULT("DEFAULT");

    public final String name;

    private BodyForm(final String name) {
        this.name = name;
    }

    public static BodyForm getType(final String type) {
        for (final BodyForm form : BodyForm.values()) {
            if (type.equalsIgnoreCase(form.name)) {
                return form;
            }
        }
        throw new IllegalArgumentException("strange type: " + type);
    }
}
