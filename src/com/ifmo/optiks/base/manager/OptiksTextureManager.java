package com.ifmo.optiks.base.manager;

import android.graphics.Color;
import android.graphics.Typeface;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.font.FontFactory;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 20.04.12
 */

public class OptiksTextureManager {

    /* Toast Font */
    public final Font font;

    /* Menu Font */
    public final Font menuFont;

    /* Optiks Base Objects */
    public final TextureRegion aimTextureRegion;
    public final TextureRegion laserTextureRegion;
    public final TextureRegion mirrorRectangleTextureRegion;
    public final TextureRegion mirrorCircleTextureRegion;
    public final TextureRegion barrierRectangleTextureRegion;
    public final TextureRegion barrierCircleTextureRegion;

    /* Menu Background */
    public final TextureRegion menuBackgroundTextureRegion;

    /* Menu Item */
    public final TextureRegion menuItemTextureRegion;

    /* Question */
    public final TextureRegion questionTextureRegion;

    /* Seasons Images */
    public final Map<Integer, TextureRegion> seasons;

    public OptiksTextureManager(final BaseGameActivity activity) {

        /* Seasons Images */
        seasons = new HashMap<Integer, TextureRegion>();

        /* Toast font */
        final BitmapTextureAtlas toastFontAtlas = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR);
        font = new Font(toastFontAtlas, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32, true, Color.BLACK);
        activity.getTextureManager().loadTexture(toastFontAtlas);
        activity.getFontManager().loadFont(font);

        /* Optiks Base Objects assets path */
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

        /* Optiks Base Objects */
        final BitmapTextureAtlas bitmapTextureAtlas = new BitmapTextureAtlas(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        barrierRectangleTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(bitmapTextureAtlas, activity, "barrier.png", 0, 0);
        mirrorRectangleTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(bitmapTextureAtlas, activity, "mirror.png", 201, 0);
        aimTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(bitmapTextureAtlas, activity, "aim.png", 0, 101);
        laserTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(bitmapTextureAtlas, activity, "laser.png", 101, 101);
        mirrorCircleTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(bitmapTextureAtlas, activity, "mirror_circle.png", 201, 101);
        barrierCircleTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(bitmapTextureAtlas, activity, "barrier_circle.png", 301, 101);
        activity.getEngine().getTextureManager().loadTexture(bitmapTextureAtlas);

        /* Font assets path */
        FontFactory.setAssetBasePath("font/");

        /* Menu Font */
        final BitmapTextureAtlas menuFontTexture = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        menuFont = FontFactory.createFromAsset(menuFontTexture, activity, "Plok.ttf", 24, true, Color.rgb(255, 140, 0));
        activity.getEngine().getTextureManager().loadTexture(menuFontTexture);
        activity.getFontManager().loadFont(menuFont);

        /* Menu assets path */
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/main/");

        /* Menu Background */
        final BitmapTextureAtlas menuBackgroundAtlas = new BitmapTextureAtlas(1024, 1024, TextureOptions.REPEATING_BILINEAR_PREMULTIPLYALPHA);
        menuBackgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuBackgroundAtlas, activity, "background.jpg", 0, 0);
        activity.getEngine().getTextureManager().loadTexture(menuBackgroundAtlas);

        /* Menu Item */
        final BitmapTextureAtlas menuAtlas = new BitmapTextureAtlas(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        menuItemTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuAtlas, activity, "menu_empty.png", 0, 0);
        activity.getEngine().getTextureManager().loadTexture(menuAtlas);

        /* Season assets path */
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/season/");

        /* Question */
        final BitmapTextureAtlas questionAtlas = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        questionTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(questionAtlas, activity, "question.png", 0, 0);
        activity.getEngine().getTextureManager().loadTexture(questionAtlas);
        seasons.put(-1, questionTextureRegion);
    }
}
