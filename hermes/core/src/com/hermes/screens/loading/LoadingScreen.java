package com.hermes.screens.loading;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.hermes.Hermes;
import com.hermes.assets.AssetDescriptors;
import com.hermes.config.GameConfig;
import com.hermes.screens.game.FirstLevelScreenV2;
import com.hermes.util.GdxUtils;

public class LoadingScreen extends ScreenAdapter {
    private Hermes hermes;
    private AssetManager assetManager;
    private Viewport viewport;
    private ShapeRenderer renderer;
    private float progressBar = 0f;
    private boolean flag = false;
    private static final float progressWidth = GameConfig.HUD_WIDTH/3;
    private static final float progressHeight = 20f;
    private static final float progressX = GameConfig.HUD_WIDTH/3;
    private static final float progressY = GameConfig.HUD_HEIGHT/2;


    public LoadingScreen(Hermes hermes) {
        this.hermes = hermes;
        assetManager = hermes.getAssetManager();
    }

    @Override
    public void show() {
        viewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT);
        renderer = new ShapeRenderer();
        assetManager.load(AssetDescriptors.UI_FONT);
    }

    @Override
    public void render(float delta) {
        GdxUtils.clearScreen();
        progressBar = assetManager.getProgress();

        if (assetManager.update())
        {
            flag = true;
        }
        viewport.apply();
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.rect(progressX, progressY, progressWidth*progressBar, progressHeight);
        renderer.end();
        if (flag)
        {
            hermes.setScreen(new FirstLevelScreenV2(null));
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        renderer.dispose();
    }

    @Override
    public void hide() {

    }
}
