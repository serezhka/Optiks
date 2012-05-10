package com.ifmo.optiks.scene;

import android.view.KeyEvent;
import android.widget.Toast;
import com.ifmo.optiks.OptiksActivity;
import com.ifmo.optiks.base.control.OptiksScrollDetector;
import com.ifmo.optiks.base.control.OptiksSurfaceScrollDetector;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.input.touch.detector.ClickDetector;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 22.04.12
 */

public class OptiksLevelsScene extends OptiksScene implements OptiksScrollDetector.IScrollDetectorListener, Scene.IOnSceneTouchListener, ClickDetector.IClickDetectorListener {

    private final static int LEVEL_ROWS_PER_SCREEN = 3;
    private final static int LEVEL_COLUMNS_PER_SCREEN = 5;

    private final static int LEVEL_PADDING = 30;
    private final static int TURN_PAGE_DISTANCE = 150;

    private final OptiksActivity optiksActivity;

    private final Camera camera;

    private final OptiksSurfaceScrollDetector scrollDetector;
    private final ClickDetector clickDetector;

    private final int cameraWidth;
    private final int cameraHeight;

    private final int seasonId;

    private float distanceX;
    private int page = 0;
    private int levelPages;
    private int maxLevelReached;
    private int levelClicked = -1;

    public OptiksLevelsScene(final int seasonId, final OptiksActivity optiksActivity) {
        super();
        this.seasonId = seasonId;
        this.optiksActivity = optiksActivity;
        camera = optiksActivity.getCamera();
        cameraWidth = (int) optiksActivity.getCamera().getWidth();
        cameraHeight = (int) optiksActivity.getCamera().getHeight();

        createLevelBoxes();

        /* Scroll and Click detector */
        scrollDetector = new OptiksSurfaceScrollDetector(this);
        clickDetector = new ClickDetector(this);

        // TODO fix this stub
        optiksActivity.getEngine().registerUpdateHandler(new TimerHandler(0.5f, new ITimerCallback() {
            @Override
            public void onTimePassed(final TimerHandler pTimerHandler) {
                optiksActivity.getEngine().unregisterUpdateHandler(pTimerHandler);
                OptiksLevelsScene.this.setOnSceneTouchListener(OptiksLevelsScene.this);
            }
        }));
    }

