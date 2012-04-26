package com.ifmo.optiks.test;


import com.ifmo.optiks.base.manager.GameScene;
import com.ifmo.optiks.base.manager.GameSoundManager;
import com.ifmo.optiks.base.manager.OptiksTextureManager;
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
    private final static String TAG = "MainActivityTAG";
    private static final int CAMERA_WIDTH = 720;
    private static final int CAMERA_HEIGHT = 480;


    private String level = "[" +
            "{\"bodyForm\":\"CIRCLE\",\"type\":\"LASER\",\"pX\":300.0,\"pY\":100.0,\"rotation\":0.0,\"height\":50.0,\"width\":50.0}," +
            "{\"bodyForm\":\"CIRCLE\",\"type\":\"AIM\",\"pX\":400.0,\"pY\":400.0,\"rotation\":0.0,\"height\":100.0,\"width\":100.0}," +
            "{\"bodyForm\":\"RECTANGLE\",\"type\":\"BARRIER\",\"pX\":100.0,\"pY\":200.0,\"rotation\":0.0,\"height\":100.0,\"width\":200.0}," +
            "{\"canMove\":true,\"canRotate\":true,\"bodyForm\":\"RECTANGLE\",\"type\":\"MIRROR\",\"pX\":200.0,\"pY\":400.0,\"rotation\":0.0,\"height\":75.0,\"width\":250.0}," +
            "{\"bodyForm\":\"CIRCLE\",\"type\":\"BARRIER\",\"pX\":600.0,\"pY\":100.0,\"rotation\":0.0,\"height\":100.0,\"width\":100.0}," +
            "{\"canMove\":true,\"canRotate\":false,\"bodyForm\":\"CIRCLE\",\"type\":\"MIRROR\",\"pX\":500.0,\"pY\":200.0,\"rotation\":0.0,\"height\":100.0,\"width\":100.0}" +
            "]";
    private Camera camera;


    /*protected void onCreate(Bundle pSavedInstanceState) {
        super.onCreate(pSavedInstanceState);
        final int id = getIntent().getExtras().getInt(OptiksProviderMetaData.SeasonsTable._ID);
        final Cursor cursor = managedQuery(OptiksProviderMetaData.SeasonsTable.CONTENT_URI, null, "_id=?", new String[]{id + ""}, null);
        cursor.moveToFirst();

        final int idValue = cursor.getColumnIndex(OptiksProviderMetaData.SeasonsTable.DESCRIPTION);
        level = cursor.getString(idValue);

    }*/

    @Override
    public Engine onLoadEngine() {
        camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        final EngineOptions engineOptions = new EngineOptions(true, EngineOptions.ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera).setNeedsMusic(true).setNeedsSound(true);
        engineOptions.getTouchOptions().setRunOnUpdateThread(true);

        return new Engine(engineOptions);

    }

    private OptiksTextureManager textureManager;
    private GameSoundManager gameSoundManager;

    @Override
    public void onLoadResources() {
        gameSoundManager = GameSoundManager.getInstance(this);
        textureManager = new OptiksTextureManager(this);
    }

    @Override
    public Scene onLoadScene() {
        this.mEngine.registerUpdateHandler(new FPSLogger());
        return new GameScene(level, this, textureManager, gameSoundManager);

        /*final Scene scene = new Scene();
       final PhysicsWorld physicsWorld = new PhysicsWorld(new Vector2(0, 0), false);
       final ConstructorGameScene constructorGameScene = new ConstructorGameScene(this, textureManager, gameSoundManager);

       scene.registerUpdateHandler(physicsWorld);
       constructorGameScene.setScene(scene);
       constructorGameScene.setPhysicsWorld(physicsWorld);
       constructorGameScene.addBarrier(100, 200);
       constructorGameScene.addLaser(300, 100);
       constructorGameScene.addAim(400, 400);
       constructorGameScene.addMirror(600, 400);
       Log.d(TAG, constructorGameScene.getGson());


       return scene;*/

    }

    @Override
    public void onLoadComplete() {
        mEngine.enableVibrator(this);
    }
}
