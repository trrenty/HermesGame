package com.hermes.screens.game;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.hermes.HermesHyper;
import com.hermes.assets.gui.GUIScene;
import com.hermes.common.EntityActions;
import com.hermes.common.GameManager;
import com.hermes.config.GameConfig;
import com.hermes.system.*;
import com.hermes.system.debug.DebugCameraSystem;
import com.hermes.system.debug.GridRenderSystem;
import com.hermes.system.debug.StateDebugSystem;
import com.hermes.thread.ThreadManager;
import com.hermes.util.GdxUtils;
import games.rednblack.editor.renderer.SceneLoader;
import games.rednblack.editor.renderer.utils.ItemWrapper;

public abstract class LevelScreen implements Screen {


    // constants

    private static final Logger log = new Logger(FirstLevelScreenV2.class.getName(), Logger.DEBUG);
    public static final Array<float[]> attacks = new Array<>();
    public static final Vector2 followPoint = new Vector2();
    //attributes

    protected final HermesHyper game;

    protected Viewport viewport;
    protected Viewport uiViewport;
    protected PooledEngine engine;
    protected OrthographicCamera camera;
    protected World world;
    protected SceneLoader sceneLoader;
    protected ItemWrapper root;
    protected AssetManager assetManager;
    protected CameraFollowerSystemV2 cameraFollowerSystemV2;
    // debug
    protected Box2DDebugRenderer worldRenderer;
    protected ShapeRenderer renderer;

    // threadStuff
    public static final ThreadManager threadManager = new ThreadManager();

    public ThreadManager getThreadManager() {
        return threadManager;
    }
    // threadStuff


    public LevelScreen(HermesHyper game) {
        this.game = game;
    }


    @Override
    public void show() {
        GameManager.INSTANCE.setRunning();

        initCameraAndViewports();

        worldRenderer = new Box2DDebugRenderer();

        engine = game.getEngine();
        assetManager = game.getAssetManager();
        world = game.getWorld();
        sceneLoader = game.getSceneLoader();

        cameraFollowerSystemV2 = new CameraFollowerSystemV2();
        cameraFollowerSystemV2.setViewportCenter(GameConfig.VIEWPORT_CENTER_X, GameConfig.VIEWPORT_CENTER_Y);

        setup();


        cameraFollowerSystemV2.setGuides(sceneLoader.getSceneVO().horizontalGuides, sceneLoader.getSceneVO().verticalGuides);

        new EntityActions(sceneLoader.getActionFactory());

        renderer = new ShapeRenderer();

//        GridRenderSystem system = new GridRenderSystem(viewport, renderer);
//        system.priority = 10;
//        engine.addSystem(system);
//        engine.addSystem(new DebugCameraSystem(camera, 0, 0));
        engine.addSystem(cameraFollowerSystemV2);

//        engine.addSystem(new CameraFollowerSystem(camera));
        engine.addSystem(new CameraSwitchSystem(camera));
//        engine.addSystem(new DebugWorldSystem(world, viewport, worldRenderer));
        engine.addSystem(new StateDebugSystem(viewport));
        engine.addSystem(new ParallaxSystem(camera));
        engine.addSystem(new AnimationSystem());
        engine.addSystem(new RemoveDeadStuffSystem(sceneLoader));
        engine.addSystem(new DestinationSystem());
        engine.addSystem(new CameraZoomOutSystem(camera));
        engine.addSystem(new StateSystem());
        engine.addSystem(new AnimationRemovalSystem());
        engine.addSystem(new TaskSystem());
        LicuriciSystem licuriciSystem = new LicuriciSystem();
        engine.addEntityListener(LicuriciSystem.FAMILY, licuriciSystem);
        engine.addSystem(licuriciSystem);

        worldRenderer.setDrawAABBs(true);
    }

    protected abstract void initCameraAndViewports();

    abstract void setup();

    @Override
    public void render(float delta) {

        GdxUtils.clearScreen();
        camera.update();

        viewport.apply();
        engine.update(delta);

        uiViewport.apply();
        GUIScene.INSTANCE.render();
        // debug
//        worldRenderer.render(world, camera.combined);
        renderer.setProjectionMatrix(camera.combined);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        for (float[] attack : attacks) {
            renderer.line(attack[0], attack[1], attack[2], attack[3]);
            if (attacks.size > 5) {
                attacks.removeIndex(0);
            }
            renderer.x(followPoint, 1);
        }
        renderer.end();


        if (GameManager.INSTANCE.isLevelDone()) {
            endLevel();
        }
        if (GameManager.INSTANCE.isReset()) {
            setup();
            GameManager.INSTANCE.setRunning();
        }

    }

    protected abstract void endLevel();


    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) return;
        viewport.update(width, height, true);
        uiViewport.update(width, height, true);
        GUIScene.INSTANCE.resize(width, height);
        sceneLoader.resize(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        GUIScene.INSTANCE.dispose();
        worldRenderer.dispose();
//        threadManager.end();
    }

    public SceneLoader getSceneLoader() {
        return sceneLoader;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public CameraFollowerSystemV2 getCameraFollowerSystemV2() {
        return cameraFollowerSystemV2;
    }

    public Viewport getViewport() {
        return viewport;
    }
}
