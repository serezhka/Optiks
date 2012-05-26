package com.ifmo.optiks.scene;

import android.database.Cursor;
import android.view.KeyEvent;
import com.ifmo.optiks.OptiksActivity;
import com.ifmo.optiks.base.control.OptiksScrollDetector;
import com.ifmo.optiks.base.control.OptiksSurfaceScrollDetector;
import com.ifmo.optiks.base.manager.GameScene;
import com.ifmo.optiks.provider.OptiksProviderMetaData;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.TiledSprite;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.input.touch.detector.ClickDetector;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

import java.util.ArrayList;
import java.util.List;

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

    private final List<TiledSprite> levelBoxes;

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
        levelBoxes = new ArrayList<TiledSprite>();
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
            optiksActivity.setActiveScene(optiksActivity.getScene(OptiksScenes.SEASONS_SCENE));
            return true;
        } else if (pKeyCode == KeyEvent.KEYCODE_MENU && pEvent.getAction() == KeyEvent.ACTION_DOWN) {
            optiksActivity.setActiveScene(optiksActivity.getScene(OptiksScenes.MENU_SCENE));
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

    public void setMaxLevelReached(final int maxLevelReached) {
        this.maxLevelReached = maxLevelReached;
        updateBoxes();
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

        int boxX = LEVEL_PADDING;
        int boxY = LEVEL_PADDING;

        int level = 0;
        for (int i = 0; i < levelPages; i++) {
            final int startX = i * cameraWidth;
            for (int j = 0; j < LEVEL_ROWS_PER_SCREEN; j++) {
                for (int k = 0; k < LEVEL_COLUMNS_PER_SCREEN; k++) {
                    final int levelToLoad = ++level;
                    final TiledTextureRegion textureRegion = optiksActivity.getOptiksTextureManager().getLevelsMenuItemRegion();
                    final TiledSprite box = new TiledSprite(startX + boxX, boxY, 100, 100, textureRegion) {
                        @Override
                        public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX,
                                                     final float pTouchAreaLocalY) {
                            levelClicked = levelToLoad;
                            return false;
                        }
                    };

                    levelBoxes.add(box);
                    this.attachChild(box);
                    this.registerTouchArea(box);

                    boxX += spaceBetweenColumns + LEVEL_PADDING;
                    if (level >= levelsCount) {
                        break;
                    }
                }
                if (level >= levelsCount) {
                    break;
                }
                boxY += spaceBetweenRaws + LEVEL_PADDING;
                boxX = LEVEL_PADDING;
            }
            boxY = LEVEL_PADDING;
        }
        updateBoxes();
    }

    /*private void loadLevel(final int level) {
        if (level != -1) {
            optiksActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //final String json = cursor.getString(idCol);
                    final String json = "[" +
                            "{\"bodyForm\":\"CIRCLE\",\"type\":\"LASER\",\"pX\":650.0,\"pY\":50.0,\"rotation\":0.0,\"height\":70.0,\"width\":70.0}," +
                            "{\"bodyForm\":\"CIRCLE\",\"type\":\"AIM\",\"pX\":460.0,\"pY\":460.0,\"rotation\":0.0,\"height\":70.0,\"width\":70.0}," +
                            "{\"canMove\":false,\"canRotate\":false,\"bodyForm\":\"RECTANGLE\",\"type\":\"MIRROR\",\"pX\":480.0,\"pY\":370.0,\"rotation\":0.0,\"height\":20.0,\"width\":480.0}," +
                            "{\"canMove\":false,\"canRotate\":false,\"bodyForm\":\"RECTANGLE\",\"type\":\"MIRROR\",\"pX\":360.0,\"pY\":470.0,\"rotation\":0.0,\"height\":20.0,\"width\":720.0}," +
                            "{\"canMove\":false,\"canRotate\":false,\"bodyForm\":\"RECTANGLE\",\"type\":\"MIRROR\",\"pX\":10.0,\"pY\":240.0,\"rotation\":90.0,\"height\":20.0,\"width\":480.0}," +
                            "{\"canMove\":false,\"canRotate\":false,\"bodyForm\":\"RECTANGLE\",\"type\":\"MIRROR\",\"pX\":710.0,\"pY\":420.0,\"rotation\":90.0,\"height\":20.0,\"width\":80.0}," +
                            "{\"canMove\":false,\"canRotate\":false,\"bodyForm\":\"RECTANGLE\",\"type\":\"MIRROR\",\"pX\":400.0,\"pY\":435.0,\"rotation\":90.0,\"height\":20.0,\"width\":50.0}," +
                            "]";
                    System.out.println(json);
                    final OptiksScene gameScene = new GameScene(json, optiksActivity, 1, 1, 1);
                    optiksActivity.scenes.put(OptiksScenes.GAME_SCENE, gameScene);
                    optiksActivity.setActiveScene(gameScene);
                }
            });
        }
    }*/

    private int getLevelsCount() {
        final Cursor cursor = optiksActivity.getContentResolver().query(OptiksProviderMetaData.LevelsTable.CONTENT_URI, null,
                OptiksProviderMetaData.LevelsTable.SEASON_ID + "=" + seasonId, null, null);
        return cursor.getCount();
    }

    private int getMaxLevelReached() {
        final Cursor cursor = optiksActivity.getContentResolver().query(OptiksProviderMetaData.SeasonsTable.CONTENT_URI, null,
                OptiksProviderMetaData.SeasonsTable._ID + "=" + seasonId, null, null);
        final int numReached = cursor.getColumnIndex(OptiksProviderMetaData.SeasonsTable.MAX_LEVEL_REACHED);
        cursor.moveToFirst();
        return cursor.getInt(numReached);
    }

    private void updateBoxes() {
        int level = 0;
        for (final TiledSprite levelBox : levelBoxes) {
            levelBox.detachChildren();
            if (++level <= maxLevelReached) {
                levelBox.setCurrentTileIndex(0);
                final int textOffX = (level < 10) ? 28 : 20;
                levelBox.attachChild(new Text(textOffX, 30, optiksActivity.getOptiksTextureManager().menuFont, String.valueOf(level)));
            } else {
                levelBox.setCurrentTileIndex(1);
            }
        }
    }


    private void loadLevel(final int level) {
        final Cursor cursor = optiksActivity.getContentResolver().query(OptiksProviderMetaData.LevelsTable.CONTENT_URI, null,
                OptiksProviderMetaData.LevelsTable.SEASON_ID + "=" + seasonId, null, null);
        final int levelMaxIndex = cursor.getCount();
        cursor.moveToPosition(level - 1);
        final int idCol = cursor.getColumnIndex(OptiksProviderMetaData.LevelsTable.LEVEL);
        final String json = cursor.getString(idCol);
        final OptiksScene gameScene = new GameScene(json, optiksActivity, seasonId, level, levelMaxIndex);
        optiksActivity.putScene(OptiksScenes.GAME_SCENE, gameScene);
        optiksActivity.setActiveScene(gameScene);
    }
}
