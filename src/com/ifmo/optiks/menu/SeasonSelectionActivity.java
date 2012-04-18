package com.ifmo.optiks.menu;

import android.graphics.Color;
import android.widget.Toast;
import com.ifmo.optiks.base.control.OptiksScrollDetector;
import com.ifmo.optiks.base.control.OptiksSurfaceScrollDetector;
import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.input.touch.detector.ClickDetector;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.font.FontFactory;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 18.04.12
 */

public class SeasonSelectionActivity extends BaseGameActivity implements OptiksScrollDetector.IScrollDetectorListener, Scene.IOnSceneTouchListener, ClickDetector.IClickDetectorListener {

    protected static int CAMERA_WIDTH = 720;
    protected static int CAMERA_HEIGHT = 480;

    protected static int FONT_SIZE = 24;
    protected static int PADDING = 50;

    protected static int MENUITEMS = 7;


    private Scene mScene;
    private Camera mCamera;

    private TextureRegion leftArrowTextureRegion;
    private TextureRegion rightArrowTextureRegion;

    private Sprite menuleft;
    private Sprite menuright;

    private OptiksSurfaceScrollDetector mScrollDetector;
    private ClickDetector mClickDetector;

    @SuppressWarnings("FieldCanBeLocal")
    private final float minX = 0;
    private float maxX = 0;
    private float mCurrentX = 0;
    private int iItemClicked = -1;

    private Rectangle scrollBar;
    private final List<TextureRegion> columns = new ArrayList<TextureRegion>();

    @Override
    public void onLoadResources() {
        FontFactory.setAssetBasePath("gfx/font/");
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");

        final BitmapTextureAtlas fontTexture = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        final Font font = FontFactory.createFromAsset(fontTexture, this, "Plok.ttf", FONT_SIZE, true, Color.rgb(255, 140, 0));
        mEngine.getTextureManager().loadTextures(fontTexture);
        this.getFontManager().loadFonts(font);

        for (int i = 0; i < MENUITEMS; i++) {
            final BitmapTextureAtlas mMenuBitmapTextureAtlas = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
            final TextureRegion mMenuTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mMenuBitmapTextureAtlas, this, "season.png", 0, 0);
            this.mEngine.getTextureManager().loadTexture(mMenuBitmapTextureAtlas);
            columns.add(mMenuTextureRegion);
        }
        final BitmapTextureAtlas menuTextureAtlas = new BitmapTextureAtlas(512, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        this.leftArrowTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, this, "arrow_left.png", 0, 0);
        this.rightArrowTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, this, "arrow_right.png", 256, 0);
        this.mEngine.getTextureManager().loadTexture(menuTextureAtlas);
    }

    @Override
    public Engine onLoadEngine() {
        this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        final EngineOptions engineOptions = new EngineOptions(true, EngineOptions.ScreenOrientation.LANDSCAPE, new FillResolutionPolicy(), this.mCamera);
        engineOptions.getTouchOptions().setRunOnUpdateThread(true);
        return new Engine(engineOptions);
    }

    @Override
    public Scene onLoadScene() {
        this.mEngine.registerUpdateHandler(new FPSLogger());

        this.mScene = new Scene();
        this.mScene.setBackground(new ColorBackground(0, 0, 0));

        this.mScrollDetector = new OptiksSurfaceScrollDetector(this);
        this.mClickDetector = new ClickDetector(this);

        this.mScene.setOnSceneTouchListener(this);
        this.mScene.setTouchAreaBindingEnabled(true);
        this.mScene.setOnSceneTouchListenerBindingEnabled(true);

        CreateMenuBoxes();

        return this.mScene;
    }

    @Override
    public void onLoadComplete() {
    }

    @Override
    public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
        this.mClickDetector.onTouchEvent(pSceneTouchEvent);
        this.mScrollDetector.onTouchEvent(pSceneTouchEvent);
        return true;
    }

    @Override
    public void onScrollStarted(final OptiksScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
    }

    @Override
    public void onScroll(final OptiksScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {

        if (mCamera.getMinX() <= 15) {
            menuleft.setVisible(false);
        } else {
            menuleft.setVisible(true);
        }
        if (mCamera.getMinX() > maxX - 15) {
            menuright.setVisible(false);
        } else {
            menuright.setVisible(true);
        }

        if (((mCurrentX - pDistanceX) < minX)) {
            return;
        } else if ((mCurrentX - pDistanceX) > maxX) {
            return;
        }

        this.mCamera.offsetCenter(-pDistanceX, 0);
        mCurrentX -= pDistanceX;

        float tempX = mCamera.getCenterX() - CAMERA_WIDTH / 2;
        tempX += (tempX / (maxX + CAMERA_WIDTH)) * CAMERA_WIDTH;
        scrollBar.setPosition(tempX, scrollBar.getY());

        menuright.setPosition(mCamera.getCenterX() + CAMERA_WIDTH / 2 - menuright.getWidth(), menuright.getY());
        menuleft.setPosition(mCamera.getCenterX() - CAMERA_WIDTH / 2, menuleft.getY());

        if (this.mCamera.getMinX() < 0) {
            this.mCamera.offsetCenter(0, 0);
            mCurrentX = 0;
        }
    }

    @Override
    public void onScrollFinished(final OptiksScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
    }

    @Override
    public void onClick(final ClickDetector pClickDetector, final TouchEvent pTouchEvent) {
        loadLevel(iItemClicked);
    }

    private void CreateMenuBoxes() {

        int spriteX = PADDING;
        final int spriteY = PADDING;

        int iItem = 1;

        for (final TextureRegion column : columns) {

            final int itemToLoad = iItem;

            final Sprite sprite = new Sprite(spriteX, spriteY, column) {

                public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                    iItemClicked = itemToLoad;
                    return false;
                }
            };
            iItem++;

            this.mScene.attachChild(sprite);
            this.mScene.registerTouchArea(sprite);

            spriteX += 20 + PADDING + sprite.getWidth();
        }

        maxX = spriteX - CAMERA_WIDTH;

        final float scrollbarsize = CAMERA_WIDTH / ((maxX + CAMERA_WIDTH) / CAMERA_WIDTH);
        scrollBar = new Rectangle(0, CAMERA_HEIGHT - 20, scrollbarsize, 20);
        scrollBar.setColor(1, 0, 0);
        this.mScene.attachChild(scrollBar);

        menuleft = new Sprite(0, CAMERA_HEIGHT / 2 - leftArrowTextureRegion.getHeight() / 2, leftArrowTextureRegion);
        menuright = new Sprite(CAMERA_WIDTH - rightArrowTextureRegion.getWidth(), CAMERA_HEIGHT / 2 - rightArrowTextureRegion.getHeight() / 2, rightArrowTextureRegion);
        this.mScene.attachChild(menuright);
        menuleft.setVisible(false);
        this.mScene.attachChild(menuleft);
    }

    private void loadLevel(final int iLevel) {
        if (iLevel != -1) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Toast.makeText(SeasonSelectionActivity.this, "Load Item" + String.valueOf(iLevel), Toast.LENGTH_SHORT).show();
                    iItemClicked = -1;
                }
            });
        }
    }
}