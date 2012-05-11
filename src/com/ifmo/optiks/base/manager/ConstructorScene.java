package com.ifmo.optiks.base.manager;

import com.badlogic.gdx.physics.box2d.Body;
import com.ifmo.optiks.OptiksActivity;
import com.ifmo.optiks.base.gson.BaseObjectJsonContainer;
import com.ifmo.optiks.base.gson.Converter;
import com.ifmo.optiks.base.gson.LevelGsonContainer;
import com.ifmo.optiks.base.gson.MirrorJsonContainer;
import com.ifmo.optiks.base.item.sprite.BodyForm;
import com.ifmo.optiks.base.item.sprite.GameSprite;
import com.ifmo.optiks.base.item.sprite.ObjectType;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import java.util.LinkedList;
import java.util.List;

/**
 * Author: Aleksey Vladiev (Avladiev2@gmail.com)
 */

public class ConstructorScene extends GameScene {

    private final List<GameSprite> objectsSprites = new LinkedList<GameSprite>();

    public ConstructorScene(final OptiksActivity activity, final PhysicsWorld world) {
        super(activity, world);
    }

    public void setBackground(final ColorBackground colorBackground) {
        // TODO I (Serezhka) fixed bug. But.. it's now correct?
        super.setBackground(colorBackground);
    }

    public void addLaser(final float x, final float y) {
        addLaser(new BaseObjectJsonContainer(x, y, 100, 100, 0, ObjectType.LASER, BodyForm.DEFAULT));
    }

    public void addBarrier(final float x, final float y) {
        addBarrier(new BaseObjectJsonContainer(x, y, 100, 200, 0, ObjectType.BARRIER, BodyForm.RECTANGLE));
    }

    public void addMirror(final float x, final float y) {
        addMirror(new MirrorJsonContainer(x, y, 20, 20, 0, ObjectType.MIRROR, BodyForm.RECTANGLE, true, true));
    }

    public void addAim(final float x, final float y) {
        addAim(new BaseObjectJsonContainer(x, y, 50, 50, 0, ObjectType.AIM, BodyForm.CIRCLE));
    }

    @Override
    protected void addSprite(final GameSprite sprite, final Body body, final BaseObjectJsonContainer container) {
        super.addSprite(sprite, body, container);
        objectsSprites.add(sprite);
    }

    public String getGson() {
        final LevelGsonContainer fieldContainer = new LevelGsonContainer(objectsSprites.size());
        final BaseObjectJsonContainer[] containers = fieldContainer.getObjects();
        int i = 0;
        for (final GameSprite objectsSprite : objectsSprites) {
            containers[i++] = objectsSprite.getGsonContainer();
        }
        return Converter.getInstance().toGson(containers);
    }
}
