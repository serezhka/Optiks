package com.ifmo.optiks;

import android.database.Cursor;
import android.os.Bundle;
import com.ifmo.optiks.base.menagers.GameSceneManager;
import com.ifmo.optiks.base.menagers.GameSoundManager;
import com.ifmo.optiks.base.menagers.GameTextureManager;
import com.ifmo.optiks.provider.OptiksProviderMetaData;
import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.ui.activity.BaseGameActivity;

/**
 * Author: Aleksey Vladiev (Avladiev2@gmail.com)
 */
public class MainActivity extends BaseGameActivity {
    private static final int CAMERA_WIDTH = 720;
    private static final int CAMERA_HEIGHT = 480;

    private String level;/*"{\"objects\":[" +
            "{\"type\":\"LASER\",\"pY\":2.0,\"rotation\":0.0,\"width\":50,\"height\":50,\"pX\":2.0}," +
            "{\"type\":\"MIRROR\",\"pY\":4.0,\"rotation\":0.0,\"width\":200,\"height\":30,\"pX\":7.0}," +
            "{\"type\":\"MIRROR\",\"pY\":8.0,\"rotation\":90.0,\"width\":100,\"height\":30,\"pX\":15.0}," +
            "{\"type\":\"AIM\",\"pY\":14.0,\"rotation\":0.0,\"width\":50,\"height\":50,\"pX\":21.0}," +
            "{\"type\":\"BARRIER\",\"pY\":5.0,\"rotation\":0.0,\"width\":50,\"height\":50,\"pX\":12.0}" +
            "]}";
*/

    private Camera camera;


    @Override
    protected void onCreate(Bundle pSavedInstanceState) {
        super.onCreate(pSavedInstanceState);
        final int id = getIntent().getExtras().getInt(OptiksProviderMetaData.LevelTable._ID);
        final Cursor cursor = managedQuery(OptiksProviderMetaData.LevelTable.CONTENT_URI, null, "_id=?", new String[]{id + ""}, null);
        cursor.moveToFirst();

        final int idValue = cursor.getColumnIndex(OptiksProviderMetaData.LevelTable.VALUE);
        level = cursor.getString(idValue);

    }

    @Override
    public Engine onLoadEngine() {
        camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        final EngineOptions engineOptions = new EngineOptions(true, EngineOptions.ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera).setNeedsMusic(true).setNeedsSound(true);
        engineOptions.getTouchOptions().setRunOnUpdateThread(true);

        return new Engine(engineOptions);

    }

    private GameTextureManager gameTextureManager;
    private GameSoundManager gameSoundManager;

    @Override
    public void onLoadResources() {
        gameSoundManager = GameSoundManager.getInstance(this);
        gameTextureManager = GameTextureManager.getInstance(this);
    }

    @Override
    public Scene onLoadScene() {
        this.mEngine.registerUpdateHandler(new FPSLogger());
        final GameSceneManager sceneManager = new GameSceneManager(this, gameTextureManager, gameSoundManager);
        return sceneManager.createScene(level);
    }

    @Override
    public void onLoadComplete() {
        mEngine.enableVibrator(this);
    }
}
