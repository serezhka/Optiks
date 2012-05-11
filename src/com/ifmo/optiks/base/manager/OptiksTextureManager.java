package com.ifmo.optiks.base.manager;

import android.graphics.Color;
import android.graphics.Typeface;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.font.FontFactory;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.atlas.bitmap.source.FileBitmapTextureAtlasSource;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import java.io.File;
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
    public final TiledTextureRegion sight;
    public final TiledTextureRegion emptyTexture;

    /* Menu Background */
    public final TextureRegion menuBackgroundTextureRegion;

    /* Menu Item */
    public final TextureRegion menuItemTextureRegion;

    /* Season Select Menu Arrows */
    public final TextureRegion leftArrowTextureRegion;
    public final TextureRegion rightArrowTextureRegion;

    /* Levels Menu Items */
    public final TiledTextureRegion levelsMenuItem;

    /* Seasons Images */
    public final Map<Integer, TextureRegion> seasons;

    /* Settings Menu Items */
    public final TiledTextureRegion musicTextureRegion;
    public final TiledTextureRegion soundTextureRegion;
    public final TiledTextureRegion vibrationTextureRegion;
    /* Activity */
    final BaseGameActivity activity;

    public OptiksTextureManager(final BaseGameActivity activity) {

        this.activity = activity;

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
        mirrorCircleTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(bitmapTextureAtlas, activity, "mirror_circle_NA.png", 602, 0, 1, 1);
        mirrorRectangleTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(bitmapTextureAtlas, activity, "mirror_NA.png", 0, 271, 1, 1);
        barrierRectangleTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(bitmapTextureAtlas, activity, "barrier.png", 401, 271, 1, 1);
        barrierCircleTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(bitmapTextureAtlas, activity, "barrier_circle.png", 602, 271, 1, 1);
        mirrorSplash = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(bitmapTextureAtlas, activity, "mirror_splash.png", 703, 271, 1, 1);
        sight = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(bitmapTextureAtlas, activity, "sight.png", 804, 271, 1, 1);
        emptyTexture = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(bitmapTextureAtlas, activity, "empty_texture.png", 835, 271, 1, 1);
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
        menuBackgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuBackgroundAtlas, activity, "background.png", 0, 0);
        activity.getEngine().getTextureManager().loadTexture(menuBackgroundAtlas);

        /* Menu Item */
        final BitmapTextureAtlas menuAtlas = new BitmapTextureAtlas(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        menuItemTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuAtlas, activity, "menu_empty.png", 0, 0);
        activity.getEngine().getTextureManager().loadTexture(menuAtlas);

        /* Season menu assets path */
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/season/");

        /* Seasons Background */
        final BitmapTextureAtlas seasonBackgroundAtlas = new BitmapTextureAtlas(1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        final TextureRegion seasonBackgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(seasonBackgroundAtlas, activity, "season_background.jpg", 0, 0);
        activity.getEngine().getTextureManager().loadTexture(seasonBackgroundAtlas);
        seasons.put(0, seasonBackgroundTextureRegion);

        /* Season -1 test */
        //addSeasonImageFromFile(-1, new File("/mnt/sdcard/test.png"));

        /* Season Select Menu Arrows */
        final BitmapTextureAtlas arrowsTextureAtlas = new BitmapTextureAtlas(512, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        leftArrowTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(arrowsTextureAtlas, activity, "arrow_left.png", 0, 0);
        rightArrowTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(arrowsTextureAtlas, activity, "arrow_right.png", 256, 0);
        activity.getEngine().getTextureManager().loadTexture(arrowsTextureAtlas);

        /* Levels menu assets path */
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/level/");

        /* Levels menu items */
        final BitmapTextureAtlas levelsMenuTextureAtlas = new BitmapTextureAtlas(256, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        levelsMenuItem = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(levelsMenuTextureAtlas, activity, "level_item_1.png", 0, 0, 2, 1);
        activity.getEngine().getTextureManager().loadTexture(levelsMenuTextureAtlas);

        /* Settings menu assets path */
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/settings/");

        /* Sound & vibrate setting */
        final BitmapTextureAtlas settingsMenuTextureAtlas = new BitmapTextureAtlas(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        soundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(settingsMenuTextureAtlas, activity, "sound.png", 0, 0, 2, 1);
        vibrationTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(settingsMenuTextureAtlas, activity, "vibration.png", 256, 0, 2, 1);
        musicTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(settingsMenuTextureAtlas, activity, "music.png", 0, 256, 2, 1);
        activity.getEngine().getTextureManager().loadTexture(settingsMenuTextureAtlas);
    }

    // TODO this is fuckin' stub for fukin' AndEngine
    public TiledTextureRegion getLevelsMenuItemRegion() {

        final TiledTextureRegion levelsMenuItem;

        /* Levels menu assets path */
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/level/");

        /* Levels menu items */
        final BitmapTextureAtlas levelsMenuTextureAtlas = new BitmapTextureAtlas(256, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        levelsMenuItem = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(levelsMenuTextureAtlas, activity, "level_item_1.png", 0, 0, 2, 1);
        activity.getEngine().getTextureManager().loadTexture(levelsMenuTextureAtlas);

        return levelsMenuItem;
    }

    public void addSeasonImageFromFile(final int seasonId, final File image) {
        final FileBitmapTextureAtlasSource source = new FileBitmapTextureAtlasSource(image);
        final int width = calculateMinSize(source.getWidth());
        final int height = calculateMinSize(source.getHeight());
        final BitmapTextureAtlas textureAtlas = new BitmapTextureAtlas(width, height, TextureOptions.BILINEAR);
        final TextureRegion textureRegion = BitmapTextureAtlasTextureRegionFactory.createFromSource(textureAtlas, source, 0, 0);
        activity.getEngine().getTextureManager().loadTexture(textureAtlas);
        seasons.put(seasonId, textureRegion);
    }

    private int calculateMinSize(final int dimension) {
        int result = 2;
        while (dimension > result) {
            result *= 2;
        }
        return result;
    }
}