package com.ifmo.optiks;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.widget.Toast;
import com.ifmo.optiks.base.manager.OptiksSoundManager;
import com.ifmo.optiks.base.manager.OptiksTextureManager;
import com.ifmo.optiks.menu.Menu;
import com.ifmo.optiks.menu.OptiksMenu;
import com.ifmo.optiks.scene.OptiksMenuScene;
import com.ifmo.optiks.scene.OptiksScene;
import com.ifmo.optiks.scene.OptiksScenes;
import com.ifmo.optiks.scene.OptiksSplashScene;
import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 19.04.12
 */

public class OptiksActivity extends BaseGameActivity {

    private static final int CAMERA_WIDTH = 720;
    private static final int CAMERA_HEIGHT = 480;

    private OptiksScene activeScene;
    private Camera camera;

    /* Texture and Sound Manager */
    private OptiksTextureManager textureManager;
    private OptiksSoundManager soundManager;

    /* Splash */
    private TextureRegion splashTextureRegion;
    private TextureRegion tapToScreenRegion;
    private TiledTextureRegion mirrorTextureRegion;

    /* All Scenes */
    private Map<OptiksScenes, OptiksScene> scenes;

    /* Activity is loaded flag */
    public boolean loadComplete = false;

    @Override
    public Engine onLoadEngine() {
        camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        final EngineOptions engineOptions = new EngineOptions(true, EngineOptions.ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera).setNeedsMusic(true).setNeedsSound(true);
        engineOptions.getTouchOptions().setRunOnUpdateThread(true);
        return new Engine(engineOptions);
    }

    @Override
    public void onLoadResources() {

        /* Splash assets path */
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/splash/");

        /* Game Splash */
        final BitmapTextureAtlas splashAtlas = new BitmapTextureAtlas(1024, 1024, TextureOptions.NEAREST);
        splashTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashAtlas, this, "splash.jpg", 0, 0);
        mEngine.getTextureManager().loadTextures(splashAtlas);

        /* Laser Texture */
        final BitmapTextureAtlas mirrorTextureAtlas = new BitmapTextureAtlas(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        mirrorTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mirrorTextureAtlas, this, "mirror_circle.png", 0, 0, 3, 3);
        mEngine.getTextureManager().loadTextures(mirrorTextureAtlas);

        /* Tap to Screen Texture */
        final BitmapTextureAtlas tapToScreenTextureAtlas = new BitmapTextureAtlas(512, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        tapToScreenRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(tapToScreenTextureAtlas, this, "tap_to_screen.png", 0, 0);
        mEngine.getTextureManager().loadTextures(tapToScreenTextureAtlas);
    }

    @Override
    public Scene onLoadScene() {

        this.mEngine.registerUpdateHandler(new FPSLogger());
        this.mEngine.enableVibrator(this);

        /* Load other resources while splash */
        mEngine.registerUpdateHandler(new TimerHandler(0.2f, new ITimerCallback() {
            @Override
            public void onTimePassed(final TimerHandler pTimerHandler) {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                /* Create Texture Manager, Sound Manager, load other resources */
                textureManager = new OptiksTextureManager(OptiksActivity.this);
                soundManager = new OptiksSoundManager(OptiksActivity.this);
                loadScenes();
                loadComplete = true;
            }
        }));

        /*Damian "Junior Gong" Marley – Born to Be Wild*/

        /* Splash Scene */
        final Sprite splash = new Sprite(0, 0, splashTextureRegion);
        splash.setSize(CAMERA_WIDTH, CAMERA_HEIGHT);
        final AnimatedSprite mirror = new AnimatedSprite(180, 170,
                mirrorTextureRegion.getWidth() * 0.9f / 3, mirrorTextureRegion.getHeight() / 3, mirrorTextureRegion);
        final Sprite tapToScreen = new Sprite(CAMERA_WIDTH / 2 - tapToScreenRegion.getWidth() / 4, CAMERA_HEIGHT * 2 / 3,
                tapToScreenRegion.getWidth() / 2, tapToScreenRegion.getHeight() / 2, tapToScreenRegion);

        return new OptiksSplashScene(this, splash, mirror, tapToScreen);
    }

    @Override
    public void onLoadComplete() {
    }

    @Override
    public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
        return activeScene != null && activeScene.onKeyDown(pKeyCode, pEvent);
    }

    public void setActiveScene(final OptiksScene scene) {
        if (activeScene != null) {
            activeScene.setEnabled(false);
        }
        camera.setCenter(CAMERA_WIDTH / 2, CAMERA_HEIGHT / 2);
        this.mEngine.setScene(scene);
        scene.setEnabled(true);
        activeScene = scene;
    }

    public Camera getCamera() {
        return camera;
    }

    public OptiksTextureManager getOptiksTextureManager() {
        return textureManager;
    }

    public OptiksSoundManager getOptiksSoundManager() {
        return soundManager;
    }

    public OptiksScene getScene(final OptiksScenes key) {
        return scenes.get(key);
    }

    public OptiksScene putScene(final OptiksScenes type, final OptiksScene scene) {
        return scenes.put(type, scene);
    }

    public void showToast(final String message, final int duration) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(OptiksActivity.this, message, duration).show();
            }
        });
    }

    public void showExitDialog() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
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
        });
    }

    private void loadScenes() {

        /* All Scenes */
        scenes = new HashMap<OptiksScenes, OptiksScene>();

        /* Menu Scene*/
        final Menu menu = new OptiksMenu(this);
        final OptiksScene menuScene = new OptiksMenuScene(this, menu);
        menuScene.setEnabled(false);
        scenes.put(OptiksScenes.MENU_SCENE, menuScene);
    }
}
