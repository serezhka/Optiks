package com.ifmo.optiks;

import android.app.AlertDialog;
import android.content.DialogInterface;
import com.ifmo.optiks.base.manager.OptiksTextureManager;
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

    /* Texture Manager */
    private OptiksTextureManager textureManager;

    /* Splash */
    private TextureRegion splashTextureRegion;

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
                /* Create Texture Manager, load other resources */
                textureManager = new OptiksTextureManager(OptiksActivity.this);
                loadScenes();
                loadComplete = true;
            }
        }));

        /* Splash Scene */
        final Sprite splash = new Sprite(0, 0, splashTextureRegion);
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

    private void loadScenes() {

        // TODO create menuScene one time and never destroy. Just turn off listeners on it!

        /* Menu */
        menu = new OptiksMenu(this);
    }

    public OptiksTextureManager getOptiksTextureManager() {
        return textureManager;
    }
}
