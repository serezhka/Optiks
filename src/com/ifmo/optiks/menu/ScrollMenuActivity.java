package com.ifmo.optiks.menu;

import android.graphics.Color;
import android.widget.Toast;
import com.ifmo.optiks.base.control.OptiksScrollDetector;
import com.ifmo.optiks.base.control.OptiksSurfaceScrollDetector;
import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.camera.hud.HUD;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.input.touch.detector.ClickDetector;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.font.FontFactory;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.ui.activity.BaseGameActivity;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 10.04.12
 */

public class ScrollMenuActivity extends BaseGameActivity implements OptiksScrollDetector.IScrollDetectorListener,
        Scene.IOnSceneTouchListener, ClickDetector.IClickDetectorListener {

    protected static int CAMERA_WIDTH = 720;
    protected static int CAMERA_HEIGHT = 480;

    private final static int LEVELS = 60;
    private final static int LEVEL_COLUMNS_PER_SCREEN = 3;
    private final static int LEVEL_ROWS_PER_SCREEN = 3;
    private final static int LEVEL_PAGES = (LEVELS % (LEVEL_COLUMNS_PER_SCREEN * LEVEL_ROWS_PER_SCREEN)) == 0 ?
            (LEVELS / (LEVEL_COLUMNS_PER_SCREEN * LEVEL_ROWS_PER_SCREEN)) : (LEVELS / (LEVEL_COLUMNS_PER_SCREEN * LEVEL_ROWS_PER_SCREEN)) + 1;
    private final static int LEVEL_PADDING = 30;
    private final static int TURN_PAGE_DISTANCE = 150;

    private Scene scene;
    private Camera camera;
    private HUD hud;

    private Font font;

    private OptiksSurfaceScrollDetector surfaceScrollDetector;
    private ClickDetector clickDetector;

    private int levelClicked = -1;

    private int maxLevelReached = 30;

    private float distanceX;

    private int page = 0;

    @Override
    public Engine onLoadEngine() {
        camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        return new Engine(new EngineOptions(true, EngineOptions.ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.camera));
    }

    @Override
    public void onLoadResources() {
        FontFactory.setAssetBasePath("font/");
        final BitmapTextureAtlas fontTexture = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        this.font = FontFactory.createFromAsset(fontTexture, this, "Plok.ttf", 48, true, Color.WHITE);
        this.getFontManager().loadFont(this.font);
    }

    @Override
    public Scene onLoadScene() {
        mEngine.registerUpdateHandler(new FPSLogger());

        scene = new Scene();
        scene.setBackground(new ColorBackground(0.9f, 0.9f, 0.9f));

        surfaceScrollDetector = new OptiksSurfaceScrollDetector(this);
        clickDetector = new ClickDetector(this);

        scene.setOnSceneTouchListener(this);
        scene.setTouchAreaBindingEnabled(true);
        scene.setOnSceneTouchListenerBindingEnabled(true);

        createHUD();
        createLevelBoxes();

        return scene;
    }

    @Override
    public void onLoadComplete() {
    }

    @Override
    public void onClick(final ClickDetector clickDetector, final TouchEvent touchEvent) {
        loadLevel(levelClicked);
    }

    @Override
    public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
        clickDetector.onTouchEvent(pSceneTouchEvent);
        surfaceScrollDetector.onTouchEvent(pSceneTouchEvent);
        return true;
    }

    private void createHUD() {
        hud = new HUD();
        final Rectangle hudBox = new Rectangle(20, 700, 80, 80) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX,
                                         final float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionUp()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ScrollMenuActivity.this, "BACK", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                return true;
            }
        };
        hudBox.setColor(200, 40, 40);
        hud.attachChild(hudBox);
        hud.registerTouchArea(hudBox);
        camera.setHUD(hud);
    }

    private void createLevelBoxes() {
        final int spaceBetweenRaws = (CAMERA_HEIGHT / LEVEL_ROWS_PER_SCREEN) - LEVEL_PADDING;
        final int spaceBetweenColumns = (CAMERA_WIDTH / LEVEL_COLUMNS_PER_SCREEN) - LEVEL_PADDING;

        int level = 0;

        int boxX = LEVEL_PADDING;
        int boxY = LEVEL_PADDING;

        for (int i = 0; i < LEVEL_PAGES; i++) {
            final int startX = i * CAMERA_WIDTH;

            for (int j = 0; j < LEVEL_ROWS_PER_SCREEN; j++) {
                for (int k = 0; k < LEVEL_COLUMNS_PER_SCREEN; k++) {
                    final int levelToLoad = level;
                    final Rectangle box = new Rectangle(startX + boxX, boxY, 100, 100) {
                        @Override
                        public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX,
                                                     final float pTouchAreaLocalY) {
                            levelClicked = levelToLoad;

                            return false;
                        }
                    };
                    if (level >= maxLevelReached) {
                        box.setColor(0, 0, 0.9f);
                    } else {
                        box.setColor(0, 0.9f, 0);
                    }
                    scene.attachChild(box);
                    scene.registerTouchArea(box);
                    int textOffX = 0;
                    if (level < 10) {
                        textOffX = 28;
                    } else {
                        textOffX = 20;
                    }
                    box.attachChild(new Text(textOffX, 20, font, String.valueOf(level + 1)));

                    level++;
                    boxX += spaceBetweenColumns + LEVEL_PADDING;
                    if (level > LEVELS) {
                        break;
                    }
                }
                if (level > LEVELS) {
                    break;
                }
                boxY += spaceBetweenRaws + LEVEL_PADDING;
                boxX = LEVEL_PADDING;
            }
            boxY = LEVEL_PADDING;
        }
    }

    private void loadLevel(final int level) {
        if (level != -1) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ScrollMenuActivity.this, "Loading the " + (level + 1) + " level!", Toast.LENGTH_SHORT)
                            .show();
                }
            });
        }
    }

    @Override
    public void onScrollStarted(final OptiksScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
        distanceX = 0;
    }

    @Override
    public void onScroll(final OptiksScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
        camera.offsetCenter(-pDistanceX, 0);
        distanceX += pDistanceX;
    }

    @Override
    public void onScrollFinished(final OptiksScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
        if ((distanceX > TURN_PAGE_DISTANCE) && (page > 0)) {
            page--;
            camera.offsetCenter(distanceX - CAMERA_WIDTH, 0);
        } else if ((distanceX < -TURN_PAGE_DISTANCE) && (page < LEVEL_PAGES - 1)) {
            page++;
            camera.offsetCenter(distanceX + CAMERA_WIDTH, 0);
        } else {
            camera.offsetCenter(distanceX, 0);
        }
    }
}