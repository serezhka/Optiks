package com.ifmo.optiks.scene;

import android.view.KeyEvent;
import com.ifmo.optiks.OptiksActivity;
import com.ifmo.optiks.menu.Menu;
import com.ifmo.optiks.menu.MenuItem;
import com.ifmo.optiks.menu.SimpleMenuItem;
import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.scene.menu.MenuScene;
import org.anddev.andengine.entity.scene.menu.item.IMenuItem;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

import javax.microedition.khronos.opengles.GL10;
import java.util.Collection;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 19.04.12
 */

public class OptiksMenuScene extends OptiksScene implements MenuScene.IOnMenuItemClickListener/*, OptiksScrollDetector.IScrollDetectorListener, Scene.IOnSceneTouchListener*/ {

    private final OptiksActivity optiksActivity;

    //private final OptiksSurfaceScrollDetector scrollDetector;

    private final MenuScene menuScene;

    private Menu activeMenu;

    private int menuHeight;

    public OptiksMenuScene(final OptiksActivity optiksActivity, final Menu menu) {
        super();
        this.optiksActivity = optiksActivity;
        activeMenu = menu;
        setBackground();
        menuScene = createMenuScene();
        this.setChildScene(menuScene);
        // TODO activate scroll ability if menuHeight > CAMERA_HEIGHT
        //scrollDetector = new OptiksSurfaceScrollDetector(this);
        //this.setOnSceneTouchListener(this);
    }

    @Override
    public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
        if (pKeyCode == KeyEvent.KEYCODE_BACK && pEvent.getAction() == KeyEvent.ACTION_DOWN) {
            optiksActivity.showExitDialog();
            return true;
        }
        return false;
    }

    @Override
    public boolean onMenuItemClicked(final MenuScene pMenuScene, final IMenuItem pMenuItem, final float pMenuItemLocalX, final float pMenuItemLocalY) {
        if (pMenuItem instanceof MenuItem) {
            final MenuItem menuItem = (SimpleMenuItem) pMenuItem;
            switch (menuItem.getType()) {

                case SEASON_CHOICE:
                    final OptiksScene seasons = new OptiksSeasonsScene(optiksActivity);
                    optiksActivity.scenes.put(OptiksScenes.SEASONS_SCENE, seasons);
                    optiksActivity.setActiveScene(seasons);
                    break;

                case SETTINGS:

                    // TODO implement settings scene

                case QUIT:
                    optiksActivity.showExitDialog();
                    return true;

                case BACK:
                    this.setChildScene(new OptiksMenuScene(optiksActivity, activeMenu.getParent()));
                    optiksActivity.setActiveScene(this);
                    return true;

                default:
                    return false;
            }
        }
        return false;
    }

    /*@Override
    public void onScrollStarted(final OptiksScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
    }

    @Override
    public void onScroll(final OptiksScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
        if (pDistanceY > 0) {
            if (menuScene.getY() + pDistanceY > menuHeight / 4) {
                menuScene.setPosition(menuScene.getX(), menuHeight / 4);
            } else {
                menuScene.setPosition(menuScene.getX(), menuScene.getY() + pDistanceY);
            }
        } else {
            if (menuScene.getY() + pDistanceY < -menuHeight / 4) {
                menuScene.setPosition(menuScene.getX(), -menuHeight / 4);
            } else {
                menuScene.setPosition(menuScene.getX(), menuScene.getY() + pDistanceY);
            }
        }
    }

    @Override
    public void onScrollFinished(final OptiksScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
    }*/

    /*@Override
    public boolean onSceneTouchEvent(final Scene scene, final TouchEvent touchEvent) {
        //scrollDetector.onTouchEvent(touchEvent);
        return true;
    }*/

    private MenuScene createMenuScene() {
        menuHeight = 0;
        final MenuScene menuScene = new MenuScene(optiksActivity.getCamera());
        final Collection<MenuItem> menuItems = activeMenu.getMenuItems();
        for (final MenuItem menuItem : menuItems) {
            menuItem.setParent(null);
            menuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
            menuItem.setText(new ChangeableText(0, 0, optiksActivity.getOptiksTextureManager().menuFont, menuItem.getName()));
            menuScene.addMenuItem(menuItem);
            menuHeight += menuItem.getHeight();
        }
        menuScene.buildAnimations();
        menuScene.setBackgroundEnabled(false);

        // TODO fix this stub
        optiksActivity.getEngine().registerUpdateHandler(new TimerHandler(0.5f, new ITimerCallback() {
            @Override
            public void onTimePassed(final TimerHandler pTimerHandler) {
                optiksActivity.getEngine().unregisterUpdateHandler(pTimerHandler);
                menuScene.setOnMenuItemClickListener(OptiksMenuScene.this);
                menuScene.setTouchAreaBindingEnabled(true);
                menuScene.setOnSceneTouchListenerBindingEnabled(true);
            }
        }));

        //menuScene.setPosition(menuScene.getX(), menuHeight / 4);
        return menuScene;
    }

    private void setBackground() {
        this.setBackground(new ColorBackground(0.09804f, 0.6274f, 0.8784f));
        this.setBackgroundEnabled(false);
        final TextureRegion menuBackgroundTextureRegion = optiksActivity.getOptiksTextureManager().menuBackgroundTextureRegion;
        final int x = ((int) optiksActivity.getCamera().getWidth() - menuBackgroundTextureRegion.getWidth()) / 2;
        final int y = ((int) optiksActivity.getCamera().getHeight() - menuBackgroundTextureRegion.getHeight()) / 2;
        final Sprite menuBackground = new Sprite(x, y, menuBackgroundTextureRegion);
        menuBackground.setScale(1.25f);
        this.attachChild(menuBackground);
    }
}
