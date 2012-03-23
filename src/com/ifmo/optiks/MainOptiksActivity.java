package com.ifmo.optiks;

import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.ifmo.optiks.base.ObjectType;
import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.shape.IShape;
import org.anddev.andengine.entity.shape.Shape;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.extension.physics.box2d.util.Vector2Pool;
import org.anddev.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.sensor.accelerometer.AccelerometerData;
import org.anddev.andengine.sensor.accelerometer.IAccelerometerListener;
import org.anddev.andengine.ui.activity.BaseGameActivity;

public class MainOptiksActivity extends BaseGameActivity implements IAccelerometerListener, Scene.IOnSceneTouchListener, Scene.IOnAreaTouchListener {
    private static final int CAMERA_WIDTH = 720;
    private static final int CAMERA_HEIGHT = 480;

    // ===========================================================
    // Fields
    // ===========================================================

    private Camera camera;
    private Scene scene;

    private BitmapTextureAtlas bitmapTextureAtlas;
    private TextureRegion laserTextureRegion;
    private TextureRegion mirrorTextureRegion;
    private TextureRegion barrierTextureRegion;
    private TextureRegion aimTextureRegion;

    private PhysicsWorld physicsWorld;

    private MouseJoint mouseJointActive;
    private Body groundBody;


    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    @Override
    public Engine onLoadEngine() {
        Toast.makeText(this, "Touch & Drag the mirror!", Toast.LENGTH_LONG).show();
        this.camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        final EngineOptions engineOptions = new EngineOptions(true, EngineOptions.ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.camera);
        engineOptions.getTouchOptions().setRunOnUpdateThread(true);
        return new Engine(engineOptions);
    }

