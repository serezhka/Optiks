//package com.ifmo.optiks.test;


/*
 * Author: Aleksey Vladiev (Avladiev2@gmail.com)


public class MainActivity extends BaseGameActivity {
    private final static String TAG = "MainActivityTAG";
    private static final int CAMERA_WIDTH = 720;
    private static final int CAMERA_HEIGHT = 480;


    private final String level = "[" +
            "{\"bodyForm\":\"CIRCLE\",\"type\":\"LASER\",\"pX\":100.0,\"pY\":400.0,\"rotation\":0.0,\"height\":50.0,\"width\":50.0}," +
            "{\"bodyForm\":\"CIRCLE\",\"type\":\"AIM\",\"pX\":600.0,\"pY\":400.0,\"rotation\":0.0,\"height\":100.0,\"width\":100.0}," +
            "{\"bodyForm\":\"RECTANGLE\",\"type\":\"BARRIER\",\"pX\":200.0,\"pY\":100.0,\"rotation\":0.0,\"height\":50.0,\"width\":400.0}," +
//            "{\"bodyForm\":\"RECTANGLE\",\"type\":\"BARRIER\",\"pX\":200.0,\"pY\":300.0,\"rotation\":0.0,\"height\":50.0,\"width\":250.0}," +
            "{\"canMove\":true,\"canRotate\":true,\"bodyForm\":\"RECTANGLE\",\"type\":\"MIRROR\",\"pX\":200.0,\"pY\":400.0,\"rotation\":0.0,\"height\":50.0,\"width\":250.0}," +
            "{\"bodyForm\":\"CIRCLE\",\"type\":\"BARRIER\",\"pX\":600.0,\"pY\":100.0,\"rotation\":0.0,\"height\":100.0,\"width\":100.0}," +
            "{\"canMove\":true,\"canRotate\":false,\"bodyForm\":\"CIRCLE\",\"type\":\"MIRROR\",\"pX\":500.0,\"pY\":200.0,\"rotation\":0.0,\"height\":100.0,\"width\":100.0}" +
            "]";
    private Camera camera;


protected void onCreate(Bundle pSavedInstanceState) {
        super.onCreate(pSavedInstanceState);
        final int id = getIntent().getExtras().getInt(OptiksProviderMetaData.SeasonsTable._ID);
        final Cursor cursor = managedQuery(OptiksProviderMetaData.SeasonsTable.CONTENT_URI, null, "_id=?", new String[]{id + ""}, null);
        cursor.moveToFirst();

        final int idValue = cursor.getColumnIndex(OptiksProviderMetaData.SeasonsTable.DESCRIPTION);
        level = cursor.getString(idValue);

    }


    @Override
    public Engine onLoadEngine() {
        camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        final EngineOptions engineOptions = new EngineOptions(true, EngineOptions.ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera).setNeedsMusic(true).setNeedsSound(true);
        engineOptions.getTouchOptions().setRunOnUpdateThread(true);

        return new Engine(engineOptions);

    }

    private OptiksTextureManager textureManager;
    private OptiksSoundManager gameSoundManager;

    @Override
    public void onLoadResources() {
        gameSoundManager = new OptiksSoundManager(this);
        textureManager = new OptiksTextureManager(this);
    }

    @Override
    public Scene onLoadScene() {
        this.mEngine.registerUpdateHandler(new FPSLogger());
        return new GameScene(level, this, textureManager, gameSoundManager);

final Scene scene = new Scene();
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


       return scene;


    }

    @Override
    public void onLoadComplete() {
        mEngine.enableVibrator(this);
    }
}*/
