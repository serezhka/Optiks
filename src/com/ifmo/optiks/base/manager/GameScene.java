package com.ifmo.optiks.base.manager;

import android.util.Log;
import android.view.KeyEvent;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.ifmo.optiks.OptiksActivity;
import com.ifmo.optiks.base.control.ActionMoveFilter;
import com.ifmo.optiks.base.gson.BaseObjectJsonContainer;
import com.ifmo.optiks.base.gson.Constants;
import com.ifmo.optiks.base.gson.MirrorJsonContainer;
import com.ifmo.optiks.base.item.line.LaserBeam;
import com.ifmo.optiks.base.item.sprite.*;
import com.ifmo.optiks.base.physics.CollisionHandler;
import com.ifmo.optiks.base.physics.Fixtures;
import com.ifmo.optiks.base.physics.LaserBullet;
import com.ifmo.optiks.base.physics.joints.JointsManager;
import com.ifmo.optiks.scene.OptiksLevelsScene;
import com.ifmo.optiks.scene.OptiksScene;
import com.ifmo.optiks.scene.OptiksScenes;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.shape.IShape;
import org.anddev.andengine.entity.shape.Shape;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.input.touch.TouchEvent;
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
public class GameScene extends OptiksScene {

    private final static String TAG = "GameSceneTAG";

    protected final OptiksActivity optiksActivity;

    protected final PhysicsWorld physicsWorld;

    protected final OptiksTextureManager textureManager;
    protected final OptiksSoundManager soundManager;

    protected Body aimBody;
    protected Body laserBody;

    protected AnimatedSprite sight;
    protected AnimatedSprite sightChild;

    protected final List<Body> mirrorBodies = new LinkedList<Body>();
    protected final List<Body> barrierBodies = new LinkedList<Body>();
    protected final List<Body> wallBodies = new LinkedList<Body>();

    private LaserBullet bullet;

//    private int numberOfTry = 5; // TODO assign it from level json data

    private Text toast;
    private Text numberOfTryToast;

    private final ColorBackground colorBackground = new ColorBackground(0.0f, 0.0f, 0.0f);

    public GameScene(final OptiksActivity optiksActivity, final PhysicsWorld world) {
        this.optiksActivity = optiksActivity;
        textureManager = optiksActivity.getOptiksTextureManager();
        soundManager = optiksActivity.getOptiksSoundManager();
        physicsWorld = world;
    }

