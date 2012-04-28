package com.ifmo.optiks.scene;

import android.view.KeyEvent;
import android.widget.Toast;
import com.ifmo.optiks.OptiksActivity;
import com.ifmo.optiks.base.control.OptiksScrollDetector;
import com.ifmo.optiks.base.control.OptiksSurfaceScrollDetector;
import com.ifmo.optiks.menu.SeasonMenuItem;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.input.touch.detector.ClickDetector;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 19.04.12
 */

public class OptiksSeasonsScene extends OptiksScene implements OptiksScrollDetector.IScrollDetectorListener, Scene.IOnSceneTouchListener, ClickDetector.IClickDetectorListener {

    private final OptiksActivity optiksActivity;
    private final Camera camera;

    private OptiksScrollDetector scrollDetector;
    private ClickDetector clickDetector;

    private final Scene seasonsScene;

    private final List<SeasonMenuItem> seasons = new ArrayList<SeasonMenuItem>();

    private Sprite menuleft;
    private Sprite menuright;

    private Rectangle scrollBar;

    @SuppressWarnings("FieldCanBeLocal")
    private float maxX = 0;
    private int seasonClicked = -1;
    private int page = 0;
    private float distanceX;

    private final int CAMERA_WIDTH;
    private final int CAMERA_HEIGHT;

    private final static int TURN_PAGE_DISTANCE = 150;

    public OptiksSeasonsScene(final OptiksActivity optiksActivity) {
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

        // TODO fix this stub
        optiksActivity.getEngine().registerUpdateHandler(new TimerHandler(0.5f, new ITimerCallback() {
            @Override
            public void onTimePassed(final TimerHandler pTimerHandler) {
                optiksActivity.getEngine().unregisterUpdateHandler(pTimerHandler);
                OptiksSeasonsScene.this.setOnSceneTouchListener(OptiksSeasonsScene.this);

            }
        }));
    }

