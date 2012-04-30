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

public class OptiksLevelsScene extends OptiksScene implements OptiksScrollDetector.IScrollDetectorListener, ClickDetector.IClickDetectorListener, Scene.IOnSceneTouchListener {

    private final static int LEVEL_ROWS_PER_SCREEN = 3;
    private final static int LEVEL_COLUMNS_PER_SCREEN = 5;

    private final static int LEVEL_PADDING = 30;
    private final static int TURN_PAGE_DISTANCE = 150;

    private final OptiksActivity optiksActivity;

    private final Camera camera;

    private final OptiksSurfaceScrollDetector scrollDetector;
    private final ClickDetector clickDetector;

    private final TextureRegion reachedLevel;
    private final TextureRegion unReachedLevel;

    private final int cameraWidth;
    private final int cameraHeight;

    private final int levelsCount;
    private final int maxLevelReached;
    private final int levelPages;

    private float distanceX;
    private int page = 0;
    private int levelClicked = -1;

    public OptiksLevelsScene(final OptiksActivity optiksActivity) {
        super();
        this.optiksActivity = optiksActivity;
        this.camera = optiksActivity.getCamera();
        cameraWidth = (int) optiksActivity.getCamera().getWidth();
        cameraHeight = (int) optiksActivity.getCamera().getHeight();
        // TODO assign levels count and max level reached
        levelsCount = 10;
        maxLevelReached = 5;
        levelPages = (levelsCount % (LEVEL_COLUMNS_PER_SCREEN * LEVEL_ROWS_PER_SCREEN)) == 0 ?
                (levelsCount / (LEVEL_COLUMNS_PER_SCREEN * LEVEL_ROWS_PER_SCREEN)) : (levelsCount / (LEVEL_COLUMNS_PER_SCREEN * LEVEL_ROWS_PER_SCREEN)) + 1;
        reachedLevel = optiksActivity.getOptiksTextureManager().levelsMenuStar;
        unReachedLevel = optiksActivity.getOptiksTextureManager().levelsMenuQuestion;
        createLevelBoxes();

        /* Click and Scroll detector */
        clickDetector = new ClickDetector(this);
        scrollDetector = new OptiksSurfaceScrollDetector(this);

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
        clickDetector.onTouchEvent(touchEvent);
        scrollDetector.onTouchEvent(touchEvent);
        return true;
    }

    @Override
    public void onClick(final ClickDetector clickDetector, final TouchEvent touchEvent) {
        loadLevel(levelClicked);
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
            camera.offsetCenter(distanceX - cameraWidth, 0);
        } else if ((distanceX < -TURN_PAGE_DISTANCE) && (page < levelPages - 1)) {
            page++;
            camera.offsetCenter(distanceX + cameraWidth, 0);
        } else {
            camera.offsetCenter(distanceX, 0);
        }
        // TODO if last level page is turned -> show new season's levels
    }

    private void createLevelBoxes() {

        final int spaceBetweenRaws = (cameraHeight / LEVEL_ROWS_PER_SCREEN) - LEVEL_PADDING;
        final int spaceBetweenColumns = (cameraWidth / LEVEL_COLUMNS_PER_SCREEN) - LEVEL_PADDING;

        int boxX = LEVEL_PADDING;
        int boxY = LEVEL_PADDING;

        int level = 0;
        for (int i = 0; i < levelPages; i++) {
            final int startX = i * cameraWidth;
            for (int j = 0; j < LEVEL_ROWS_PER_SCREEN; j++) {
                for (int k = 0; k < LEVEL_COLUMNS_PER_SCREEN; k++) {
                    final int levelToLoad = level;
                    final Sprite box;
                    if (level <= maxLevelReached) {
                        box = new Sprite(startX + boxX, boxY, 100, 100, reachedLevel) {
                            @Override
                            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX,
                                                         final float pTouchAreaLocalY) {
                                levelClicked = levelToLoad;
                                return true;
                            }
                        };
                    } else {
                        box = new Sprite(startX + boxX, boxY, 100, 100, unReachedLevel) {
                            @Override
                            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX,
                                                         final float pTouchAreaLocalY) {
                                optiksActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(optiksActivity, "It's not available! :)", Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                });
                                return true;
                            }
                        };
                    }

                    this.attachChild(box);
                    this.registerTouchArea(box);
                    int textOffX = 20;
                    if (level < 10) {
                        textOffX = 28;
                    }
                    box.attachChild(new Text(textOffX, 30, optiksActivity.getOptiksTextureManager().menuFont, String.valueOf(level + 1)));

                    level++;
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

    private void loadLevel(final int level) {
        if (level != -1) {
            optiksActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(optiksActivity, "Loading the " + (level + 1) + " level!", Toast.LENGTH_SHORT)
                            .show();
                }
            });
        }
    }
}