    @Override
    public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
        if (pKeyCode == KeyEvent.KEYCODE_BACK && pEvent.getAction() == KeyEvent.ACTION_DOWN) {
            optiksActivity.setActiveScene(optiksActivity.scenes.get(OptiksScenes.SEASONS_SCENE));
            return true;
        } else if (pKeyCode == KeyEvent.KEYCODE_MENU && pEvent.getAction() == KeyEvent.ACTION_DOWN) {
            optiksActivity.setActiveScene(optiksActivity.scenes.get(OptiksScenes.MENU_SCENE));
            return true;
        }
        return false;
    }

    @Override
    public boolean onSceneTouchEvent(final Scene scene, final TouchEvent touchEvent) {
        return scrollDetector.onTouchEvent(touchEvent) || clickDetector.onTouchEvent(touchEvent);
    }

    @Override
    public void onClick(final ClickDetector clickDetector, final TouchEvent touchEvent) {
        if (levelClicked > 0 && levelClicked <= maxLevelReached) {
            loadLevel(levelClicked);
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
            slideCamera(distanceX - cameraWidth, 0);
        } else if ((distanceX < -TURN_PAGE_DISTANCE) && (page < levelPages - 1)) {
            page++;
            slideCamera(distanceX + cameraWidth, 0);
        } else {
            slideCamera(distanceX, 0);
        }
    }

    private void slideCamera(final float ofsetX, final float ofsetY) {
        slideCamera((int) ofsetX, (int) ofsetY);
    }

    private void slideCamera(final int ofsetX, final int ofsetY) {
        optiksActivity.runOnUiThread(new Runnable() {

            int x = 0;
            int y = 0;
            int flag = 0;

            @Override
            public void run() {
                while (x != ofsetX || y != ofsetY) {
                    /* Slide oX */
                    if (x != ofsetX) {
                        camera.offsetCenter(Math.signum(ofsetX), 0);
                        x += Math.signum(ofsetX);
                    }
                    /* Slide oY */
                    if (y != ofsetY) {
                        camera.offsetCenter(0, Math.signum(ofsetY));
                        y += Math.signum(ofsetY);
                    }
                    try {
                        if (flag++ > 3) {
                            Thread.sleep(1);
                            flag = 0;
                        }
                    } catch (InterruptedException e) {
                        // do nothing
                    }
                }
            }
        });
    }

    private void createLevelBoxes() {

        final int levelsCount = getLevelsCount();
        maxLevelReached = getMaxLevelReached();
        levelPages = (levelsCount % (LEVEL_COLUMNS_PER_SCREEN * LEVEL_ROWS_PER_SCREEN)) == 0 ?
                (levelsCount / (LEVEL_COLUMNS_PER_SCREEN * LEVEL_ROWS_PER_SCREEN)) : (levelsCount / (LEVEL_COLUMNS_PER_SCREEN * LEVEL_ROWS_PER_SCREEN)) + 1;

        final int spaceBetweenRaws = (cameraHeight / LEVEL_ROWS_PER_SCREEN) - LEVEL_PADDING;
        final int spaceBetweenColumns = (cameraWidth / LEVEL_COLUMNS_PER_SCREEN) - LEVEL_PADDING;

        final TextureRegion reachedLevel = optiksActivity.getOptiksTextureManager().levelsMenuStar;
        final TextureRegion unReachedLevel = optiksActivity.getOptiksTextureManager().levelsMenuQuestion;

        int boxX = LEVEL_PADDING;
        int boxY = LEVEL_PADDING;

        int level = 0;
        for (int i = 0; i < levelPages; i++) {
            final int startX = i * cameraWidth;
            for (int j = 0; j < LEVEL_ROWS_PER_SCREEN; j++) {
                for (int k = 0; k < LEVEL_COLUMNS_PER_SCREEN; k++) {
                    final int levelToLoad = ++level;
                    final TextureRegion textureRegion = (level <= maxLevelReached) ? reachedLevel : unReachedLevel;
                    final Sprite box = new Sprite(startX + boxX, boxY, 100, 100, textureRegion) {
                        @Override
                        public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX,
                                                     final float pTouchAreaLocalY) {
                            levelClicked = levelToLoad;
                            return false;
                        }
                    };

                    this.attachChild(box);
                    this.registerTouchArea(box);
                    int textOffX = 20;
                    if (level < 10) {
                        textOffX = 28;
                    }
                    box.attachChild(new Text(textOffX, 30, optiksActivity.getOptiksTextureManager().menuFont, String.valueOf(level)));

                    boxX += spaceBetweenColumns + LEVEL_PADDING;
                    if (level > levelsCount) {
                        break;
                    }
                }
                if (level > levelsCount) {
                    break;
                }
                boxY += spaceBetweenRaws + LEVEL_PADDING;
                boxX = LEVEL_PADDING;
            }
            boxY = LEVEL_PADDING;
        }
    }

    private int getLevelsCount() {
        // TODO check this commented code
        /*final Cursor cursor = optiksActivity.getContentResolver().query(OptiksProviderMetaData.LevelsTable.CONTENT_URI, null,
                OptiksProviderMetaData.LevelsTable.SEASON_ID + "=" + seasonId, null, null);
        return cursor.getCount();*/
        return 5;
    }

    private int getMaxLevelReached() {
        // TODO implement this
        return 3;
    }

    private void loadLevel(final int level) {
        optiksActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(optiksActivity, "Loading the " + (level) + " level!", Toast.LENGTH_SHORT)
                        .show();
                // TODO load game scene
                /*final Cursor cursor = optiksActivity.getContentResolver().query(OptiksProviderMetaData.LevelsTable.CONTENT_URI, null,
                        "(" + OptiksProviderMetaData.LevelsTable.SEASON_ID + "=" + seasonId + ") AND(" + OptiksProviderMetaData.LevelsTable.LEVEL_ID + "=" + level + ")"
                        , null, null);
                cursor.moveToFirst();
                if (cursor.getCount() == 1) {
                    final int idCol = cursor.getColumnIndex(OptiksProviderMetaData.LevelsTable.LEVEL);
                    final String json = cursor.getString(idCol);
                    final OptiksScene gameScene = new GameScene(json, optiksActivity);
                    optiksActivity.scenes.put(OptiksScenes.GAME_SCENE, gameScene);
                    optiksActivity.setActiveScene(gameScene);
                }*/
            }
        });
    }
}