    @Override
    public void onLoadResources() {
        /* Textures. */
        this.bitmapTextureAtlas = new BitmapTextureAtlas(512, 512, TextureOptions.BILINEAR);
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

        /* TextureRegions */
        this.aimTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.bitmapTextureAtlas, this, "aim.png", 0, 200);
        this.laserTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.bitmapTextureAtlas, this, "laser.png", 200, 200);
        this.mirrorTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.bitmapTextureAtlas, this, "mirror.png", 300, 200);
        this.barrierTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.bitmapTextureAtlas, this, "barrier.png", 0, 0);

        this.mEngine.getTextureManager().loadTexture(this.bitmapTextureAtlas);
    }

    @Override
    public Scene onLoadScene() {
        this.mEngine.registerUpdateHandler(new FPSLogger());

        this.scene = new Scene();
        this.scene.setBackground(new ColorBackground(0.09804f, 0.6274f, 0.8784f));

        this.physicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false);
        this.groundBody = this.physicsWorld.createBody(new BodyDef());

        final Shape ground = new Rectangle(0, CAMERA_HEIGHT - 2, CAMERA_WIDTH, 2);
        final Shape roof = new Rectangle(0, 0, CAMERA_WIDTH, 2);
        final Shape left = new Rectangle(0, 0, 2, CAMERA_HEIGHT);
        final Shape right = new Rectangle(CAMERA_WIDTH - 2, 0, 2, CAMERA_HEIGHT);

        final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
        PhysicsFactory.createBoxBody(this.physicsWorld, ground, BodyDef.BodyType.StaticBody, wallFixtureDef);
        PhysicsFactory.createBoxBody(this.physicsWorld, roof, BodyDef.BodyType.StaticBody, wallFixtureDef);
        PhysicsFactory.createBoxBody(this.physicsWorld, left, BodyDef.BodyType.StaticBody, wallFixtureDef);
        PhysicsFactory.createBoxBody(this.physicsWorld, right, BodyDef.BodyType.StaticBody, wallFixtureDef);

        this.scene.attachChild(ground);
        this.scene.attachChild(roof);
        this.scene.attachChild(left);
        this.scene.attachChild(right);

        this.scene.setOnAreaTouchListener(this);
        this.scene.setOnSceneTouchListener(this);

        addObject(100, 100, ObjectType.MIRROR);
        addObject(400, 100, ObjectType.MIRROR);
        addObject(200, 200, ObjectType.BARRIER);
        addObject(200, 300, ObjectType.LASER);
        addObject(500, 350, ObjectType.AIM);

        this.scene.registerUpdateHandler(this.physicsWorld);

        return this.scene;
    }

    @Override
    public void onLoadComplete() {

    }

    public void onAccelerometerChanged(final AccelerometerData pAccelerometerData) {
        final Vector2 gravity = Vector2Pool.obtain(pAccelerometerData.getX(), pAccelerometerData.getY());
        this.physicsWorld.setGravity(gravity);
        Vector2Pool.recycle(gravity);
    }

    @Override
    public void onResumeGame() {
        super.onResumeGame();

        this.enableAccelerometerSensor(this);
    }

    @Override
    public void onPauseGame() {
        super.onPauseGame();

        this.disableAccelerometerSensor();
    }

    public boolean onSceneTouchEvent(final Scene scene, final TouchEvent touchEvent) {
        if(this.physicsWorld != null) {
            switch(touchEvent.getAction()) {
                case TouchEvent.ACTION_DOWN:
                    return true;
                case TouchEvent.ACTION_MOVE:
                    if(this.mouseJointActive != null) {
                        final Vector2 vec = Vector2Pool.obtain(touchEvent.getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, touchEvent.getY() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
                        this.mouseJointActive.setTarget(vec);
                        Vector2Pool.recycle(vec);
                    }
                    return true;
                case TouchEvent.ACTION_UP:
                    if(this.mouseJointActive != null) {
                        this.physicsWorld.destroyJoint(this.mouseJointActive);
                        this.mouseJointActive = null;
                    }
                    return true;
            }
            return false;
        }
        return false;
    }

    public boolean onAreaTouched(final TouchEvent touchEvent, final Scene.ITouchArea touchArea, final float touchAreaLocalX, final float touchAreaLocalY) {
        final IShape object = (IShape) touchArea;
        if (touchEvent.isActionDown()) {
            Log.d("body", "actionDown");
            if (this.mouseJointActive == null) {
                this.mouseJointActive = this.createMouseJoint(object, touchAreaLocalX, touchAreaLocalY);
            }
            return true;
        }
        if (touchEvent.isActionUp()) {
            final Body body = (Body) object.getUserData();
            body.setType(BodyDef.BodyType.StaticBody);
            return true;
        }
        return false;
    }

    public MouseJoint createMouseJoint(final IShape object, final float touchAreaLocalX, final float touchAreaLocalY) {
        Log.d("body", "ok");
        final Body body = (Body) object.getUserData();
        body.setType(BodyDef.BodyType.DynamicBody);
        final MouseJointDef mouseJointDef = new MouseJointDef();

        final Vector2 localPoint = Vector2Pool.obtain((touchAreaLocalX - object.getWidth() * 0.5f) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (touchAreaLocalY - object.getHeight() * 0.5f) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
        this.groundBody.setTransform(localPoint, 0);

        mouseJointDef.bodyA = this.groundBody;
        mouseJointDef.bodyB = body;
        mouseJointDef.dampingRatio = 0.95f;
        mouseJointDef.frequencyHz = 30;
        mouseJointDef.maxForce = (200.0f * body.getMass());
        mouseJointDef.collideConnected = true;

        mouseJointDef.target.set(body.getWorldPoint(localPoint));
        Vector2Pool.recycle(localPoint);

        return (MouseJoint) this.physicsWorld.createJoint(mouseJointDef);
    }

    private void addObject(final int x, final int y, final ObjectType type) {
        final FixtureDef objectFixtureDef = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);

        final Sprite sprite;
        final Body body;

        sprite = getSpriteByObjType(x, y, type);

        switch (type.getBodyType()) {
            case CIRCLE:
                body = PhysicsFactory.createCircleBody(this.physicsWorld, sprite, BodyDef.BodyType.StaticBody, objectFixtureDef);
                break;
            case BOX:
                body = PhysicsFactory.createBoxBody(this.physicsWorld, sprite, BodyDef.BodyType.StaticBody, objectFixtureDef);
                break;
            default:
                body = null;
        }

        sprite.setUserData(body);
        switch (type) {
            case LASER:
                break;
            case MIRROR:
                this.scene.registerTouchArea(sprite);
                break;
            case BARRIER:
                break;
            case AIM:
                break;
        }
        this.scene.attachChild(sprite);

        this.physicsWorld.registerPhysicsConnector(new PhysicsConnector(sprite, body, true, false));
    }

    private Sprite getSpriteByObjType(final int x, final int y, final ObjectType type) {
        final Sprite sprite;
        switch (type) {
            case LASER:
                sprite = new Sprite(x, y, this.laserTextureRegion);
                sprite.setScale((float) 0.5);
                break;
            case MIRROR:
                sprite = new Sprite(x, y, this.mirrorTextureRegion);
                break;
            case BARRIER:
                sprite = new Sprite(x, y, this.barrierTextureRegion);
                sprite.setScale((float) 0.25);
                break;
            case AIM:
                sprite = new Sprite(x, y, this.aimTextureRegion);
                sprite.setScale((float) 0.3);
                break;
            default:
                sprite = null;
                break;
        }
        return sprite;
    }
}