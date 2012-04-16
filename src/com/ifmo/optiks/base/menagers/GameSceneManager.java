package com.ifmo.optiks.base.menagers;

import android.util.Log;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.ifmo.optiks.base.control.ActionMoveFilter;
import com.ifmo.optiks.base.gson.Constants;
import com.ifmo.optiks.base.gson.ObjectJsonContainer;
import com.ifmo.optiks.base.physics.joints.MouseJointOptiks;
import com.ifmo.optiks.base.physics.joints.RevoluteJointOptiks;
import com.ifmo.optiks.base.physics.*;
import com.ifmo.optiks.base.primitive_scene_items.sprite.*;
import com.ifmo.optiks.base.primitive_scene_items.lines.LaserBeam;
import com.ifmo.optiks.base.primitive_scene_items.lines.LaserSight;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.shape.IShape;
import org.anddev.andengine.entity.shape.Shape;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.extension.physics.box2d.util.Vector2Pool;
import org.anddev.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.util.HorizontalAlign;
import org.anddev.andengine.util.MathUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * Author: Aleksey Vladiev (Avladiev2@gmail.com)
 */
public class GameSceneManager {
    private final static String TAG = "GameSceneManagerTAG";

    private final GameTextureManager textureManager;
    private final GameSoundManager soundManager;
    final BaseGameActivity activity;
    protected Scene scene;
    private PhysicsWorld physicsWorld;
    private int level;
    private Body laserBody;
    private Body aimBody;
    private final List<Body> mirrorBodies = new LinkedList<Body>();
    private final List<Body> barrierBodies = new LinkedList<Body>();
    private final List<Body> wallBodies = new LinkedList<Body>();

    private Text toast;
    private Text numberOfTryToast;


    volatile private boolean mirrorIsSelected = false;
    volatile Body mirrorSelectedBody;

    private Body groundBody;
    private LaserBullet bullet;


    private MouseJointOptiks mouseJointOptiks;
    private MouseJoint mouseJoint;


    private int numberOfTry = 5;//todo


    public GameSceneManager(final BaseGameActivity activity, final GameTextureManager gtm, final GameSoundManager gsm) {
        textureManager = gtm;
        this.activity = activity;
        soundManager = gsm;
    }

