package com.ifmo.optiks.base.primitive_game_scene_items.sprite;

/**
 * User: dududko@gmail.com
 * Date: 17.04.12
 */
public enum BodyForm {
    CIRCLE("CIRCLE"), RECTANGLE("RECTANGLE"), DEFAULT("DEFAULT");
    public final String name;

    private BodyForm(final String name) {
        this.name = name;
    }

    public static BodyForm getType(final String type) {
        if (type.equals(CIRCLE.name)) {
            return CIRCLE;
        } else if (type.equals(RECTANGLE.name)) {
            return RECTANGLE;
        } else if (type.equals(DEFAULT.name)) {
            return DEFAULT;
        } else {
            throw new IllegalArgumentException("unicom type :" + type);
        }
    }

}