    public GameScene(final String json, final OptiksActivity optiksActivity) {
        this.optiksActivity = optiksActivity;
        textureManager = optiksActivity.getOptiksTextureManager();
        soundManager = optiksActivity.getOptiksSoundManager();
        physicsWorld = new PhysicsWorld(new Vector2(0, 0), false);
        registerUpdateHandler(physicsWorld);
        sight = new AnimatedSprite(360, 240, textureManager.sight);
        sightChild = new AnimatedSprite(0, 0, textureManager.emptyTexture);
        sightChild.setScaleX(sight.getWidth() * 2 / sightChild.getWidth());
        sightChild.setScaleY(sight.getHeight() * 2 / sightChild.getHeight());
        sight.attachChild(sightChild);
        sightChild.setPosition(-sightChild.getWidth() / 4, -sightChild.getHeight() / 4);
        registerTouchArea(sightChild);
        try {
//            final JSONObject jsonObject = new JSONObject(json);
//            final JSONArray jsonArray = jsonObject.getJSONArray(Constants.OBJECTS);
            final JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); ++i) {
                final JSONObject object = jsonArray.getJSONObject(i);
                final ObjectType objectType = ObjectType.getType(object.getString(Constants.TYPE));
                switch (objectType) {
                    case AIM: {
                        addAim(new BaseObjectJsonContainer(object));
                        break;
                    }
                    case BARRIER: {
                        addBarrier(new BaseObjectJsonContainer(object));
                        break;
                    }

                    case LASER: {
                        addLaser(new BaseObjectJsonContainer(object));
                        break;
                    }

                    case MIRROR: {
                        addMirror(new MirrorJsonContainer(object));
                        break;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setBackground(colorBackground);

        createBorder(2);//  todo move  in json


        final float x = laserBody.getPosition().x * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT;
        final float y = laserBody.getPosition().y * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT;

//        numberOfTryToast = new Text(x, y, textureManager.font, "" + numberOfTry);
//        attachChild(numberOfTryToast);

        final LaserBeam laserBeam = new LaserBeam(this, new LaserBeam.Color(0, 1, 0, 0.5f), x, y);

        attachChild(sight);
        bullet = new LaserBullet(PhysicsFactory.createCircleBody(physicsWorld, -1, -1, 1, 0, BodyDef.BodyType.DynamicBody, Fixtures.BULLET),
                laserBody, sight, laserBeam, new SampleCollisionHandler());

        physicsWorld.setContactListener(bullet);
        final TouchListener touchListener = new TouchListener(physicsWorld);
        setOnSceneTouchListener(touchListener);
        setOnAreaTouchListener(touchListener);
    }

    public void createBorder(final float a) {
        final Camera camera = optiksActivity.getEngine().getCamera();
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

        attachChild(ground);
        attachChild(roof);
        attachChild(left);
        attachChild(right);
    }

    protected void addLaser(final BaseObjectJsonContainer ojc) {
        final Laser laser = new Laser(ojc, textureManager.laserTextureRegion, BodyForm.CIRCLE);
        final Body body = PhysicsFactory.createCircleBody(physicsWorld, laser, BodyDef.BodyType.StaticBody, Fixtures.LASER);
        addSprite(laser, body, ojc);
    }

    protected void addAim(final BaseObjectJsonContainer ojc) {
        final Aim aim;
        final Body body;
        switch (ojc.bodyForm) {
            case RECTANGLE:
                aim = new Aim(ojc, textureManager.aimTextureRegion, BodyForm.RECTANGLE);
                body = PhysicsFactory.createBoxBody(physicsWorld, aim, BodyDef.BodyType.StaticBody, Fixtures.AIM_MIRROR_BARRIER);
                break;
            case CIRCLE:
                aim = new Aim(ojc, textureManager.aimTextureRegion, BodyForm.CIRCLE);
                body = PhysicsFactory.createCircleBody(physicsWorld, aim, BodyDef.BodyType.StaticBody, Fixtures.AIM_MIRROR_BARRIER);
                break;
            default:
                throw new RuntimeException();
        }
        addSprite(aim, body, ojc);
    }

    protected void addMirror(final MirrorJsonContainer mjc) {
        final Mirror mirror;
        final Body body;
        switch (mjc.bodyForm) {
            case RECTANGLE:
                mirror = new Mirror(mjc, textureManager.mirrorRectangleTextureRegion, BodyForm.RECTANGLE);
                body = PhysicsFactory.createBoxBody(physicsWorld, mirror, BodyDef.BodyType.StaticBody, Fixtures.AIM_MIRROR_BARRIER);
//                mirror.animate(new long[]{100, 100, 100, 100, 100, 100, 100, 100, 100, 100}, 1, 10, true);
                final float meter = (mjc.width <= 200) ? mjc.width : 200;
                if (mirror.canRotate) {
                    AnimatedSprite mirrorSplash = new AnimatedSprite(0, 0, textureManager.mirrorSplash);
                    mirrorSplash.setPosition(-mirrorSplash.getWidth() / 3, (mirror.getHeight() - mirrorSplash.getHeight()) / 2);
                    mirrorSplash.setScaleX((meter * 4 / 10) / mirrorSplash.getHeight());
                    mirrorSplash.setScaleY((meter * 4 / 10) / mirrorSplash.getHeight());
                    mirror.attachChild(mirrorSplash);

                    mirrorSplash = new AnimatedSprite(0, 0, textureManager.mirrorSplash);
                    mirrorSplash.setPosition(mirror.getWidth() - mirrorSplash.getWidth() * 2 / 3, (mirror.getHeight() - mirrorSplash.getHeight()) / 2);
                    mirrorSplash.setScaleX((meter * 4 / 10) / mirrorSplash.getHeight());
                    mirrorSplash.setScaleY((meter * 4 / 10) / mirrorSplash.getHeight());
                    mirror.attachChild(mirrorSplash);
                }
                if (mirror.canMove) {
                    final AnimatedSprite mirrorSplash = new AnimatedSprite(0, 0, textureManager.mirrorSplash);
                    mirrorSplash.setPosition((mirror.getWidth() - mirrorSplash.getWidth()) / 2, (mirror.getHeight() - mirrorSplash.getHeight()) / 2);
                    mirrorSplash.setScaleX((meter * 3 / 5) / mirrorSplash.getHeight());
                    mirrorSplash.setScaleY((meter * 3 / 5) / mirrorSplash.getHeight());
                    mirror.attachChild(mirrorSplash);
                }
                break;
            case CIRCLE:
                mirror = new Mirror(mjc, textureManager.mirrorCircleTextureRegion, BodyForm.CIRCLE);
                body = PhysicsFactory.createCircleBody(physicsWorld, mirror, BodyDef.BodyType.StaticBody, Fixtures.AIM_MIRROR_BARRIER);
//                mirror.animate(new long[]{100, 100, 100, 100, 100, 100, 100, 100, 100, 100}, 0, 9, true);
                if (mirror.canMove) {
                    final AnimatedSprite mirrorSplash = new AnimatedSprite(0, 0, textureManager.mirrorSplash);
                    mirrorSplash.setPosition((mirror.getWidth() - mirrorSplash.getWidth()) / 2, (mirror.getHeight() - mirrorSplash.getHeight()) / 2);
                    mirrorSplash.setScaleX((mjc.height * 3 / 2) / mirrorSplash.getHeight());
                    mirrorSplash.setScaleY((mjc.height * 3 / 2) / mirrorSplash.getHeight());
                    mirror.attachChild(mirrorSplash);
                }
                break;
            default:
                throw new RuntimeException();
        }
        addSprite(mirror, body, mjc);
    }

    protected void addBarrier(final BaseObjectJsonContainer ojc) {
        final Barrier barrier;
        final Body body;
        switch (ojc.bodyForm) {
            case RECTANGLE:
                barrier = new Barrier(ojc, textureManager.barrierRectangleTextureRegion, BodyForm.RECTANGLE);
                body = PhysicsFactory.createBoxBody(physicsWorld, barrier, BodyDef.BodyType.StaticBody, Fixtures.AIM_MIRROR_BARRIER);
                break;
            case CIRCLE:
                barrier = new Barrier(ojc, textureManager.barrierCircleTextureRegion, BodyForm.CIRCLE);
                body = PhysicsFactory.createCircleBody(physicsWorld, barrier, BodyDef.BodyType.StaticBody, Fixtures.AIM_MIRROR_BARRIER);
                break;
            default:
                throw new RuntimeException();
        }
        addSprite(barrier, body, ojc);
    }

    protected void addSprite(final GameSprite sprite, final Body body, final BaseObjectJsonContainer container) {
        body.setUserData(sprite);
        sprite.setUserData(body);
        physicsWorld.registerPhysicsConnector(new PhysicsConnector(sprite, body));
        switch (container.type) {
            case LASER:
                laserBody = body;
                sprite.animate(100);
                break;
            case MIRROR:
                if (((Mirror) sprite).canMove || ((Mirror) sprite).canRotate) {
                    final AnimatedSprite emptySprite = new AnimatedSprite(0, 0, textureManager.emptyTexture);
                    switch (container.bodyForm) {
                        case CIRCLE:
                            sprite.attachChild(emptySprite);
                            emptySprite.setWidth(sprite.getWidth() + 20);
                            emptySprite.setHeight(sprite.getHeight() + 20);
                            emptySprite.setPosition(-10, -10);
                            registerTouchArea(emptySprite);
                            break;
                        case RECTANGLE:
                            sprite.attachChild(emptySprite);
                            emptySprite.setWidth(sprite.getWidth());
                            emptySprite.setHeight((container.height >= 50) ? container.height + 50 : 50);
                            emptySprite.setPosition(0, (sprite.getHeight() - emptySprite.getHeight()) / 2);
                            registerTouchArea(emptySprite);
                            break;
                        case DEFAULT:
                            break;
                    }
                }
                mirrorBodies.add(body);
                break;
            case BARRIER:
                barrierBodies.add(body);
                break;
            case AIM:
                registerTouchArea(sprite);
                aimBody = body;
                sprite.animate(100);
                break;
        }
        body.setTransform(container.pX / PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT, container.pY / PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT, MathUtils.degToRad(container.rotation));
        attachChild(sprite);
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

    public PhysicsWorld getPhysicsWorld() {
        return physicsWorld;
    }

    @Override
    public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
        if (pKeyCode == KeyEvent.KEYCODE_BACK && pEvent.getAction() == KeyEvent.ACTION_DOWN) {
            optiksActivity.setActiveScene(optiksActivity.scenes.get(OptiksScenes.LEVELS_SCENE));
            return true;
        } else if (pKeyCode == KeyEvent.KEYCODE_MENU && pEvent.getAction() == KeyEvent.ACTION_DOWN) {
            optiksActivity.setActiveScene(optiksActivity.scenes.get(OptiksScenes.MENU_SCENE));
            return true;
        }
        return false;
    }

    private class TouchListener implements IOnSceneTouchListener, IOnAreaTouchListener {
        private final ActionMoveFilter filter;
        private final JointsManager jointsManager;
        private int wasActionDown = 0; /*if =1, mirror. if =2, sight*/
        private float dx;
        private float dy;
        private SelectedObject selectedObject = null;

        private TouchListener(final PhysicsWorld physicsWorld) {
            filter = new ActionMoveFilter();
            jointsManager = new JointsManager(physicsWorld);
        }
        
        
        void succus(){
            //todo....
            //todo go to base
            final OptiksLevelsScene levelsScene =(OptiksLevelsScene) optiksActivity.scenes.get(OptiksScenes.LEVELS_SCENE);
            levelsScene.setMaxLevelReached(-1);
            optiksActivity.setActiveScene(levelsScene);
        }

        @Override
        public boolean onSceneTouchEvent(final Scene scene, final TouchEvent touchEvent) {
            switch (touchEvent.getAction()) {
                case TouchEvent.ACTION_DOWN:
                    if (toast != null) {
                        detachChild(toast);
                        toast = null;
                    }
                    return true;
                case TouchEvent.ACTION_MOVE:
                    switch (wasActionDown) {
                        case 1:
                            if (!jointsManager.setTarget(touchEvent)) {
                            }
                            break;
                        case 2:
                            sight.setPosition(touchEvent.getX() + dx, touchEvent.getY() + dy);
                            break;
                        default:
                            break;
                    }
                    return true;
                case TouchEvent.ACTION_UP:
                    wasActionDown = 0;
                    jointsManager.destroyJoints();
                    filter.destroy();
                    return true;
                default:
                    return false;
            }

        }

        @Override
        public boolean onAreaTouched(final TouchEvent touchEvent, final ITouchArea touchArea, final float touchAreaLocalX, final float touchAreaLocalY) {
            final IShape object = (AnimatedSprite) touchArea;
            IShape objectParent = null;
            try {
                objectParent = (AnimatedSprite) object.getParent();
            } catch (ClassCastException e) {
                //todo!!
            }
            switch (touchEvent.getAction()) {
                case TouchEvent.ACTION_DOWN:
                    if (object.equals(sightChild)) {
                        wasActionDown = 2;
                        dx = sight.getX() - touchEvent.getX();
                        dy = sight.getY() - touchEvent.getY();
                    } else if (aimBody.equals(object.getUserData())) {
                        if (!bullet.isMoving()) {
//                            if (numberOfTry > 0) {
//                                detachChild(numberOfTryToast);
//                                numberOfTryToast = new Text(numberOfTryToast.getX(), numberOfTryToast.getY(), textureManager.font, "" + --numberOfTry, HorizontalAlign.CENTER);
//                                attachChild(numberOfTryToast);
                            bullet.shoot();
//                            } else {
//                                toast = new Text(360, 240, textureManager.font, "Try again...", HorizontalAlign.CENTER);
//                                attachChild(toast);
//                            }
                        }
                    } else if (mirrorBodies.contains(objectParent.getUserData())) {
                        Log.d(TAG, "objectParent is ok");
                        wasActionDown = 1;
                        jointsManager.createJoints(objectParent, object, touchAreaLocalX, touchAreaLocalY);
                        if (((Mirror) objectParent).canMove) {
                            filter.init(touchAreaLocalX, touchAreaLocalY);
                        }
                    }
                    return true;
                case TouchEvent.ACTION_MOVE:
                    switch (wasActionDown) {
                        case 1:
                            if (!filter.isWaiting(touchAreaLocalX, touchAreaLocalY)) {
                                jointsManager.setTarget(touchEvent);
                                if (!filter.isMove() && filter.isDestroyRotate(touchAreaLocalX, touchAreaLocalY)) {
                                    soundManager.vibrate();
                                    jointsManager.destroyRotate();
                                }
                            }
                            break;
                        case 2:
                            sight.setPosition(touchEvent.getX() + dx, touchEvent.getY() + dy);
                            break;
                        default:
                            break;
                    }
                    return true;
                case TouchEvent.ACTION_OUTSIDE:
                    return true;
                case TouchEvent.ACTION_UP:
                    jointsManager.destroyJoints();
                    filter.destroy();
                    wasActionDown = 0;
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
                toast = new Text(360, 240, textureManager.font, "Try again...", HorizontalAlign.CENTER);
                attachChild(toast);
            } else if (thing == aimBody) {
                bullet.stop();
                toast = new Text(360, 240, textureManager.font, "Good Shoot!", HorizontalAlign.CENTER);
                attachChild(toast);
            }
        }
    }
}
