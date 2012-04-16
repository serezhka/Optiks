package com.ifmo.optiks.base.menagers;

import com.badlogic.gdx.physics.box2d.Body;
import com.ifmo.optiks.base.gson.Converter;
import com.ifmo.optiks.base.gson.LevelGsonContainer;
import com.ifmo.optiks.base.gson.ObjectJsonContainer;
import com.ifmo.optiks.base.primitive_game_scene_items.sprite.GameSprite;
import com.ifmo.optiks.base.primitive_game_scene_items.sprite.ObjectType;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import java.util.LinkedList;
import java.util.List;

/**
 * Author: Aleksey Vladiev (Avladiev2@gmail.com)
 */
public class ConstructorGameScene extends GameSceneManager {
    private final List<GameSprite> objectsSprites = new LinkedList<GameSprite>();


    public ConstructorGameScene(final BaseGameActivity activity, final GameTextureManager gtm, final GameSoundManager gsm) {
        super(activity, gtm, gsm);
    }

    //??todo delete
    public void setBackground(final ColorBackground colorBackground) {
        scene.setBackground(colorBackground);
    }

    public void addLaser(final float x, final float y) {
        addLaser(new ObjectJsonContainer(x, y, 1, 1, 0, ObjectType.LASER));
    }

    public void addBarrier(final float x, final float y) {
        addBarrier(new ObjectJsonContainer(x, y, 0.3f, 1, 0, ObjectType.BARRIER));
    }

    public void addMirror(final float x, final float y) {
        addMirror(new ObjectJsonContainer(x, y, 2, 2, 0, ObjectType.MIRROR));
    }

    public void addAim(final float x, final float y) {
        addAim(new ObjectJsonContainer(x, y, 0.3f, 0.3f, 0, ObjectType.AIM));
    }

    @Override
    protected void addSprite(final GameSprite sprite, final Body body, final ObjectJsonContainer container) {
        super.addSprite(sprite, body, container);
        objectsSprites.add(sprite);
    }

    public String getGson() {
        final LevelGsonContainer fieldContainer = new LevelGsonContainer(objectsSprites.size());
        final ObjectJsonContainer[] containers = fieldContainer.getObjects();
        int i = 0;
        for (final GameSprite objectsSprite : objectsSprites) {
            containers[i++] = objectsSprite.getGsonContainer();
        }
        return Converter.getInstance().toGson(containers);
    }
}
