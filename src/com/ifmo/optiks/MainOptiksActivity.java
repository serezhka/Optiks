package com.ifmo.optiks;

import android.util.Log;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.ifmo.optiks.base.LevelField;
import com.ifmo.optiks.base.physics.Fixtures;
import com.ifmo.optiks.base.physics.LaserBeam;
import com.ifmo.optiks.base.physics.LaserBullet;
import com.ifmo.optiks.base.physics.MouseJointOptiks;
import com.ifmo.optiks.base.sprite.GameSprite;
import com.ifmo.optiks.control.KnobRotationControl;
import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.shape.IShape;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.extension.physics.box2d.util.Vector2Pool;
import org.anddev.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import javax.microedition.khronos.opengles.GL10;

public class MainOptiksActivity extends BaseGameActivity implements Scene.IOnSceneTouchListener, Scene.IOnAreaTouchListener {

    private static final int CAMERA_WIDTH = 720;
    private static final int CAMERA_HEIGHT = 480;

    private String level = "{\"objects\":[" +
            "{\"type\":\"LASER\",\"pY\":2.0,\"rotation\":0.0,\"width\":50,\"height\":50,\"pX\":2.0}," +
            "{\"type\":\"MIRROR\",\"pY\":4.0,\"rotation\":0.0,\"width\":50,\"height\":50,\"pX\":7.0}," +
            "{\"type\":\"MIRROR\",\"pY\":8.0,\"rotation\":30.0,\"width\":100,\"height\":70,\"pX\":15.0}," +
            "{\"type\":\"AIM\",\"pY\":14.0,\"rotation\":0.0,\"width\":50,\"height\":50,\"pX\":21.0}," +
            "{\"type\":\"BARRIER\",\"pY\":5.0,\"rotation\":0.0,\"width\":50,\"height\":50,\"pX\":12.0}" +
            "]}";

    Camera camera;

    private Body groundBody;
    private LaserBullet bullet;

    private LaserBeam laserBeam;

    private MouseJointOptiks mouseJointOptiks;
    private MouseJoint mouseJoint;

    volatile private boolean bulletIsMoving = false;
    volatile private boolean bulletIsOnEndPosition = false;

    final private Fixtures fixtures = new Fixtures();

    private LevelField field;
    private PhysicsWorld physicsWorld;

    private TextureRegion rotationBaseTextureRegion;
    private TextureRegion rotationKnobTextureRegion;

    private KnobRotationControl knob;

    final static private String TAG = "MainOptiksActivityTAG";

    //@Override
    /* protected void onCreate(final Bundle pSavedInstanceState) {

        super.onCreate(pSavedInstanceState);
        //извлекаем параметры запуска
        final int id = getIntent().getExtras().getInt(OptiksProviderMetaData.LevelTable._ID);
        //запрос к базе
        final Cursor cursor = managedQuery(OptiksProviderMetaData.LevelTable.CONTENT_URI,null,"_id=?", new String[]{id + ""},null);
        cursor.moveToFirst();

        final int idValue = cursor.getColumnIndex(OptiksProviderMetaData.LevelTable.VALUE);
        level = cursor.getString(idValue);
    }*/

    public Engine onLoadEngine() {
        camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        final EngineOptions engineOptions = new EngineOptions(true, EngineOptions.ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
        engineOptions.getTouchOptions().setRunOnUpdateThread(true);
        return new Engine(engineOptions);
    }

    @Override
    public void onLoadResources() {
        field = new LevelField(this);
        field.setFixtures(fixtures);
        final BitmapTextureAtlas mOnScreenControlTexture = new BitmapTextureAtlas(256, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        this.rotationBaseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mOnScreenControlTexture, this, "control/onscreen_control_base.png", 0, 0);
        this.rotationKnobTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mOnScreenControlTexture, this, "control/onscreen_control_knob.png", 128, 0);
        this.mEngine.getTextureManager().loadTextures(mOnScreenControlTexture);
    }