    public Scene createScene(final String json) {
        scene = new Scene();
        physicsWorld = new PhysicsWorld(new Vector2(0, 0), false);
        scene.registerUpdateHandler(physicsWorld);
        try {
            final JSONObject jsonObject = new JSONObject(json);
            final JSONArray jsonArray = jsonObject.getJSONArray(Constants.OBJECTS);
            for (int i = 0; i < jsonArray.length(); ++i) {
                final ObjectJsonContainer container = new ObjectJsonContainer(jsonArray.getJSONObject(i));
                switch (container.type) {
                    case AIM: {
                        addAim(container);
                        break;
                    }
                    case BARRIER: {
                        addBarrier(container);
                        break;
                    }

                    case LASER: {
                        addLaser(container);
                        break;
                    }

                    case MIRROR: {
                        addMirror(container);
                        break;
                    }

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        scene.setBackground(new ColorBackground(0.09804f, 0.6274f, 0.8784f));


        createBorder(2);

        groundBody = physicsWorld.createBody(new BodyDef());


        final float x = laserBody.getPosition().x * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT;
        final float y = laserBody.getPosition().y * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT;

        numberOfTryToast = new Text(x, y, textureManager.getFont(), "" + numberOfTry);
        scene.attachChild(numberOfTryToast);

        final LaserBeam laserBeam = new LaserBeam(scene, new LaserBeam.Color(0, 1, 0, 0.5f), x, y);

        final LaserSight laserSight = new LaserSight(x, y, -1, -1);
        scene.attachChild(laserSight);
        bullet = new LaserBullet(PhysicsFactory.createCircleBody(physicsWorld, -1, -1, 1, 0, BodyDef.BodyType.DynamicBody, Fixtures.BULLET),
                laserBody, laserSight, laserBeam, new SampleCollisionHandler());
        //laserSight.setVisible(false);


        physicsWorld.setContactListener(bullet);
        scene.registerUpdateHandler(this.physicsWorld);
        scene.setOnSceneTouchListener(new SceneTouchListener());
        scene.setOnAreaTouchListener(new AreaTouchListener());

        return scene;
    }


    public void createBorder(final float a) {
        final Camera camera = activity.getEngine().getCamera();
        final float h = camera.getHeight();
        final float w = camera.getWidth();

        final Shape ground = new Rectangle(0, h - a, w, a);
        final Shape roof = new Rectangle(0, 0, w, a);
        final Shape left = new Rectangle(0, 0, a, h);
        final Shape right = new Rectangle(w - a, 0, a, w);

        wallBodies.add(PhysicsFactory.createBoxBody(this.physicsWorld, ground, BodyDef.BodyType.StaticBody, Fixtures.WALL));
        wallBodies.add(PhysicsFactory.createBoxBody(this.physicsWorld, roof, BodyDef.BodyType.StaticBody, Fixtures.WALL));
        wallBodies.add(PhysicsFactory.createBoxBody(this.physicsWorld, left, BodyDef.BodyType.StaticBody, Fixtures.WALL));
        wallBodies.add(PhysicsFactory.createBoxBody(this.physicsWorld, right, BodyDef.BodyType.StaticBody, Fixtures.WALL));

        scene.attachChild(ground);
        scene.attachChild(roof);
        scene.attachChild(left);
        scene.attachChild(right);
    }


    protected void addLaser(final ObjectJsonContainer container) {
        if (container.type != ObjectType.LASER) {
            throw new IllegalArgumentException();
        }
        final Laser laser = new Laser(container.pX, container.pY, textureManager.getLaserTextureRegion());
        scaleSprite(laser, container.width, container.height);
        final Body body = PhysicsFactory.createCircleBody(physicsWorld, laser, BodyDef.BodyType.StaticBody, Fixtures.LASER);
        addSprite(laser, body, container);
    }

    protected void addAim(final ObjectJsonContainer ogc) {
        final Aim aim = new Aim(ogc.pX, ogc.pY, textureManager.getAimTextureRegion());
        scaleSprite(aim, ogc.width, ogc.height);
        final Body body = PhysicsFactory.createCircleBody(physicsWorld, aim, BodyDef.BodyType.StaticBody, Fixtures.AIM_MIRROR_BARRIER);
        addSprite(aim, body, ogc);
    }

    protected void addMirror(final ObjectJsonContainer ogc) {
        final Mirror mirror = new Mirror(ogc.pX, ogc.pY, textureManager.getMirrorTextureRegion());
        scaleSprite(mirror, ogc.width, ogc.height);
        final Body body = PhysicsFactory.createBoxBody(physicsWorld, mirror, BodyDef.BodyType.StaticBody, Fixtures.AIM_MIRROR_BARRIER);
        addSprite(mirror, body, ogc);
    }

    protected void addBarrier(final ObjectJsonContainer ogc) {
        final Barrier barrier = new Barrier(ogc.pX, ogc.pY, textureManager.getBarrierTextureRegion());
        scaleSprite(barrier, ogc.width, ogc.height);
        final Body body = PhysicsFactory.createBoxBody(physicsWorld, barrier, BodyDef.BodyType.StaticBody, Fixtures.AIM_MIRROR_BARRIER);
        addSprite(barrier, body, ogc);
    }

    protected void addSprite(final GameSprite sprite, final Body body, final ObjectJsonContainer container) {
        body.setUserData(sprite);
        sprite.setUserData(body);
        physicsWorld.registerPhysicsConnector(new PhysicsConnector(sprite, body));
        body.setTransform(container.pX, container.pY, MathUtils.degToRad(container.rotation));
        switch (container.type) {
            case LASER:
                laserBody = body;
                break;
            case MIRROR:
                scene.registerTouchArea(sprite);
                mirrorBodies.add(body);
                break;
            case BARRIER:
                barrierBodies.add(body);
                break;
            case AIM:
                scene.registerTouchArea(sprite);
                aimBody = body;
                break;
        }
        scene.attachChild(sprite);
    }


    private void scaleSprite(final Sprite sprite, final float width, final float height) {
        final float scaleX = width / sprite.getTextureRegion().getWidth();
        final float scaleY = height / sprite.getTextureRegion().getHeight();
        sprite.setScale(scaleX, scaleY);
    }


    public int getLevel() {
        return level;
    }

    public Body getLaser() {
        return laserBody;
    }

    public Body getAim() {
        return aimBody;
    }

    public List<Body> getMirrors() {
        return mirrorBodies;
    }

    public List<Body> getBarriers() {
        return barrierBodies;
    }

    public List<Body> getWalls() {
        return wallBodies;
    }

    public Scene getScene() {
        return scene;
    }

    public PhysicsWorld getPhysicsWorld() {
        return physicsWorld;
    }


    private class SceneTouchListener implements Scene.IOnSceneTouchListener {
        public boolean onSceneTouchEvent(final Scene scene, final TouchEvent touchEvent) {
            switch (touchEvent.getAction()) {
                case TouchEvent.ACTION_DOWN:
                    if (toast != null) {
                        scene.detachChild(toast);
                        toast = null;
                    }
                    bullet.sightSetPos(touchEvent.getX(), touchEvent.getY());
                    return true;
                case TouchEvent.ACTION_MOVE:
                    if (mouseJoint != null) {
                        final Vector2 vec = Vector2Pool.obtain(touchEvent.getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, touchEvent.getY() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
                        mouseJoint.setTarget(vec);
                        Vector2Pool.recycle(vec);
                    } else {
                        bullet.sightSetPos(touchEvent.getX(), touchEvent.getY());
                    }
                    return true;
                case TouchEvent.ACTION_UP:
                    if (mouseJoint != null) {
                        mouseJoint.getBodyB().setType(BodyDef.BodyType.StaticBody);
                        physicsWorld.destroyJoint(mouseJoint);
                        mouseJoint = null;
                        mouseJointOptiks = null;
                    }
                    if (rjo != null) {
                        rjo.destroyJoint(physicsWorld);
                        rjo = null;
                    }
                    return true;
            }
            return false;


        }

    }


    private RevoluteJointOptiks rjo = null;

    private class AreaTouchListener implements Scene.IOnAreaTouchListener {
        private final long timer = 200;
        protected long currentTimer;
        private boolean wasActionDown = false;
        private ActionMoveFilter filter;

        public boolean onAreaTouched(final TouchEvent touchEvent, final Scene.ITouchArea touchArea, final float touchAreaLocalX, final float touchAreaLocalY) {
            final IShape object = (Sprite) touchArea;
            final Body body = (Body) object.getUserData();
            switch (touchEvent.getAction()) {
                case TouchEvent.ACTION_DOWN:
                    if (aimBody == body) {
                        if (!bullet.isMoving()) {
                            if (numberOfTry > 0) {
                                scene.detachChild(numberOfTryToast);
                                numberOfTryToast = new Text(numberOfTryToast.getX(), numberOfTryToast.getY(), textureManager.getFont(), "" + --numberOfTry, HorizontalAlign.CENTER);
                                scene.attachChild(numberOfTryToast);
                                bullet.shoot();
                            } else {
                                toast = new Text(360, 240, textureManager.getFont(), "Try again...", HorizontalAlign.CENTER);
                                scene.attachChild(toast);
                            }
                        }
                    } else {
                        if (mouseJointOptiks == null) {
                            mouseJointOptiks = new MouseJointOptiks(object, groundBody, touchAreaLocalX, touchAreaLocalY) {
                                @Override
                                public MouseJoint getMouseJoint() {
                                    return (MouseJoint) physicsWorld.createJoint(this);
                                }
                            };
                            mouseJoint = mouseJointOptiks.getMouseJoint();
                        }
                        if (rjo == null) {
                            Log.d(TAG, "create_new");
                            rjo = new RevoluteJointOptiks();
                            rjo.initialize(body, physicsWorld);
                        }
                        currentTimer = System.currentTimeMillis();
                        wasActionDown = true;
                        filter = new ActionMoveFilter(touchAreaLocalX, touchAreaLocalY);
                    }
                    return true;
                case TouchEvent.ACTION_MOVE:
                    if (wasActionDown && aimBody != body) {
                        if (filter.notMoving(touchAreaLocalX, touchAreaLocalY)) {
                            if (System.currentTimeMillis() - currentTimer >= timer) {
                                if (rjo != null) {
//                                    activity.getEngine().vibrate(100);
                                    physicsWorld.destroyJoint(mouseJoint);
                                    mouseJointOptiks = new MouseJointOptiks(object, groundBody, touchAreaLocalX, touchAreaLocalY) {
                                        @Override
                                        public MouseJoint getMouseJoint() {
                                            return (MouseJoint) physicsWorld.createJoint(this);
                                        }
                                    };
                                    mouseJoint = mouseJointOptiks.getMouseJoint();
                                    rjo.destroyJoint(physicsWorld);
                                    rjo = null;
                                }
                                filter.setByTimer();
                                Log.d("timer", "long_touch");
                            } else {
                                Log.d("timer", "waiting");
                            }
                        } else {
                            Log.d("timer", "not_long_touch");
                        }
                    }
                    return true;
                case TouchEvent.ACTION_OUTSIDE:
                    return true;
                case TouchEvent.ACTION_UP:
                    wasActionDown = false;
                    filter = null;
                    if (rjo != null) {
                        rjo.destroyJoint(physicsWorld);
                        rjo = null;
                    }
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


    private class SampleCollisionHandler implements CollisionHandler {

        @Override
        public void handle(final Contact contact, final LaserBullet bullet, final Body thing) {
            final Vector2 vec = contact.getWorldManifold().getPoints()[0];
            bullet.AddLineToLaserBeam(vec.x * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT, vec.y * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT);

            if (wallBodies.contains(thing) || barrierBodies.contains(thing)) {
                bullet.stop();
                toast = new Text(360, 240, textureManager.getFont(), "Try again...", HorizontalAlign.CENTER);
                scene.attachChild(toast);
            } else if (thing == aimBody) {
                bullet.stop();
                toast = new Text(360, 240, textureManager.getFont(), "Good Shoot!", HorizontalAlign.CENTER);
                scene.attachChild(toast);
            }


        }
    }

}
