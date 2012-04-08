package com.ifmo.optiks.menu;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;
import com.ifmo.optiks.MainOptiksActivity;
import com.ifmo.optiks.OptiksProps;
import com.ifmo.optiks.base.gson.Converter;
import com.ifmo.optiks.base.gson.GsonFromServer;
import com.ifmo.optiks.provider.OptiksProviderMetaData;
import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.scene.menu.MenuScene;
import org.anddev.andengine.entity.scene.menu.item.IMenuItem;
import org.anddev.andengine.entity.scene.menu.item.TextMenuItem;
import org.anddev.andengine.entity.scene.menu.item.decorator.ColorMenuItemDecorator;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.input.touch.detector.ScrollDetector;
import org.anddev.andengine.input.touch.detector.SurfaceScrollDetector;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.font.FontFactory;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import javax.microedition.khronos.opengles.GL10;
import java.io.IOException;
import java.util.Collection;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 23.03.12
 */

public class MenuActivity extends BaseGameActivity implements Scene.IOnSceneTouchListener, ScrollDetector.IScrollDetectorListener, MenuScene.IOnMenuItemClickListener {

    private static final int CAMERA_WIDTH = 720;
    private static final int CAMERA_HEIGHT = 480;

    private Menu menu;
    private Menu activeMenu;
    private Sprite background;
    protected Camera camera;
    protected Scene mainScene;
    protected MenuScene menuScene;

    private Font font;
    private TextureRegion backgroundTextureRegion;

    private SurfaceScrollDetector scrollDetector;

    private final static String TAG = "MenuTAG";

    @Override
    public Engine onLoadEngine() {
        camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        return new Engine(new EngineOptions(true, EngineOptions.ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.camera));
    }

    @Override
    public void onLoadResources() {

        FontFactory.setAssetBasePath("gfx/font/");

        final BitmapTextureAtlas fontTexture = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        this.font = FontFactory.createFromAsset(fontTexture, this, "Plok.ttf", 48, true, Color.WHITE);
        this.mEngine.getTextureManager().loadTexture(fontTexture);
        this.getFontManager().loadFont(this.font);

        TextureRegion region;
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");

        final BitmapTextureAtlas backgroundAtlas = new BitmapTextureAtlas(1024, 1024, TextureOptions.REPEATING_BILINEAR_PREMULTIPLYALPHA);
        backgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(backgroundAtlas, this, "background.jpg", 0, 0);

        final BitmapTextureAtlas menuAtlas = new BitmapTextureAtlas(256, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

        menu = new SimpleMenu();

        final Menu levelChoiceMenu = new SimpleMenu();
        levelChoiceMenu.setParent(menu);

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
        final MenuItem gameInfo = new SimpleMenuItem(MenuItemType.LOAD_LEVELS, region);

        region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuAtlas, this, "menu_quit_rus.png", 0, 100);
        final MenuItem quit = new SimpleMenuItem(MenuItemType.QUIT, region);

        menu.addMenuItem(levelChoice);
        menu.addMenuItem(gameInfo);
        menu.addMenuItem(quit);

        mEngine.getTextureManager().loadTexture(backgroundAtlas);
        mEngine.getTextureManager().loadTexture(menuAtlas);
    }

    @Override
    public Scene onLoadScene() {

        scrollDetector = new SurfaceScrollDetector(this);
        mEngine.registerUpdateHandler(new FPSLogger());

        mainScene = new Scene();

        /* Set background */
        mainScene.setBackground(new ColorBackground(0.09804f, 0.6274f, 0.8784f));
        final int x = (CAMERA_WIDTH - this.backgroundTextureRegion.getWidth()) / 2;
        final int y = (CAMERA_HEIGHT - this.backgroundTextureRegion.getHeight()) / 2;
        background = new Sprite(x, y, this.backgroundTextureRegion);
        background.setScale(1.25f);
        mainScene.attachChild(background);

        /* Set Menu Scene */
        menuScene = createMenuScene(menu);
        //mainScene.setChildScene(menuScene, false, true, true);
        mainScene.setChildScene(menuScene);

        return mainScene;
    }

    @Override
    public void onLoadComplete() {
    }

