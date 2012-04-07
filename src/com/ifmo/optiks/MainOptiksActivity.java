package com.ifmo.optiks;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.ifmo.optiks.base.LevelField;
import com.ifmo.optiks.base.physics.*;
import com.ifmo.optiks.sound.SoundDispatcher;
import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.shape.IShape;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.extension.physics.box2d.util.Vector2Pool;
import org.anddev.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.util.HorizontalAlign;
import org.anddev.andengine.util.MathUtils;

public class MainOptiksActivity extends BaseGameActivity implements Scene.IOnSceneTouchListener, Scene.IOnAreaTouchListener {

    private static final int CAMERA_WIDTH = 720;
    private static final int CAMERA_HEIGHT = 480;

    private String level = "{\"objects\":[" +
            "{\"type\":\"LASER\",\"pY\":2.0,\"rotation\":0.0,\"width\":50,\"height\":50,\"pX\":2.0}," +
            "{\"type\":\"MIRROR\",\"pY\":4.0,\"rotation\":0.0,\"width\":200,\"height\":30,\"pX\":7.0}," +
            "{\"type\":\"MIRROR\",\"pY\":8.0,\"rotation\":90.0,\"width\":100,\"height\":30,\"pX\":15.0}," +
            "{\"type\":\"AIM\",\"pY\":14.0,\"rotation\":0.0,\"width\":50,\"height\":50,\"pX\":21.0}," +
            "{\"type\":\"BARRIER\",\"pY\":5.0,\"rotation\":0.0,\"width\":50,\"height\":50,\"pX\":12.0}" +
            "]}";

    Camera camera;

    private Body groundBody;
    private LaserBullet bullet;

    private LaserBeam laserBeam;
    private LaserSight laserSight;

    private MouseJointOptiks mouseJointOptiks;
    private MouseJoint mouseJoint;

    volatile private boolean bulletIsMoving = false;
    volatile private boolean mirrorIsSelected = false;
    volatile Body mirrorSelectedBody;

    private final Fixtures fixtures = new Fixtures();

    private LevelField field;
    private PhysicsWorld physicsWorld;
    private Text toast;
    private Text numberOfTryToast;
    private Font font;

    private int numberOfTry = 5;
    private Scene scene;

