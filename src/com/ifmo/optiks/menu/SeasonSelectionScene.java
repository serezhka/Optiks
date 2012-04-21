package com.ifmo.optiks.menu;

import android.database.Cursor;
import android.widget.Toast;
import com.ifmo.optiks.OptiksActivity;
import com.ifmo.optiks.base.control.OptiksScrollDetector;
import com.ifmo.optiks.base.control.OptiksSurfaceScrollDetector;
import com.ifmo.optiks.provider.OptiksProviderMetaData;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.input.touch.detector.ClickDetector;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 19.04.12
 */

public class SeasonSelectionScene extends Scene implements OptiksScrollDetector.IScrollDetectorListener, Scene.IOnSceneTouchListener, ClickDetector.IClickDetectorListener {

    private final OptiksActivity optiksActivity;
    private final Camera camera;

    private OptiksScrollDetector scrollDetector;
    private ClickDetector clickDetector;

    private final Scene seasonsScene;

    private final List<TextureRegion> seasons = new ArrayList<TextureRegion>();

    private TextureRegion leftArrowTextureRegion;
    private TextureRegion rightArrowTextureRegion;

    private Sprite menuleft;
    private Sprite menuright;

    private Rectangle scrollBar;

    @SuppressWarnings("FieldCanBeLocal")
    private final float minX = 0;
    private float maxX = 0;
    private float mCurrentX = 0;
    private int seasonClicked = -1;

    private final int CAMERA_WIDTH;
    private final int CAMERA_HEIGHT;

    private static final int PADDING = 50;

    public SeasonSelectionScene(final OptiksActivity optiksActivity) {
        super();
        this.optiksActivity = optiksActivity;
        this.camera = optiksActivity.getCamera();
        this.CAMERA_WIDTH = (int) camera.getWidth();
        this.CAMERA_HEIGHT = (int) camera.getHeight();
        seasonsScene = new Scene();
        init();
        this.setBackground(new ColorBackground(0, 0, 0));
        this.setBackgroundEnabled(false);
        this.setChildScene(seasonsScene);
        this.setOnSceneTouchListener(this);
    }

    @Override
    public void onClick(final ClickDetector clickDetector, final TouchEvent touchEvent) {
        loadSeason(seasonClicked);
    }

    @Override
    public boolean onSceneTouchEvent(final Scene scene, final TouchEvent touchEvent) {
        clickDetector.onTouchEvent(touchEvent);
        scrollDetector.onTouchEvent(touchEvent);
        return true;
    }

    @Override
    public void onScrollStarted(final OptiksScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
        scrollBar.setVisible(true);
    }

    @Override
    public void onScroll(final OptiksScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {

        if (camera.getMinX() <= 15) {
            menuleft.setVisible(false);
        } else {
            menuleft.setVisible(true);
        }
        if (camera.getMinX() > maxX - 15) {
            menuright.setVisible(false);
        } else {
            menuright.setVisible(true);
        }

        if (((mCurrentX - pDistanceX) < minX)) {
            return;
        } else if ((mCurrentX - pDistanceX) > maxX) {
            return;
        }

        this.camera.offsetCenter(-pDistanceX, 0);
        mCurrentX -= pDistanceX;

        float tempX = camera.getCenterX() - CAMERA_WIDTH / 2;
        tempX += (tempX / (maxX + CAMERA_WIDTH)) * CAMERA_WIDTH;
        scrollBar.setPosition(tempX, scrollBar.getY());

        menuright.setPosition(camera.getCenterX() + CAMERA_WIDTH / 2 - menuright.getWidth(), menuright.getY());
        menuleft.setPosition(camera.getCenterX() - CAMERA_WIDTH / 2, menuleft.getY());

        if (this.camera.getMinX() < 0) {
            this.camera.offsetCenter(0, 0);
            mCurrentX = 0;
        }
    }

    @Override
    public void onScrollFinished(final OptiksScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
        scrollBar.setVisible(false);
    }

    private void init() {

        final Cursor cursor = optiksActivity.getContentResolver().query(OptiksProviderMetaData.SeasonsTable.CONTENT_URI, null, null, null, null);
        final int nameNum = cursor.getColumnIndex(OptiksProviderMetaData.SeasonsTable.NAME);
        final int descriptionNum = cursor.getColumnIndex(OptiksProviderMetaData.SeasonsTable.DESCRIPTION);
        final int idNum = cursor.getColumnIndex(OptiksProviderMetaData.SeasonsTable._ID);
        for (cursor.moveToFirst(); !cursor.isBeforeFirst(); cursor.moveToNext()) {
            final String name = cursor.getString(nameNum);
            final String description = cursor.getString(descriptionNum);
            final int id = cursor.getInt(idNum);
        }

        final HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet("../Download?getSeasonsCount=1");




        // TODO load existing seasons
        // TODO ask user to check for new seasons, load new seasons from server

        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/season/");

        for (int i = 0; i < 4; i++) {
            final BitmapTextureAtlas mMenuBitmapTextureAtlas = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
            final TextureRegion mMenuTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mMenuBitmapTextureAtlas, optiksActivity, "season.png", 0, 0);
            optiksActivity.getEngine().getTextureManager().loadTexture(mMenuBitmapTextureAtlas);
            seasons.add(mMenuTextureRegion);
        }

        final BitmapTextureAtlas menuTextureAtlas = new BitmapTextureAtlas(512, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        this.leftArrowTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, optiksActivity, "arrow_left.png", 0, 0);
        this.rightArrowTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, optiksActivity, "arrow_right.png", 256, 0);
        optiksActivity.getEngine().getTextureManager().loadTexture(menuTextureAtlas);

        createSeasonBoxes();

        scrollDetector = new OptiksSurfaceScrollDetector(this);
        clickDetector = new ClickDetector(this);
    }

    private void createSeasonBoxes() {

        int spriteX = PADDING;
        final int spriteY = PADDING;

        int iItem = 1;

        for (final TextureRegion season : seasons) {

            final int itemToLoad = iItem;

            final Sprite sprite = new Sprite(spriteX, spriteY, season) {
                public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                    seasonClicked = itemToLoad;
                    return false;
                }
            };
            iItem++;

            this.seasonsScene.attachChild(sprite);
            this.seasonsScene.registerTouchArea(sprite);

            spriteX += 20 + PADDING + sprite.getWidth();
        }

        maxX = spriteX - CAMERA_WIDTH;

        final float scrollbarsize = CAMERA_WIDTH / ((maxX + CAMERA_WIDTH) / CAMERA_WIDTH);
        scrollBar = new Rectangle(0, CAMERA_HEIGHT - 20, scrollbarsize, 20);
        scrollBar.setColor(1, 0, 0);
        scrollBar.setVisible(false);
        this.seasonsScene.attachChild(scrollBar);

        menuleft = new Sprite(0, CAMERA_HEIGHT / 2 - leftArrowTextureRegion.getHeight() / 2, leftArrowTextureRegion);
        menuright = new Sprite(CAMERA_WIDTH - rightArrowTextureRegion.getWidth(), CAMERA_HEIGHT / 2 - rightArrowTextureRegion.getHeight() / 2, rightArrowTextureRegion);
        this.seasonsScene.attachChild(menuright);
        menuleft.setVisible(false);
        this.seasonsScene.attachChild(menuleft);
    }

    private void loadSeason(final int seasonNumber) {
        if (seasonNumber != -1) {
            optiksActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(optiksActivity, "Load Season" + String.valueOf(seasonNumber), Toast.LENGTH_SHORT).show();
                    seasonClicked = -1;
                }
            });
        }
    }
}
