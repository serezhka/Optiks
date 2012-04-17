package com.ifmo.optiks.base.menagers;

import android.graphics.Color;
import android.graphics.Typeface;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

/**
 * Author: Aleksey Vladiev (Avladiev2@gmail.com)
 */
public class GameTextureManager {
    private static GameTextureManager ourInstance = new GameTextureManager();
    private final BaseGameActivity activity;

    private final BitmapTextureAtlas bitmapTextureAtlas;
    private final TextureRegion aimTextureRegion;
    private final TextureRegion laserTextureRegion;
    private final TextureRegion mirrorTextureRegion;
    private final TextureRegion barrierTextureRegion;


    private final Font font;


    public static GameTextureManager getInstance(final BaseGameActivity act) {
        if (act != ourInstance.activity) {
            ourInstance = new GameTextureManager(act);      //todo
        }
        return ourInstance;
    }


    private GameTextureManager() {
        this.activity = null;
        this.bitmapTextureAtlas = null;
        this.aimTextureRegion = null;
        this.laserTextureRegion = null;
        this.mirrorTextureRegion = null;
        this.barrierTextureRegion = null;
        font = null;
    }

    private GameTextureManager(final BaseGameActivity gameActivity) {
        activity = gameActivity;
        bitmapTextureAtlas = new BitmapTextureAtlas(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        barrierTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.bitmapTextureAtlas, activity, "barrier.png", 0, 0);
        aimTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.bitmapTextureAtlas, activity, "aim.png", 0, 200);
        laserTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.bitmapTextureAtlas, activity, "laser.png", 200, 200);
        mirrorTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.bitmapTextureAtlas, activity, "mirror.png", 0, 400);
        activity.getEngine().getTextureManager().loadTexture(bitmapTextureAtlas);

        final BitmapTextureAtlas atlas = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR);
        font = new Font(atlas, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32, true, Color.BLACK);
        activity.getTextureManager().loadTexture(atlas);
        activity.getFontManager().loadFont(font);

    }

    public BitmapTextureAtlas getBitmapTextureAtlas() {
        return bitmapTextureAtlas;
    }

    public TextureRegion getAimTextureRegion() {
        return aimTextureRegion;
    }

    public TextureRegion getLaserTextureRegion() {
        return laserTextureRegion;
    }

    public TextureRegion getMirrorTextureRegion() {
        return mirrorTextureRegion;
    }

    public TextureRegion getBarrierTextureRegion() {
        return barrierTextureRegion;
    }

    public Font getFont() {
        return font;
    }

    public TextureRegion X3() {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }
}