    private SoundDispatcher soundDispatcher;

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
        final EngineOptions engineOptions = new EngineOptions(true, EngineOptions.ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera).setNeedsMusic(true).setNeedsSound(true);
        engineOptions.getTouchOptions().setRunOnUpdateThread(true);
        return new Engine(engineOptions);
    }

    @Override
    public void onLoadResources() {

        field = new LevelField(this);
        field.setFixtures(fixtures);
        final BitmapTextureAtlas atlas = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR);

        soundDispatcher = new SoundDispatcher(this);
        soundDispatcher.playBackGroundMusic();

        font = new Font(atlas, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32, true, Color.BLACK);
        this.mEngine.getTextureManager().loadTexture(atlas);
        this.getFontManager().loadFont(font);
    }

    @Override
    public Scene onLoadScene() {
        this.mEngine.registerUpdateHandler(new FPSLogger());

        final Scene scene = field.onLoadScene(level);
        field.setBackground(new ColorBackground(0.09804f, 0.6274f, 0.8784f));

        physicsWorld = field.getPhysicsWorld();

        this.groundBody = physicsWorld.createBody(new BodyDef());

        scene.setOnAreaTouchListener(this);
        scene.setOnSceneTouchListener(this);

        scene.registerUpdateHandler(this.physicsWorld);

        final Body laser = field.getLaser();

        final float x = laser.getPosition().x * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT;
        final float y = laser.getPosition().y * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT;

        numberOfTryToast = new Text(x, y, font, "" + numberOfTry);
        scene.attachChild(numberOfTryToast);

        bullet = new LaserBullet(physicsWorld, x, y);
        bullet.stop();

        scene.registerUpdateHandler(this.physicsWorld);

        laserSight = new LaserSight(x, y, -1, -1);
        scene.attachChild(laserSight);
        laserSight.setVisible(false);

        laserBeam = new LaserBeam(scene, 0, 1, 0, 3);
        final Context context = this;

        this.physicsWorld.setContactListener(new ContactListener() {
            @Override
            public void beginContact(final Contact contact) {
                if (contact.getFixtureB().getBody() == bullet.getBody()) {

                    final Body bodyA = contact.getFixtureA().getBody();
                    final Vector2 vec = contact.getWorldManifold().getPoints()[0];
                    laserBeam.addLine(x, y, vec.x * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT, vec.y * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT);
                    if (field.getMirrors().contains(bodyA)) {
                    } else {
                        if (field.getWalls().contains(bodyA) || field.getBarriers().contains(bodyA)) {
                            bullet.stop();
                            bulletIsMoving = false;
                            toast = new Text(360, 240, font, "Try again...", HorizontalAlign.CENTER);
                            scene.attachChild(toast);
                        } else if (bodyA == field.getAim()) {
                            bullet.stop();
                            bulletIsMoving = false;
                            toast = new Text(360, 240, font, "Good Shoot!", HorizontalAlign.CENTER);
                            scene.attachChild(toast);
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
        this.scene = scene;
        return this.scene;
    }


    @Override
    public void onLoadComplete() {

    }

    public boolean onSceneTouchEvent(final Scene scene, final TouchEvent touchEvent) {
        if (this.physicsWorld != null) {
            switch (touchEvent.getAction()) {
                case TouchEvent.ACTION_DOWN:
                    if (toast != null) {
                        scene.detachChild(toast);
                        toast = null;
                    }
                    if (mirrorIsSelected) {
                        final float x = mirrorSelectedBody.getPosition().x * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT - touchEvent.getX();
                        final float y = mirrorSelectedBody.getPosition().y * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT - touchEvent.getY();
                        final float angle = MathUtils.radToDeg(MathUtils.atan2(y, x));
                        mirrorSelectedBody.setTransform(mirrorSelectedBody.getPosition(), MathUtils.degToRad(angle));
                    } else {
                        laserSight.setPosition(laserSight.getX1(), laserSight.getY1(), touchEvent.getX(), touchEvent.getY());
                        laserSight.setVisible(true);
                    }
                    return true;
                case TouchEvent.ACTION_MOVE:
                    if (mirrorIsSelected) {
                        final float x = mirrorSelectedBody.getPosition().x * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT - touchEvent.getX();
                        final float y = mirrorSelectedBody.getPosition().y * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT - touchEvent.getY();
                        final float angle = mirrorSelectedBody.getAngle();
                        final float angleTemp = (MathUtils.atan2(y, x) - angle) / 100;
                        for (int i = 0; i <= 100; i++) {
                            mirrorSelectedBody.setTransform(mirrorSelectedBody.getPosition(), angle + angleTemp * i);
                        }
                    } else {
                        if (mouseJoint != null) {
                            final Vector2 vec = Vector2Pool.obtain(touchEvent.getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, touchEvent.getY() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
                            mouseJoint.setTarget(vec);
                            Vector2Pool.recycle(vec);
                        } else {
                            laserSight.setPosition(laserSight.getX1(), laserSight.getY1(), touchEvent.getX(), touchEvent.getY());
                        }
                    }
                    return true;
                case TouchEvent.ACTION_UP:
                    if (mouseJoint != null) {
                        mouseJoint.getBodyB().setType(BodyDef.BodyType.StaticBody);
                        physicsWorld.destroyJoint(mouseJoint);
                        mouseJoint = null;
                        mouseJointOptiks = null;
                    } else {

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
                if (field.getAim().equals(body)) {
                    if (!mirrorIsSelected) {
                        if (bulletIsMoving) {

                        } else {
                            if (numberOfTry > 0) {
                                scene.detachChild(numberOfTryToast);
                                numberOfTryToast = new Text(numberOfTryToast.getX(), numberOfTryToast.getY(), font, "" + --numberOfTry, HorizontalAlign.CENTER);
                                scene.attachChild(numberOfTryToast);

                                soundDispatcher.playPlayShootLaser();
                                bullet.setTransform(Vector2Pool.obtain(field.getLaser().getPosition()));
                                laserBeam.resetBeam();
                                bulletIsMoving = true;
                                final Vector2 vec = Vector2Pool.obtain(10000 * (laserSight.getX2() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT - this.bullet.getPosition().x), 10000 *
                                        (laserSight.getY2() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT - this.bullet.getPosition().y));
                                this.bullet.setLinearVelocity((new Vector2(vec)));
                            } else {
                                toast = new Text(360, 240, font, "Try again...", HorizontalAlign.CENTER);
                                scene.attachChild(toast);
                            }
                        }
                    }
                } else {

                }
                return true;
            case TouchEvent.ACTION_MOVE:
                if (mirrorIsSelected) {

                } else {
                    if (mouseJointOptiks == null && (Body) object.getUserData() != field.getAim()) {
                        mouseJointOptiks = new MouseJointOptiks(object, groundBody, touchAreaLocalX, touchAreaLocalY) {
                            @Override
                            public MouseJoint getMouseJoint() {
                                return (MouseJoint) physicsWorld.createJoint(this);
                            }
                        };
                        mouseJoint = mouseJointOptiks.getMouseJoint();
                    }
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
                Log.d(TAG, "ActionUp");
                if (mouseJoint != null) {
                    mouseJoint.getBodyB().setType(BodyDef.BodyType.StaticBody);
                    physicsWorld.destroyJoint(mouseJoint);
                    mouseJoint = null;
                    mouseJointOptiks = null;
                } else {
                    if (body != field.getAim() && (mirrorSelectedBody == null || mirrorSelectedBody == body)) {
                        if (!mirrorIsSelected) {
                            body.setType(BodyDef.BodyType.DynamicBody);

                            /*RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
                            Body anchorBody = physicsWorld.createBody(new BodyDef());
                            anchorBody.setType(BodyDef.BodyType.StaticBody);
                            anchorBody.setTransform(body.getWorldCenter(), body.getAngle());
                            revoluteJointDef.initialize(anchorBody, body, anchorBody.getWorldCenter());
                            physicsWorld.createJoint(revoluteJointDef);*/

                            mirrorSelectedBody = body;
                            object.setAlpha(0.5f);
                        } else {
                            body.setType(BodyDef.BodyType.StaticBody);
                            mirrorSelectedBody = null;
                            object.setAlpha(1f);
                        }
                        mirrorIsSelected = !mirrorIsSelected;
                    }
                }
                return true;
        }
        return true;
    }
}