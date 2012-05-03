package com.ifmo.optiks.scene;

import android.view.KeyEvent;
import org.anddev.andengine.entity.scene.Scene;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 28.04.12
 */

public abstract class OptiksScene extends Scene implements IOptiksScene {

    @Override
    public void setEnabled(final boolean enabled) {
        if (enabled) {
            this.setIgnoreUpdate(false);
            this.setVisible(true);
        } else {
            this.setIgnoreUpdate(true);
            this.setVisible(false);
        }
    }

    public abstract boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent);
}
