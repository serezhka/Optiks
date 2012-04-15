package com.ifmo.optiks.test;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.physics.PhysicsHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 28.03.12
 */

public class MirrorRotationTestActivity extends BaseGameActivity implements Scene.IOnAreaTouchListener {

    private static final int CAMERA_WIDTH = 720;
    private static final int CAMERA_HEIGHT = 480;

    private Camera mCamera;

    private TextureRegion mirrorTextureRegion;

    private TextureRegion rotationBaseTextureRegion;
    private TextureRegion rotationKnobTextureRegion;


    @Override
    public Engine onLoadEngine() {
        this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        return new Engine(new EngineOptions(true, EngineOptions.ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera));
    }

    @Override
    public void onLoadResources() {
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

        final BitmapTextureAtlas mBitmapTextureAtlas = new BitmapTextureAtlas(128, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        this.mirrorTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, this, "mirror.png", 0, 0);

        final BitmapTextureAtlas mOnScreenControlTexture = new BitmapTextureAtlas(256, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        this.rotationBaseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mOnScreenControlTexture, this, "control/onscreen_control_base.png", 0, 0);
        this.rotationKnobTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mOnScreenControlTexture, this, "control/onscreen_control_knob.png", 128, 0);

        this.mEngine.getTextureManager().loadTextures(mBitmapTextureAtlas, mOnScreenControlTexture);
    }

    @Override
    public Scene onLoadScene() {
        this.mEngine.registerUpdateHandler(new FPSLogger());

        final Scene scene = new Scene();
        scene.setBackground(new ColorBackground(0.09804f, 0.6274f, 0.8784f));

        final int centerX = (CAMERA_WIDTH - this.mirrorTextureRegion.getWidth()) / 2;
        final int centerY = (CAMERA_HEIGHT - this.mirrorTextureRegion.getHeight()) / 2;

        final Sprite mirror = new Sprite(centerX, centerY, this.mirrorTextureRegion);
        final Sprite mirror1 = new Sprite(centerX + 100, centerY + 100, this.mirrorTextureRegion);

        mirror.setAlpha(45);
        final PhysicsHandler physicsHandler = new PhysicsHandler(mirror);
        mirror.registerUpdateHandler(physicsHandler);
        mirror1.registerUpdateHandler(physicsHandler);

        scene.attachChild(mirror);
        scene.attachChild(mirror1);

        scene.setOnAreaTouchListener(this);
        scene.registerTouchArea(mirror);
        scene.registerTouchArea(mirror1);

        /* Rotation control */
        final int x = CAMERA_WIDTH - this.rotationBaseTextureRegion.getWidth();
        final int y = CAMERA_HEIGHT - this.rotationBaseTextureRegion.getHeight();
        /*knob.setTarget(mirror);*/
        /*knob.getControlBase().setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        knob.getControlBase().setAlpha(0.5f);
        knob.getControlKnob().setScale(0.5f);

        scene.setChildScene(knob);*/

        return scene;
    }

    @Override
    public void onLoadComplete() {
    }

    @Override
    public boolean onAreaTouched(final TouchEvent touchEvent, final Scene.ITouchArea iTouchArea, final float v, final float v1) {
        final Sprite sprite = (Sprite) iTouchArea;
//        knob.setTarget((Body) sprite.getUserData());
        return true;
    }
}
