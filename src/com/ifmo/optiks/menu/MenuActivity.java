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
import com.ifmo.optiks.MainActivity;
import com.ifmo.optiks.base.gson.Converter;
import com.ifmo.optiks.base.gson.GsonFromServer;
import com.ifmo.optiks.provider.OptiksProviderMetaData;
import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.scene.menu.MenuScene;
import org.anddev.andengine.entity.scene.menu.item.IMenuItem;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.ChangeableText;
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
    protected Scene splashScene;
    protected MenuScene menuScene;

    private Font font;
    private TextureRegion backgroundTextureRegion;
    private TextureRegion splashTextureRegion;

    private SurfaceScrollDetector scrollDetector;

    private final static String TAG = "MenuTAG";

    @Override
    public Engine onLoadEngine() {
        camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        return new Engine(new EngineOptions(true, EngineOptions.ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.camera));
    }

    @Override
    public void onLoadResources() {
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        final BitmapTextureAtlas splashAtlas = new BitmapTextureAtlas(1024, 1024, TextureOptions.NEAREST);
        splashTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashAtlas, this, "splash.png", 0, 0);
        mEngine.getTextureManager().loadTextures(splashAtlas);
    }

    @Override
    public Scene onLoadScene() {
        splashScene = new Scene();
        splashScene.setBackgroundEnabled(false);
        final Sprite splash = new Sprite(0, 0, splashTextureRegion);
        splash.setSize(CAMERA_WIDTH, CAMERA_HEIGHT);
        splashScene.attachChild(splash);
        return this.splashScene;
    }

    @Override
    public void onLoadComplete() {
        mEngine.registerUpdateHandler(new TimerHandler(3f, new ITimerCallback() {
            @Override
            public void onTimePassed(final TimerHandler pTimerHandler) {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                loadResources();
                loadScenes();
                mEngine.setScene(mainScene);
            }
        }));
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
                    boolean sac = true;
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
                        } else {
                            sac = false;
                        }
                    }
                    if (sac) {
                        Toast.makeText(this, "load levels from Server", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "error", Toast.LENGTH_LONG).show();
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
                        final Intent intent = new Intent(this, MainActivity.class);
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

    private void loadResources() {

        FontFactory.setAssetBasePath("gfx/font/");

        final BitmapTextureAtlas fontTexture = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        this.font = FontFactory.createFromAsset(fontTexture, this, "Plok.ttf", 24, true, Color.rgb(255, 140, 0));
        this.mEngine.getTextureManager().loadTexture(fontTexture);
        this.getFontManager().loadFont(this.font);

        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");

        final BitmapTextureAtlas backgroundAtlas = new BitmapTextureAtlas(1024, 1024, TextureOptions.REPEATING_BILINEAR_PREMULTIPLYALPHA);
        backgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(backgroundAtlas, this, "background.jpg", 0, 0);

        final BitmapTextureAtlas menuAtlas = new BitmapTextureAtlas(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

        final TextureRegion region;
        region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuAtlas, this, "menu_empty.png", 0, 150);

        menu = new SimpleMenu();

        final Menu levelChoiceMenu = new SimpleMenu();
        levelChoiceMenu.setParent(menu);

        final int levelsCount = getLevelsCount();
        for (int i = 1; i <= levelsCount; i++) {
            final MenuItem level = new LevelMenuItem(MenuItemType.LEVEL, i, region);
            levelChoiceMenu.addMenuItem(level);
        }

        final MenuItem back = new SimpleMenuItem(MenuItemType.BACK, region);
        levelChoiceMenu.addMenuItem(back);

        final MenuItem levelChoice = new SimpleMenuItem(MenuItemType.LEVEL_CHOICE, region);
        levelChoice.setContextMenu(levelChoiceMenu);

        final MenuItem loadLevels = new SimpleMenuItem(MenuItemType.LOAD_LEVELS, region);

        final MenuItem gameInfo = new SimpleMenuItem(MenuItemType.GAME_INFO, region);

        final MenuItem quit = new SimpleMenuItem(MenuItemType.QUIT, region);

        menu.addMenuItem(levelChoice);
        menu.addMenuItem(loadLevels);
        menu.addMenuItem(gameInfo);
        menu.addMenuItem(quit);

        mEngine.getTextureManager().loadTexture(backgroundAtlas);
        mEngine.getTextureManager().loadTexture(menuAtlas);
    }

    private int getLevelsCount() {
        return managedQuery(OptiksProviderMetaData.LevelTable.CONTENT_URI, null, null, null, null).getCount();
    }

    private void loadScenes() {

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
        mainScene.setChildScene(menuScene);
    }

    private MenuScene createMenuScene(final Menu menu) {
        activeMenu = menu;
        final MenuScene menuScene = new MenuScene(camera);
        final Collection<MenuItem> menuItems = menu.getMenuItems();
        for (final MenuItem menuItem : menuItems) {
            menuItem.setParent(null);
            menuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
            if (menuItem instanceof LevelMenuItem) {
                final LevelMenuItem item = (LevelMenuItem) menuItem;
                item.setText(new ChangeableText(0, 0, font, item.getName() + item.getLevel()));
                menuScene.addMenuItem(item);
            } else {
                menuItem.setText(new ChangeableText(0, 0, font, menuItem.getName()));
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
        /*final String url = OptiksProps.getProperty(OptiksProps.Keys.SERVER_ADDRESS.toString())
                + "/" + OptiksProps.getProperty(OptiksProps.Keys.GET_LEVEL_BOLET.toString());*/
        final String url = "http://89.112.11.137:8028/Download";
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