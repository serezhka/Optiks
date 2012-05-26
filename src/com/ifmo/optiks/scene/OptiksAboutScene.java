package com.ifmo.optiks.scene;

import android.view.KeyEvent;
import com.ifmo.optiks.OptiksActivity;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.Text;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 19.05.12
 */

public class OptiksAboutScene extends OptiksScene {

    private final OptiksActivity optiksActivity;

    private final static String ABOUT_TEXT =
            "Optiks 2012\n" +
                    "\n" +
                    "\n" +
                    "developers:\n" +
                    "\n"+
                    "Serezhka (aka StatuS)\n" +
                    "Dududko\n" +
                    "AVladiev";

    public OptiksAboutScene(final OptiksActivity optiksActivity) {
        this.optiksActivity = optiksActivity;
        init();
    }

    @Override
    public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
        if (pKeyCode == KeyEvent.KEYCODE_BACK && pEvent.getAction() == KeyEvent.ACTION_DOWN) {
            optiksActivity.setActiveScene(optiksActivity.getScene(OptiksScenes.MENU_SCENE));
            return true;
        } else if (pKeyCode == KeyEvent.KEYCODE_MENU && pEvent.getAction() == KeyEvent.ACTION_DOWN) {
            optiksActivity.setActiveScene(optiksActivity.getScene(OptiksScenes.MENU_SCENE));
            return true;
        }
        return false;
    }

    private void init() {
        this.setEnabled(false);
        /*final Sprite background = new Sprite(0, 0, optiksActivity.getOptiksTextureManager().aboutBackgroundTextureRegion);
        background.setSize(optiksActivity.getCamera().getWidth(), optiksActivity.getCamera().getHeight());
        this.attachChild(background);*/
        final Text info = new Text(10, 10, optiksActivity.getOptiksTextureManager().aboutFont, ABOUT_TEXT);
        this.attachChild(info);
    }
}