    @Override
    public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
        return super.onKeyDown(pKeyCode, pEvent);
    }

    @Override
    public void onScroll(final ScrollDetector scrollDetector, final TouchEvent touchEvent, final float pDistanceX, final float pDistanceY) {
        menuScene.setPosition(menuScene.getX(), menuScene.getY() + pDistanceY);
    }

    public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
        scrollDetector.onTouchEvent(pSceneTouchEvent);
        return true;
    }

    @Override
    public boolean onMenuItemClicked(final MenuScene pMenuScene, final IMenuItem pMenuItem, final float pMenuItemLocalX, final float pMenuItemLocalY) {
        if (pMenuItem instanceof MenuItem) {
            final MenuItem menuItem = (SimpleMenuItem) pMenuItem;
            switch (menuItem.getType()) {
                case LEVEL_CHOICE:
                    if (menuItem.hasContextMenu()) {
                        clearMainScene();
                        menuScene = createMenuScene(menuItem.getContextMenu());
                        mainScene.setChildScene(menuScene);
                        return true;
                    } else {
                        return false;
                    }

                case LOAD_LEVELS:

                    // TODO fix connection error!
                    // TODO seperate method to loading levels
                    // TODO add menu items for new levels

                    Toast.makeText(this, "load levels from Server", Toast.LENGTH_LONG).show();
                    for (int i = 1; i < 3; i++) {
                        final String gson = connect("level=" + i);
                        if (gson != null) {
                            final GsonFromServer gsonFromServer = Converter.getInstance().fromGson(gson, GsonFromServer.class);
                            final ContentValues val = new ContentValues();
                            val.put(OptiksProviderMetaData.LevelTable.NAME, gsonFromServer.name + "");
                            val.put(OptiksProviderMetaData.LevelTable._ID, gsonFromServer.id + "");
                            val.put(OptiksProviderMetaData.LevelTable.VALUE, gsonFromServer.level + "");
                            final Cursor cursor = getContentResolver().query(OptiksProviderMetaData.LevelTable.CONTENT_URI, null, OptiksProviderMetaData.LevelTable._ID + "=" + gsonFromServer.id, null, null);
                            if (cursor.getCount() != 0) {
                                getContentResolver().update(OptiksProviderMetaData.LevelTable.CONTENT_URI, val, "_id=?", new String[]{gsonFromServer.id + ""});
                            } else {
                                getContentResolver().insert(OptiksProviderMetaData.LevelTable.CONTENT_URI, val);
                            }
                            Toast.makeText(this, "level  " + gsonFromServer.id + "load", Toast.LENGTH_LONG).show();
                        }
                    }
                    break;

                case QUIT:
                    //finish();
                    showExitDialog();
                    return true;

                case BACK:
                    clearMainScene();
                    menuScene = createMenuScene(activeMenu.getParent());
                    mainScene.setChildScene(menuScene);
                    return true;

                case LEVEL:
                    if (menuItem instanceof LevelMenuItem) {
                        final LevelMenuItem levelMenuItem = (LevelMenuItem) menuItem;
                        final int id = levelMenuItem.getLevel();
                        final Intent intent = new Intent(this, MainOptiksActivity.class);
                        intent.putExtra(OptiksProviderMetaData.LevelTable._ID, id);
                        startActivity(intent);
                        return true;
                    }
                    return false;
                default:
                    return false;
            }
        }
        return false;
    }

    private MenuScene createMenuScene(final Menu menu) {
        activeMenu = menu;
        final MenuScene menuScene = new MenuScene(camera);
        final Collection<MenuItem> menuItems = menu.getMenuItems();
        for (final MenuItem menuItem : menuItems) {
            menuItem.setParent(null);
            if (menuItem instanceof LevelMenuItem) {
                final LevelMenuItem item = (LevelMenuItem) menuItem;
                final IMenuItem levelMenuItem = new ColorMenuItemDecorator(new TextMenuItem(item.getID(), this.font, item.getName() + item.getLevel()), 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
                levelMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
                menuScene.addMenuItem(levelMenuItem);
            } else if (menuItem.getType() == MenuItemType.LOAD_LEVELS) {
                final IMenuItem levelMenuItem = new ColorMenuItemDecorator(new TextMenuItem(menuItem.getID(), this.font, menuItem.getName()), 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
                levelMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
                menuScene.addMenuItem(levelMenuItem);
            } else {
                menuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
                menuScene.addMenuItem(menuItem);
            }
        }
        menuScene.buildAnimations();
        menuScene.setBackgroundEnabled(false);
        menuScene.setOnMenuItemClickListener(this);
        menuScene.setOnSceneTouchListener(this);
        menuScene.setTouchAreaBindingEnabled(true);
        menuScene.setOnSceneTouchListenerBindingEnabled(true);
        return menuScene;
    }

    private String connect(final String param) {
        final String url = OptiksProps.getProperty(OptiksProps.Keys.SERVER_ADDRESS.toString())
                + "/" + OptiksProps.getProperty(OptiksProps.Keys.GET_LEVEL_BOLET.toString());
        String res = null;
        final HttpClient client = new DefaultHttpClient();
        final HttpGet httpGet = new HttpGet(url + "?" + param);

        final ResponseHandler<String> responseHandler = new BasicResponseHandler();
        try {
            res = client.execute(httpGet, responseHandler);
        } catch (IOException e) {
            Log.e(TAG, "", e);
        }
        return res;
    }

    private void clearMainScene() {
        mainScene.detachChildren();
        mainScene.reset();
        mainScene.attachChild(background);
    }

    public void showExitDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setTitle("EXIT")
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int id) {
                                MenuActivity.this.finish();
                            }
                        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}