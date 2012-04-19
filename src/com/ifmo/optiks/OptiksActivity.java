package com.ifmo.optiks;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import com.ifmo.optiks.menu.Menu;
import com.ifmo.optiks.menu.OptiksMenu;
import com.ifmo.optiks.menu.OptiksSplashScene;
import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.font.FontFactory;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 19.04.12
 */

public class OptiksActivity extends BaseGameActivity {

    private static final int CAMERA_WIDTH = 720;
    private static final int CAMERA_HEIGHT = 480;

    private Scene activeScene;
    private Camera camera;

    /* Splash */
    public Sprite splash;
    private TextureRegion splashTextureRegion;

    /* Menu Background */
    public Sprite menuBackground;
    private TextureRegion menuBackgroundTextureRegion;

    /* Menu Font */
    public Font menuFont;

    /* Menu Item */
    public TextureRegion menuItemTextureRegion;

    /* Menu */
    public Menu menu;

    /* Activity is loaded flag */
    public boolean loadComplete = false;

    @Override
    public Engine onLoadEngine() {
        camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        return new Engine(new EngineOptions(true, EngineOptions.ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.camera));
    }

    @Override
    public void onLoadResources() {

        /* Splash assets path */
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/splash/");

        /* Game Splash */
        final BitmapTextureAtlas splashAtlas = new BitmapTextureAtlas(1024, 1024, TextureOptions.NEAREST);
        splashTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashAtlas, this, "splash.png", 0, 0);
        mEngine.getTextureManager().loadTextures(splashAtlas);
    }

    @Override
    public Scene onLoadScene() {

        /* Load other resources while splash */
        mEngine.registerUpdateHandler(new TimerHandler(0.2f, new ITimerCallback() {
            @Override
            public void onTimePassed(final TimerHandler pTimerHandler) {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                loadResources();
                loadScenes();
                loadComplete = true;
            }
        }));

        /* Splash Scene */
        splash = new Sprite(0, 0, splashTextureRegion);
        splash.setSize(CAMERA_WIDTH, CAMERA_HEIGHT);
        return new OptiksSplashScene(this, splash);
    }

    @Override
    public void onLoadComplete() {
    }

    public Scene getActiveScene() {
        return activeScene;
    }

    public void setActiveScene(final Scene scene) {
        this.mEngine.setScene(scene);
        activeScene = scene;
    }

    public Camera getCamera() {
        return camera;
    }

    public void showExitDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(OptiksActivity.this);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setTitle("EXIT")
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int id) {
                                OptiksActivity.this.finish();
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

    private void loadResources() {

        /* Font assets path */
        FontFactory.setAssetBasePath("font/");

        /* Menu Font */
        final BitmapTextureAtlas menuFontTexture = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        menuFont = FontFactory.createFromAsset(menuFontTexture, this, "Plok.ttf", 24, true, Color.rgb(255, 140, 0));
        mEngine.getTextureManager().loadTexture(menuFontTexture);
        this.getFontManager().loadFont(menuFont);

        /* Menu assets path */
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/main/");

        /* Menu Background */
        final BitmapTextureAtlas menuBackgroundAtlas = new BitmapTextureAtlas(1024, 1024, TextureOptions.REPEATING_BILINEAR_PREMULTIPLYALPHA);
        menuBackgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuBackgroundAtlas, this, "background.jpg", 0, 0);
        mEngine.getTextureManager().loadTexture(menuBackgroundAtlas);

        /* Menu Item */
        final BitmapTextureAtlas menuAtlas = new BitmapTextureAtlas(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        menuItemTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuAtlas, this, "menu_empty.png", 0, 0);
        mEngine.getTextureManager().loadTexture(menuAtlas);
    }

    private void loadScenes() {

        /* Menu Background */
        final int x = (CAMERA_WIDTH - this.menuBackgroundTextureRegion.getWidth()) / 2;
        final int y = (CAMERA_HEIGHT - this.menuBackgroundTextureRegion.getHeight()) / 2;
        menuBackground = new Sprite(x, y, this.menuBackgroundTextureRegion);
        menuBackground.setScale(1.25f);

        /* Menu */
        menu = new OptiksMenu(this);
    }
}
