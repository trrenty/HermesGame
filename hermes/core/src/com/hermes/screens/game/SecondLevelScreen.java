package com.hermes.screens.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.hermes.HermesHyper;
import com.hermes.assets.ChildrenNames;
import com.hermes.assets.gui.GUIScene;
import com.hermes.config.GameConfig;
import com.hermes.system.passive.InitLevel1System;
import com.hermes.system.passive.InitLevel2;
import com.hermes.system.passive.InitSystem;
import games.rednblack.editor.renderer.utils.ItemWrapper;

public class SecondLevelScreen extends LevelScreen {
    public SecondLevelScreen(HermesHyper game) {
        super(game);
    }

    @Override
    protected void initCameraAndViewports() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(GameConfig.VIEWPORT_WIDTH, GameConfig.VIEWPORT_HEIGHT, camera);
        uiViewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT);
        camera.position.set(GameConfig.VIEWPORT_CENTER_X, GameConfig.VIEWPORT_CENTER_Y, 0);
    }

    @Override
    void setup() {
        sceneLoader.loadScene("Level2", viewport);
        root = new ItemWrapper(sceneLoader.getRoot());
        GUIScene.INSTANCE.create(assetManager, uiViewport, game.getBatch());
        engine.addSystem(new InitLevel2(root, this));
    }

    @Override
    protected void endLevel() {

    }
}
