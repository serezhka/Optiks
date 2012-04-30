package com.ifmo.optiks.base.manager;

import android.graphics.Color;
import android.graphics.Typeface;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.font.FontFactory;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: Sergey Fedorov (serezhka@xakep.ru)
 * Date: 20.04.12
 */

public class OptiksTextureManager {

    /* toast Font */
    public final Font font;

    /* Menu Font */
    public final Font menuFont;

    /* Optiks Base Objects */
    public final TiledTextureRegion aimTextureRegion;
    public final TiledTextureRegion laserTextureRegion;
    public final TiledTextureRegion mirrorRectangleTextureRegion;
    public final TiledTextureRegion mirrorCircleTextureRegion;
    public final TiledTextureRegion barrierRectangleTextureRegion;
    public final TiledTextureRegion barrierCircleTextureRegion;
    public final TiledTextureRegion mirrorSplash;

    /* Menu Background */
    public final TextureRegion menuBackgroundTextureRegion;

    /* Menu Item */
    public final TextureRegion menuItemTextureRegion;

    /* Question */
    public final TextureRegion questionTextureRegion;

    /* Season Select Menu Arrows */
    public final TextureRegion leftArrowTextureRegion;
    public final TextureRegion rightArrowTextureRegion;

    /* Levels Menu Items */
    public final TextureRegion levelsMenuStar;
    public final TextureRegion levelsMenuQuestion;

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
        final BitmapTextureAtlas bitmapTextureAtlas = new BitmapTextureAtlas(1024, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        aimTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(bitmapTextureAtlas, activity, "aim.png", 0, 0, 3, 3);
        laserTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(bitmapTextureAtlas, activity, "laser.png", 301, 0, 3, 3);
        mirrorCircleTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(bitmapTextureAtlas, activity, "mirror_circle.png", 602, 0, 3, 3);
        mirrorRectangleTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(bitmapTextureAtlas, activity, "mirror.png", 0, 271, 2, 6);
        barrierRectangleTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(bitmapTextureAtlas, activity, "barrier.png", 401, 271, 1, 1);
        barrierCircleTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(bitmapTextureAtlas, activity, "barrier_circle.png", 602, 271, 1, 1);
        mirrorSplash = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(bitmapTextureAtlas, activity, "mirror_splash.png", 703, 271, 1, 1);
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

        /* Season menu assets path */
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/season/");

        /* Question */
        final BitmapTextureAtlas questionAtlas = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        questionTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(questionAtlas, activity, "question.png", 0, 0);
        activity.getEngine().getTextureManager().loadTexture(questionAtlas);
        seasons.put(-1, questionTextureRegion);

        /* Seasons Background */
        final BitmapTextureAtlas seasonBackgroundAtlas = new BitmapTextureAtlas(1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        final TextureRegion seasonBackgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(seasonBackgroundAtlas, activity, "season_background.jpg", 0, 0);
        activity.getEngine().getTextureManager().loadTexture(seasonBackgroundAtlas);
        seasons.put(0, seasonBackgroundTextureRegion);

        /* Season Select Menu Arrows */
        final BitmapTextureAtlas arrowsTextureAtlas = new BitmapTextureAtlas(512, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        leftArrowTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(arrowsTextureAtlas, activity, "arrow_left.png", 0, 0);
        rightArrowTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(arrowsTextureAtlas, activity, "arrow_right.png", 256, 0);
        activity.getEngine().getTextureManager().loadTexture(arrowsTextureAtlas);

        /* Levels menu assets path */
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/level/");

        /* Levels menu items */
        final BitmapTextureAtlas levelsMenuTextureAtlas = new BitmapTextureAtlas(512, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        levelsMenuStar = BitmapTextureAtlasTextureRegionFactory.createFromAsset(levelsMenuTextureAtlas, activity, "star.png", 0, 0);
        levelsMenuQuestion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(levelsMenuTextureAtlas, activity, "question.png", 256, 0);
        activity.getEngine().getTextureManager().loadTexture(levelsMenuTextureAtlas);
    }
}
