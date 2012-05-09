package com.ifmo.optiks.base.manager;

import org.anddev.andengine.input.touch.TouchEvent;

/**
 * Author: Dudko Alex (dududko@gmail.com)
 * Date: 09.05.12
 */
public interface SelectedObject {
    void handle(final TouchEvent touchEvent,final float touchAreaLocalX, final float touchAreaLocalY);
}