    @Override
    public Scene onLoadScene() {
        this.mEngine.registerUpdateHandler(new FPSLogger());

        final Scene scene = field.onLoadScene(level);
        field.setBackground(new ColorBackground(0.09804f, 0.6274f, 0.8784f));

        knob = new KnobRotationControl(50, 50, camera, this.rotationBaseTextureRegion, this.rotationKnobTextureRegion, 0.1f);
        knob.getControlBase().setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        knob.getControlBase().setAlpha(0.5f);
        knob.getControlKnob().setScale(0.5f);

        scene.setChildScene(knob);

        physicsWorld = field.getPhysicsWorld();

        this.groundBody = physicsWorld.createBody(new BodyDef());

        scene.setOnAreaTouchListener(this);
        scene.setOnSceneTouchListener(this);

        scene.registerUpdateHandler(this.physicsWorld);

        final Body laser = field.getLaser();

        final float x = laser.getPosition().x * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT;
        final float y = laser.getPosition().y * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT;

        bullet = new LaserBullet(physicsWorld, x, y);
        bullet.stop();

        scene.registerUpdateHandler(this.physicsWorld);

        laserBeam = new LaserBeam(scene, 0, 1, 0, 3);

        this.physicsWorld.setContactListener(new ContactListener() {
            @Override
            public void beginContact(final Contact contact) {
                if (contact.getFixtureB().getBody() == bullet.getBody()) {

                    final Body bodyA = contact.getFixtureA().getBody();
                    final Vector2 vec = contact.getWorldManifold().getPoints()[0];
                    laserBeam.addLine(x, y, vec.x * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT, vec.y * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT);
                    final GameSprite sprite = (GameSprite) bodyA.getUserData();
                    if (field.getMirrors().contains(bodyA)) {
                        Log.d("bullet", "mirror");
                    } else {
                        if (field.getWalls().contains(bodyA) || field.getBarriers().contains(bodyA)) {
                            bullet.stop();
                            bulletIsMoving = false;
                            bulletIsOnEndPosition = true;

                            Log.d("bullet", "wall");
                        } else if (bodyA == field.getAim()) {
                            bullet.stop();
                            bulletIsMoving = false;
                            bulletIsOnEndPosition = true;

                            Log.d("bullet", "aim");
                        }
                    }
                }
            }

            @Override
            public void endContact(final Contact contact) {

            }

            @Override
            public void preSolve(final Contact contact, final Manifold manifold) {

            }

            @Override
            public void postSolve(final Contact contact, final ContactImpulse contactImpulse) {

            }
        });
        return scene;
    }

    @Override
    public void onLoadComplete() {

    }

    public boolean onSceneTouchEvent(final Scene scene, final TouchEvent touchEvent) {
        if (this.physicsWorld != null) {
            switch (touchEvent.getAction()) {
                case TouchEvent.ACTION_DOWN:
                    Log.d("error!!!", "fixtureError");

                    if (bulletIsMoving) {

                    } else {
                        if (bulletIsOnEndPosition) {
                            bulletIsOnEndPosition = false;

                            laserBeam.resetBeam();
                            final Body body = (Body) field.getLaser();
                            bullet.setTransform(Vector2Pool.obtain(body.getPosition()));
                        } else {
                            bulletIsMoving = true;
                            final Vector2 vec = Vector2Pool.obtain(10000 * (touchEvent.getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT - this.bullet.getPosition().x), 10000 *
                                    (touchEvent.getY() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT - this.bullet.getPosition().y));
                            this.bullet.setLinearVelocity((new Vector2(vec)));
                        }
                    }
                    return true;
                case TouchEvent.ACTION_MOVE:
                    if (mouseJoint != null) {
                        final Vector2 vec = Vector2Pool.obtain(touchEvent.getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, touchEvent.getY() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
                        mouseJoint.setTarget(vec);
                        Vector2Pool.recycle(vec);
                    }
                    return true;
                case TouchEvent.ACTION_UP:
                    if (mouseJoint != null) {
                        mouseJoint.getBodyB().setType(BodyDef.BodyType.StaticBody);
                        physicsWorld.destroyJoint(mouseJoint);
                        mouseJoint = null;
                        mouseJointOptiks = null;
                    }
                    return true;
            }
            return false;
        }
        return false;
    }

    public boolean onAreaTouched(final TouchEvent touchEvent, final Scene.ITouchArea touchArea, final float touchAreaLocalX, final float touchAreaLocalY) {
        final IShape object = (Sprite) touchArea;
        final Body body = (Body) object.getUserData();
        switch (touchEvent.getAction()) {
            case TouchEvent.ACTION_DOWN:
                knob.setTarget(body);
                return true;
            case TouchEvent.ACTION_MOVE:
                knob.setTarget(body);
                if (mouseJointOptiks == null) {
                    mouseJointOptiks = new MouseJointOptiks(object, groundBody, touchAreaLocalX, touchAreaLocalY) {
                        @Override
                        public MouseJoint getMouseJoint() {
                            return (MouseJoint) physicsWorld.createJoint(this);
                        }
                    };
                    mouseJoint = mouseJointOptiks.getMouseJoint();
                }
                return true;
            case TouchEvent.ACTION_OUTSIDE:
                if (mouseJoint != null) {
                    mouseJoint.getBodyB().setType(BodyDef.BodyType.StaticBody);
                    physicsWorld.destroyJoint(mouseJoint);
                    mouseJoint = null;
                    mouseJointOptiks = null;
                }
                return true;
            case TouchEvent.ACTION_UP:
                if (mouseJoint != null) {
                    mouseJoint.getBodyB().setType(BodyDef.BodyType.StaticBody);
                    physicsWorld.destroyJoint(mouseJoint);
                    mouseJoint = null;
                    mouseJointOptiks = null;
                }
                return true;
        }
        return true;
    }
}