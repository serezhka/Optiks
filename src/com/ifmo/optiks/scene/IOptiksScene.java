package com.ifmo.optiks.scene;

import android.view.KeyEvent;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 28.04.12
 */

public interface IOptiksScene {

    public void setEnabled(final boolean enabled);

    public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent);
}
