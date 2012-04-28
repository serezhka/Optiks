package com.ifmo.optiks.scene;

import android.view.KeyEvent;
import com.ifmo.optiks.OptiksActivity;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 22.04.12
 */

public class OptiksSettingsScene extends OptiksScene {

    private final OptiksActivity optiksActivity;

    public OptiksSettingsScene(final OptiksActivity optiksActivity) {
        super();
        this.optiksActivity = optiksActivity;
    }

    @Override
    public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
        if (pKeyCode == KeyEvent.KEYCODE_BACK && pEvent.getAction() == KeyEvent.ACTION_DOWN) {
            optiksActivity.setActiveScene(optiksActivity.scenes.get(OptiksScenes.MENU_SCENE));
            return true;
        }
        return false;
    }
}
