package com.ifmo.optiks.base;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.ifmo.optiks.base.gson.Converter;
import com.ifmo.optiks.base.gson.LevelGsonContainer;
import com.ifmo.optiks.base.gson.ObjectGsonContainer;
import com.ifmo.optiks.base.physics.Fixtures;
import com.ifmo.optiks.base.sprite.*;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.shape.Shape;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.util.MathUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Author: Aleksey Vladiev (Avladiev2@gmail.com)
 */

public class LevelField {

    private int level;

    private final BaseGameActivity activity;

    private Scene scene;
    private PhysicsWorld physicsWorld;
    private Fixtures fixtures;

    private final List<GameSprite> objectsSprites = new LinkedList<GameSprite>();

    private Body laserBody;
    private Body aimBody;
    private final List<Body> mirrorBodies = new LinkedList<Body>();
    private final List<Body> barrierBodies = new LinkedList<Body>();
    private final List<Body> wallBodies = new LinkedList<Body>();

    private final BitmapTextureAtlas bitmapTextureAtlas;
    private final TextureRegion aimTextureRegion;
    private final TextureRegion laserTextureRegion;
    private final TextureRegion mirrorTextureRegion;
    private final TextureRegion barrierTextureRegion;

    public LevelField(final BaseGameActivity gameActivity) {
        this.activity = gameActivity;
        this.bitmapTextureAtlas = new BitmapTextureAtlas(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        this.barrierTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.bitmapTextureAtlas, activity, "barrier_1.png", 0, 0);
        this.mirrorTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.bitmapTextureAtlas, activity, "mirror_1.png", 201, 0);
        this.aimTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.bitmapTextureAtlas, activity, "aim_1.png", 0, 101);
        this.laserTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.bitmapTextureAtlas, activity, "laser_2.png", 101, 101);
        this.activity.getEngine().getTextureManager().loadTexture(bitmapTextureAtlas);
    }

    public void setFixtures(final Fixtures fixtures) {
        this.fixtures = fixtures;
    }

    public Scene onLoadScene(final String gson) {
        this.scene = new Scene();
        this.physicsWorld = new PhysicsWorld(new Vector2(0, 0), false);
        if (gson != null) {
            createBorder(2f);
            final LevelGsonContainer gsonContainer = Converter.getInstance().fromGson(gson, LevelGsonContainer.class);
            final ObjectGsonContainer[] objects = gsonContainer.getObjects();
            for (final ObjectGsonContainer container : objects) {
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
        }
        return scene;
    }

    public String getGson() {
        final LevelGsonContainer fieldContainer = new LevelGsonContainer(objectsSprites.size());
        final ObjectGsonContainer[] containers = fieldContainer.getObjects();
        int i = 0;
        for (final GameSprite objectsSprite : objectsSprites) {
            containers[i++] = objectsSprite.getGsonContainer();
        }
        return Converter.getInstance().toGson(containers);
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(final int level) {
        this.level = level;
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

    public BitmapTextureAtlas getBitmapTextureAtlas() {
        return bitmapTextureAtlas;
    }

    public void addLaser(final float x, final float y) {
        addLaser(new ObjectGsonContainer(x, y, 1, 1, 0, ObjectType.LASER));
    }

    public void addBarrier(final float x, final float y) {
        addBarrier(new ObjectGsonContainer(x, y, 0.3f, 1, 0, ObjectType.BARRIER));
    }

    public void addMirror(final float x, final float y) {
        addMirror(new ObjectGsonContainer(x, y, 2, 2, 0, ObjectType.MIRROR));
    }

    public void addAim(final float x, final float y) {
        addAim(new ObjectGsonContainer(x, y, 0.3f, 0.3f, 0, ObjectType.AIM));
    }

    public void createBorder(final float a) {
        final Camera camera = activity.getEngine().getCamera();
        final float h = camera.getHeight();
        final float w = camera.getWidth();

        final Shape ground = new Rectangle(0, h - a, w, a);
        final Shape roof = new Rectangle(0, 0, w, a);
        final Shape left = new Rectangle(0, 0, a, h);
        final Shape right = new Rectangle(w - a, 0, a, w);

        wallBodies.add(PhysicsFactory.createBoxBody(this.physicsWorld, ground, BodyDef.BodyType.StaticBody, fixtures.getWall()));
        wallBodies.add(PhysicsFactory.createBoxBody(this.physicsWorld, roof, BodyDef.BodyType.StaticBody, fixtures.getWall()));
        wallBodies.add(PhysicsFactory.createBoxBody(this.physicsWorld, left, BodyDef.BodyType.StaticBody, fixtures.getWall()));
        wallBodies.add(PhysicsFactory.createBoxBody(this.physicsWorld, right, BodyDef.BodyType.StaticBody, fixtures.getWall()));

        scene.attachChild(ground);
        scene.attachChild(roof);
        scene.attachChild(left);
        scene.attachChild(right);
    }

    public void setBackground(final ColorBackground colorBackground) {
        scene.setBackground(colorBackground);
    }

    private void addLaser(final ObjectGsonContainer container) {
        if (container.type != ObjectType.LASER) {
            throw new IllegalArgumentException();
        }
        final Laser laser = new Laser(container.pX, container.pY, laserTextureRegion);
        scaleSprite(laser, container.width, container.height);
        final Body body = PhysicsFactory.createCircleBody(physicsWorld, laser, BodyDef.BodyType.StaticBody, fixtures.getLaser());
        addSprite(laser, body, container);
    }

    private void addAim(final ObjectGsonContainer ogc) {
        final Aim aim = new Aim(ogc.pX, ogc.pY, aimTextureRegion);
        scaleSprite(aim, ogc.width, ogc.height);
        final Body body = PhysicsFactory.createCircleBody(physicsWorld, aim, BodyDef.BodyType.StaticBody, fixtures.getAimMirrorBarrier());
        addSprite(aim, body, ogc);
    }

    private void addMirror(final ObjectGsonContainer ogc) {
        final Mirror mirror = new Mirror(ogc.pX, ogc.pY, mirrorTextureRegion);
        scaleSprite(mirror, ogc.width, ogc.height);
        final Body body = PhysicsFactory.createBoxBody(physicsWorld, mirror, BodyDef.BodyType.StaticBody, fixtures.getAimMirrorBarrier());
        addSprite(mirror, body, ogc);
    }

    private void addBarrier(final ObjectGsonContainer ogc) {
        final Barrier barrier = new Barrier(ogc.pX, ogc.pY, barrierTextureRegion);
        scaleSprite(barrier, ogc.width, ogc.height);
        final Body body = PhysicsFactory.createBoxBody(physicsWorld, barrier, BodyDef.BodyType.StaticBody, fixtures.getAimMirrorBarrier());
        addSprite(barrier, body, ogc);
    }

    private void addSprite(final GameSprite sprite, final Body body, final ObjectGsonContainer container) {
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
        objectsSprites.add(sprite);
        scene.attachChild(sprite);
    }

    private void scaleSprite(final Sprite sprite, final float width, final float height) {
        final float scaleX = width / sprite.getTextureRegion().getWidth();
        final float scaleY = height / sprite.getTextureRegion().getHeight();
        sprite.setScale(scaleX, scaleY);
    }
}