    @Override
    public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
        if (pKeyCode == KeyEvent.KEYCODE_BACK && pEvent.getAction() == KeyEvent.ACTION_DOWN) {
            optiksActivity.setActiveScene(optiksActivity.scenes.get(OptiksScenes.MENU_SCENE));
            return true;
        } else if (pKeyCode == KeyEvent.KEYCODE_MENU && pEvent.getAction() == KeyEvent.ACTION_DOWN) {
            optiksActivity.setActiveScene(optiksActivity.scenes.get(OptiksScenes.MENU_SCENE));
            return true;
        }
        return false;
    }

    @Override
    public void setEnabled(final boolean enabled) {
        if (enabled) {
            this.setIgnoreUpdate(false);
            this.setVisible(true);
        } else {
            this.setIgnoreUpdate(true);
            this.setVisible(false);
        }
        page = 0;
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
        distanceX = 0;
        menuleft.setVisible(false);
        // TODO replace "setVisible" with alpha modifiers
        //menuleft.registerEntityModifier(new AlphaModifier(0.5f, 1f, 0f));
        menuright.setVisible(false);
        scrollBar.setVisible(true);
    }

    @Override
    public void onScroll(final OptiksScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
        if (page == 0 && pDistanceX >= 0) {
            return;
        } else if (page == seasons.size() - 1 && pDistanceX <= 0) {
            return;
        }

        camera.offsetCenter(-pDistanceX, 0);
        distanceX += pDistanceX;

        if (this.camera.getMinX() < 0) {
            this.camera.offsetCenter(0, 0);
            distanceX = 0;
        }

        float tempX = camera.getCenterX() - CAMERA_WIDTH / 2;
        tempX += (tempX / (maxX + CAMERA_WIDTH)) * CAMERA_WIDTH;
        scrollBar.setPosition(tempX, scrollBar.getY());

        menuright.setPosition(camera.getCenterX() + CAMERA_WIDTH / 2 - menuright.getWidth(), menuright.getY());
        menuleft.setPosition(camera.getCenterX() - CAMERA_WIDTH / 2, menuleft.getY());
    }

    @Override
    public void onScrollFinished(final OptiksScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
        if ((distanceX > TURN_PAGE_DISTANCE) && (page > 0)) {
            page--;
            camera.offsetCenter(distanceX - CAMERA_WIDTH, 0);
        } else if ((distanceX < -TURN_PAGE_DISTANCE) && (page < seasons.size() - 1)) {
            page++;
            camera.offsetCenter(distanceX + CAMERA_WIDTH, 0);
        } else {
            camera.offsetCenter(distanceX, 0);
        }

        float tempX = camera.getCenterX() - CAMERA_WIDTH / 2;
        tempX += (tempX / (maxX + CAMERA_WIDTH)) * CAMERA_WIDTH;
        scrollBar.setPosition(tempX, scrollBar.getY());

        menuright.setPosition(camera.getCenterX() + CAMERA_WIDTH / 2 - menuright.getWidth(), menuright.getY());
        menuleft.setPosition(camera.getCenterX() - CAMERA_WIDTH / 2, menuleft.getY());
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
        scrollBar.setVisible(false);
    }

    private void init() {

        // TODO load existing seasons
        // TODO ask user to check for new seasons, load new seasons from server

        /*final Cursor cursor = optiksActivity.getContentResolver().query(OptiksProviderMetaData.SeasonsTable.CONTENT_URI, null, null, null, null);
        final int nameNum = cursor.getColumnIndex(OptiksProviderMetaData.SeasonsTable.NAME);
        final int descriptionNum = cursor.getColumnIndex(OptiksProviderMetaData.SeasonsTable.DESCRIPTION);
        final int idNum = cursor.getColumnIndex(OptiksProviderMetaData.SeasonsTable._ID);
        for (cursor.moveToFirst(); !cursor.isBeforeFirst(); cursor.moveToNext()) {
            final String name = cursor.getString(nameNum);
            final String description = cursor.getString(descriptionNum);
            final int id = cursor.getInt(idNum);
        }

        final HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet("../Download?getSeasonsCount=1");*/

        seasons.add(new SeasonMenuItem(1, "Season One", "Description"));
        seasons.add(new SeasonMenuItem(2, "Season Two", "Description"));
        seasons.add(new SeasonMenuItem(3, "Season Three", "Description"));

        int seasonNumber = 0;
        int spriteX = 0;
        for (final SeasonMenuItem season : seasons) {
            spriteX = seasonNumber * (int) camera.getWidth();
            final int seasonToLoad = ++seasonNumber;
            // TODO this is stub: get(0) -> to load default picture
            final TextureRegion region = optiksActivity.getOptiksTextureManager().seasons.get(0);
            final Sprite sprite = new Sprite(spriteX, 0, region) {
                public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                    seasonClicked = seasonToLoad;
                    return false;
                }
            };
            sprite.setSize(camera.getWidth(), camera.getHeight());
            sprite.attachChild(new Text(sprite.getWidth() / 2, 20, optiksActivity.getOptiksTextureManager().font, season.getName()));
            this.seasonsScene.attachChild(sprite);
            this.seasonsScene.registerTouchArea(sprite);
        }

        maxX = spriteX;

        // TODO implement scroll bar as mini seasons preview bar
        final float scrollbarsize = CAMERA_WIDTH / ((maxX + CAMERA_WIDTH) / CAMERA_WIDTH);
        scrollBar = new Rectangle(0, CAMERA_HEIGHT - 20, scrollbarsize, 20);
        scrollBar.setColor(1, 0, 0);
        scrollBar.setVisible(false);
        this.seasonsScene.attachChild(scrollBar);

        final TextureRegion leftArrowTextureRegion = optiksActivity.getOptiksTextureManager().leftArrowTextureRegion;
        final TextureRegion rightArrowTextureRegion = optiksActivity.getOptiksTextureManager().rightArrowTextureRegion;

        menuleft = new Sprite(0, CAMERA_HEIGHT / 2 - leftArrowTextureRegion.getHeight() / 2, leftArrowTextureRegion);
        menuright = new Sprite(CAMERA_WIDTH - rightArrowTextureRegion.getWidth(), CAMERA_HEIGHT / 2 - rightArrowTextureRegion.getHeight() / 2, rightArrowTextureRegion);
        this.seasonsScene.attachChild(menuright);
        menuleft.setVisible(false);
        this.seasonsScene.attachChild(menuleft);

        scrollDetector = new OptiksSurfaceScrollDetector(this);
        clickDetector = new ClickDetector(this);
    }

    private void loadSeason(final int seasonNumber) {
        if (seasonNumber > 0) {
            optiksActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(optiksActivity, "Load Season" + String.valueOf(seasonNumber), Toast.LENGTH_SHORT).show();
                    seasonClicked = -1;
                    optiksActivity.setActiveScene(new OptiksLevelsScene(optiksActivity));
                }
            });
        }
    }
}
