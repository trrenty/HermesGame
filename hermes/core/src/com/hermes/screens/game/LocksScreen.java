package com.hermes.screens.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.hermes.HermesHyper;
import com.hermes.assets.ChildrenNames;
import com.hermes.assets.gui.GUIScene;
import com.hermes.config.GameConfig;
import com.hermes.system.passive.InitLevel1System;
import games.rednblack.editor.renderer.utils.ItemWrapper;

public class LocksScreen extends LevelScreen {
    private final float viewportWidth = 28;
    private final float viewportHeight = 18;
    public LocksScreen(HermesHyper game) {
        super(game);
    }

    @Override
    protected void initCameraAndViewports() {

        camera = new OrthographicCamera();
        viewport = new FitViewport(viewportWidth, viewportHeight, camera);
        uiViewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT);
        camera.position.set(GameConfig.VIEWPORT_CENTER_X, GameConfig.VIEWPORT_CENTER_Y, 0);
    }

    public void setup() {

        sceneLoader.loadScene("locks", viewport);
        root = new ItemWrapper(sceneLoader.getRoot());
        GUIScene.INSTANCE.create(assetManager, uiViewport, game.getBatch());
        engine.addSystem(new InitLevel1System(root, this));
        cameraFollowerSystemV2.setViewportCenter(viewportWidth / 2, viewportHeight / 2);

    }

    @Override
    protected void endLevel() {

    }
}
