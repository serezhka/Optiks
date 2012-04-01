package com.ifmo.optiks.menu;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
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
import org.anddev.andengine.entity.util.FPSLogger;
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

public class MenuActivity extends BaseGameActivity implements MenuScene.IOnMenuItemClickListener {

    private static final int CAMERA_WIDTH = 720;
    private static final int CAMERA_HEIGHT = 480;

    private Menu menu;
    protected Camera mCamera;
    protected Scene mainScene;
    protected MenuScene menuScene;

    private final String level1 = "{\"objects\":[" +
            "{\"type\":\"LASER\",\"pY\":2.0,\"rotation\":0.0,\"width\":60,\"height\":60,\"pX\":2.0}," +
            "{\"type\":\"MIRROR\",\"pY\":4.0,\"rotation\":0.0,\"width\":200,\"height\":45,\"pX\":7.0}," +
            "{\"type\":\"MIRROR\",\"pY\":6.0,\"rotation\":0.0,\"width\":200,\"height\":45,\"pX\":7.0}," +
            "{\"type\":\"AIM\",\"pY\":13.0,\"rotation\":0.0,\"width\":70,\"height\":70,\"pX\":21.0}," +
            "{\"type\":\"BARRIER\",\"pY\":4.0,\"rotation\":0.0,\"width\":50,\"height\":150,\"pX\":12.0}" +
            "]}";

    private final String level2 = "{\"objects\":[" +
            "{\"type\":\"LASER\",\"pY\":2.0,\"rotation\":0.0,\"width\":60,\"height\":60,\"pX\":2.0}," +
            "{\"type\":\"MIRROR\",\"pY\":10.0,\"rotation\":30.0,\"width\":20,\"height\":45,\"pX\":7.0}," +
            "{\"type\":\"MIRROR\",\"pY\":10.0,\"rotation\":0.0,\"width\":100,\"height\":45,\"pX\":7.0}," +
            "{\"type\":\"AIM\",\"pY\":13.0,\"rotation\":0.0,\"width\":70,\"height\":70,\"pX\":21.0}," +
            "{\"type\":\"BARRIER\",\"pY\":4.0,\"rotation\":0.0,\"width\":50,\"height\":150,\"pX\":12.0}" +
            "]}";

    private final static String TAG = "MenuTAG";

    /*  @Override
    protected void onCreate(Bundle pSavedInstanceState) {
        super.onCreate(pSavedInstanceState);
        final ContentValues cv = new ContentValues();
        cv.put(OptiksProviderMetaData.LevelTable.VALUE, level1);
        cv.put(OptiksProviderMetaData.LevelTable.NAME, "level1");
        getContentResolver().insert(OptiksProviderMetaData.LevelTable.CONTENT_URI, cv);
        cv.put(OptiksProviderMetaData.LevelTable.VALUE, level2);
        cv.put(OptiksProviderMetaData.LevelTable.NAME, "level2");
        getContentResolver().insert(OptiksProviderMetaData.LevelTable.CONTENT_URI, cv);
    }*/

    @Override
    public Engine onLoadEngine() {
        this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        return new Engine(new EngineOptions(true, EngineOptions.ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera));
    }

    @Override
    public void onLoadResources() {

        TextureRegion region;
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");

        final BitmapTextureAtlas menuAtlas = new BitmapTextureAtlas(256, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

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
        this.mainScene = new Scene();
        this.mainScene.setBackground(new ColorBackground(0.09804f, 0.6274f, 0.8784f));
        this.menuScene = createMenuScene(this.menu);
        this.mainScene.setChildScene(this.menuScene, false, true, true);
        return this.mainScene;
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

                    // TODO fix connection error!

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
                    this.finish();
                    return true;

                case BACK:
                    pMenuScene.back();
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

    public static String connect(final String param) {
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
}