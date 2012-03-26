package com.ifmo.optiks.menu;

import android.view.KeyEvent;
import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.menu.MenuScene;
import org.anddev.andengine.entity.scene.menu.item.IMenuItem;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import javax.microedition.khronos.opengles.GL10;
import java.util.Collection;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 23.03.12
 */

public class MenuActivity extends BaseGameActivity implements MenuScene.IOnMenuItemClickListener {

    private static final int CAMERA_WIDTH = 720;
    private static final int CAMERA_HEIGHT = 480;

    private Menu menu;
    protected Camera mCamera;
    protected MenuScene mMenuScene;

    @Override
    public Engine onLoadEngine() {
        this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        return new Engine(new EngineOptions(true, EngineOptions.ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera));
    }

    @Override
    public void onLoadResources() {

        TextureRegion region;
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");

        final BitmapTextureAtlas menuAtlas = new BitmapTextureAtlas(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

        this.menu = new SimpleMenu();

        final Menu levelChoiceMenu = new SimpleMenu();

        region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuAtlas, this, "menu_level_1_rus.png", 0, 150);
        final MenuItem level1 = new LevelMenuItem(MenuItemType.LEVEL, 1, region);

        region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuAtlas, this, "menu_level_2_rus.png", 0, 200);
        final MenuItem level2 = new LevelMenuItem(MenuItemType.LEVEL, 2, region);

        region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuAtlas, this, "menu_back_rus.png", 0, 250);
        final MenuItem back = new SimpleMenuItem(MenuItemType.BACK, region);

        levelChoiceMenu.addMenuItem(level1);
        levelChoiceMenu.addMenuItem(level2);
        levelChoiceMenu.addMenuItem(back);

        region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuAtlas, this, "menu_level_choice_rus.png", 0, 0);
        final MenuItem levelChoice = new SimpleMenuItem(MenuItemType.LEVEL_CHOICE, region);
        levelChoice.setContextMenu(levelChoiceMenu);

        region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuAtlas, this, "menu_game_info_rus.png", 0, 50);
        final MenuItem gameInfo = new SimpleMenuItem(MenuItemType.GAME_INFO, region);

        region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuAtlas, this, "menu_quit_rus.png", 0, 100);
        final MenuItem quit = new SimpleMenuItem(MenuItemType.QUIT, region);

        this.menu.addMenuItem(levelChoice);
        this.menu.addMenuItem(gameInfo);
        this.menu.addMenuItem(quit);

        this.mEngine.getTextureManager().loadTexture(menuAtlas);
    }

    @Override
    public Scene onLoadScene() {
        this.mEngine.registerUpdateHandler(new FPSLogger());
        this.mMenuScene = createMenuScene(this.menu);
        return this.mMenuScene;
    }

    @Override
    public void onLoadComplete() {
    }

    @Override
    public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
        return super.onKeyDown(pKeyCode, pEvent);
    }

    @Override
    public boolean onMenuItemClicked(final MenuScene pMenuScene, final IMenuItem pMenuItem, final float pMenuItemLocalX, final float pMenuItemLocalY) {
        if (pMenuItem instanceof MenuItem) {
            final MenuItem menuItem = (SimpleMenuItem) pMenuItem;
            switch (menuItem.getType()) {

                case LEVEL_CHOICE:
                    if (menuItem.hasContextMenu()) {
                        final MenuScene scene = createMenuScene(menuItem.getContextMenu());
                        pMenuScene.setChildSceneModal(scene);
                        return true;
                    } else {
                        return false;
                    }

                case GAME_INFO:
                    break;

                case QUIT:
                    this.finish();
                    return true;

                case BACK:
                    pMenuScene.back();
                    return true;

                case LEVEL:
                    if (menuItem instanceof LevelMenuItem) {
                        final LevelMenuItem levelMenuItem = (LevelMenuItem) menuItem;
                        switch (levelMenuItem.getLevel()) {
                            case 1:
                                break;
                            case 2:
                                break;
                            default:
                                return false;
                        }
                    }

                default:
                    return false;
            }
        }
        return false;
    }

    final MenuScene createMenuScene(final Menu menu) {
        final MenuScene menuScene = new MenuScene(this.mCamera);
        final Collection<MenuItem> menuItems = menu.getMenuItems();
        for (final MenuItem menuItem : menuItems) {
            menuItem.setParent(null);
            menuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
            menuScene.addMenuItem(menuItem);
        }
        menuScene.buildAnimations();
        menuScene.setBackgroundEnabled(false);
        menuScene.setOnMenuItemClickListener(this);
        return menuScene;
    }
}