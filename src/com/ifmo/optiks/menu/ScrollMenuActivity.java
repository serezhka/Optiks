package com.ifmo.optiks.menu;

import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;
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
import org.anddev.andengine.input.touch.detector.ScrollDetector;
import org.anddev.andengine.input.touch.detector.SurfaceScrollDetector;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.font.FontFactory;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.ui.activity.BaseGameActivity;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 10.04.12
 */

public class ScrollMenuActivity extends BaseGameActivity implements ScrollDetector.IScrollDetectorListener,
        Scene.IOnSceneTouchListener, ClickDetector.IClickDetectorListener {

    private final static String TAG = "onerain";

    private final static int CAMERA_WIDTH = 480;
    private final static int CAMERA_HEIGHT = 800;

    // 假设有60个关卡
    private final static int LEVELS = 60;
    // 每屏显示关卡的列数
    private final static int LEVEL_COLUMNS_PER_SCREEN = 3;
    // 每屏显示关卡的行数
    private final static int LEVEL_ROWS_PER_SCREEN = 3;
    // 页数
    private final static int LEVEL_PAGES = (LEVELS % (LEVEL_COLUMNS_PER_SCREEN * LEVEL_ROWS_PER_SCREEN)) == 0 ?
            (LEVELS / (LEVEL_COLUMNS_PER_SCREEN * LEVEL_ROWS_PER_SCREEN)) : (LEVELS / (LEVEL_COLUMNS_PER_SCREEN * LEVEL_ROWS_PER_SCREEN)) + 1;
    // 关卡之间的间距
    private final static int LEVEL_PADDING = 30;
    // 默认滑动认为是翻页的距离
    private final static int TURN_PAGE_DISTANCE = 150;

    private Scene scene;
    private Camera camera;
    private HUD hud;

    private Font font;

    private SurfaceScrollDetector surfaceScrollDetector;
    private ClickDetector clickDetector;

    private int levelClicked = -1;

    private int maxLevelReached = 30;

    private float distanceX;

    private int page = 0;

    @Override
    public Engine onLoadEngine() {
        // TODO Auto-generated method stub
        camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

        return new Engine(new EngineOptions(true, EngineOptions.ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.camera));
    }

    @Override
    public void onLoadResources() {
        // TODO Auto-generated method stub
        FontFactory.setAssetBasePath("gfx/font/");

        final BitmapTextureAtlas fontTexture = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        this.font = FontFactory.createFromAsset(fontTexture, this, "Plok.ttf", 48, true, Color.WHITE);
        this.mEngine.getTextureManager().loadTexture(fontTexture);
        this.getFontManager().loadFont(this.font);
    }

    @Override
    public Scene onLoadScene() {
        // TODO Auto-generated method stub
        mEngine.registerUpdateHandler(new FPSLogger());

        scene = new Scene();
        scene.setBackground(new ColorBackground(0.9f, 0.9f, 0.9f));

        surfaceScrollDetector = new SurfaceScrollDetector(this);
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
    public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
        // TODO Auto-generated method stub
        clickDetector.onTouchEvent(pSceneTouchEvent);
        surfaceScrollDetector.onTouchEvent(pSceneTouchEvent);

        return true;
    }

    public void onScrollStarted(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX,
                                final float pDistanceY) {
        // TODO Auto-generated method stub
        distanceX = 0;
    }


    public void onScrollFinished(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX,
                                 final float pDistanceY) {
        // TODO Auto-generated method stub
        Log.d(TAG, "page: " + page);

        // 判断是否翻页，注意：手指向左滑动是想看到右一页
        if ((distanceX > TURN_PAGE_DISTANCE) && (page > 0)) {
            Log.d(TAG, "上翻一页");
            page--;
            camera.offsetCenter(distanceX - CAMERA_WIDTH, 0);
        } else if ((distanceX < -TURN_PAGE_DISTANCE) && (page < LEVEL_PAGES - 1)) {
            Log.d(TAG, "下翻一页");
            page++;
            camera.offsetCenter(distanceX + CAMERA_WIDTH, 0);
        } else {
            Log.d(TAG, "不翻");
            camera.offsetCenter(distanceX, 0);
        }
    }

    /**
     * 创建HUD层
     */
    private void createHUD() {
        // 不用考虑层
        hud = new HUD();
        final Rectangle hudBox = new Rectangle(20, 700, 80, 80) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX,
                                         final float pTouchAreaLocalY) {
                // TODO Auto-generated method stub
                if (pSceneTouchEvent.isActionUp()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            Toast.makeText(ScrollMenuActivity.this, "BACK", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                return true;
            }
        };
        hudBox.setColor(90, 60, 90);
        hud.attachChild(hudBox);
        hud.registerTouchArea(hudBox);
        camera.setHUD(hud);
    }

    /**
     * 创建关卡盒子
     */
    private void createLevelBoxes() {
        // 计算行间距
        final int spaceBetweenRaws = (CAMERA_HEIGHT / LEVEL_ROWS_PER_SCREEN) - LEVEL_PADDING;
        // 计算列间距
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
                            // TODO Auto-generated method stub
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

    /**
     * 模拟显示等级载入
     *
     * @param level
     */
    private void loadLevel(final int level) {
        if (level != -1) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Toast.makeText(ScrollMenuActivity.this, "Loading the " + (level + 1) + " level!", Toast.LENGTH_SHORT)
                            .show();
                }
            });
        }
    }

    @Override
    public void onClick(final ClickDetector clickDetector, final TouchEvent touchEvent) {
        // TODO Auto-generated method stub
        loadLevel(levelClicked);
    }

    @Override
    public void onScroll(final ScrollDetector scrollDetector, final TouchEvent touchEvent, final float pDistanceX, final float pDistanceY) {
        // TODO Auto-generated method stub
        camera.offsetCenter(-pDistanceX, 0);

        distanceX += pDistanceX;
    }
}